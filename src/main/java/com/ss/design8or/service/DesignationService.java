package com.ss.design8or.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ss.design8or.error.exception.DesignationNotFoundException;
import com.ss.design8or.error.exception.InvalidDesignationResponseException;
import com.ss.design8or.error.exception.UserNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.notification.NotificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DesignationService {
	
	private final static String ACCEPT_RESPONSE = "accept";
	private final static String DECLINE_RESPONSE = "decline";
	
	private final PoolRepository poolRepository;

	private final UserRepository userRepository;

	private final DesignationRepository designationRepository;

	private final NotificationService notificationService;

	private final AssignmentService assignmentService;

	
	/**
	 * Designate user as lead.
	 * 
	 * @param user User to be designated
	 * 
	 * @return Designated user
	 */
	public User designate(User user) {
		final long userId = user.getId();
		user = userRepository.findById(user.getId())
				.orElseThrow(() -> new UserNotFoundException(userId));
		designationRepository.findCurrent().map(Designation::reassign).map(d -> designationRepository.save(d)); //<--- reassign() sets status to 'REASSINGED' and nullify token
		Designation designation = Designation.builder()
				.user(user)
				.build();
		designation =  designationRepository.save(designation);
		notificationService.emitDesignationEvent(designation);
		return user;
	}
	
	/**
	 * Process a designation response. Events are emitted is all cases (designation accepted, declined)
	 * 
	 * @param designationResponse Designation response to be processed
	 * 
	 * @return Processed designation
	 */
	public Designation processDesignationResponse(DesignationResponse designationResponse) {
		log.info("Processing designation response {}", designationResponse);
		Designation designation = getDesignationFromResponse(designationResponse);
		if(ACCEPT_RESPONSE.equalsIgnoreCase(designationResponse.getResponse())) {
			Assignment assignment = assignmentService.create(designation.accept().getUser(), getCurrentPool());
			designation.token(null);
			notificationService.emitAssignmentEvent(assignment);
		} else if(!designation.isDeclined()) { // If designation has not already been declined. Designation might have previously been declined.
			designation.decline();//TODO Do not set designation status to declined when a request is stale and has not been declined by the user it was assigned to
			List<User> candidates = userRepository.getCurrentPoolCandidates();
			notificationService.emitDesignationEvent(designation, candidates);//Broadcast to all users
		}
		return designationRepository.save(designation);
	}
	
	/**
	 * Finds the designation request related to the token from the response.
	 * If the records is not found (can happen when a request have been broadcasted and a user other than the one designated accepts it)
	 * a designation that is current and has been declined is returned.
	 * 
	 * @param designationResponse Response to parse designation from
	 * 
	 * @return Designation
	 */
	private Designation getDesignationFromResponse(DesignationResponse designationResponse) {
		validateResponse(designationResponse.getResponse());
		User user = userRepository.findByEmailAddress(designationResponse.getEmailAddress())
				.orElseThrow(() -> new UserNotFoundException(designationResponse.getEmailAddress()));
		Designation designation = user.getDesignations().stream()
				.filter(d -> d.isPending() && Objects.equals(d.getToken(), designationResponse.getToken())).findAny() //TODO include filter to SQL Query
				.orElse(designationRepository.findStaleOrDeclined()); //If user has not been designated, find a designation that's current and has been declined
		if(Objects.isNull(designation)) {
			throw new DesignationNotFoundException(); // When users click on designations that have already been accepted.
		}
		designation.setUser(user); //User whose email address is in the response.
		return designation;
	}
	
	public Designation getCurrent() {
		return designationRepository.findCurrent()
				.orElseThrow(DesignationNotFoundException::new);
	}
	
	/**
	 * Returns the ongoing pool.
	 * 
	 * @return Current Pool
	 */
	private Pool getCurrentPool() {
		Optional<Pool> poolOp = poolRepository.findCurrent();
		return poolOp.orElseGet(() -> poolRepository.save(new Pool()));
	}
	
	
	/**
	 * Designates the next lead. Typically called by a scheduled Job
	 * The pool of candidates is sorted alphabetically and the first user is chosen.
	 * 
	 * @return Designated user
	 */
	public User designate() {
		log.info("Designating next lead...");
		List<User> candidates = userRepository.getCurrentPoolCandidates();
		if(candidates.isEmpty()) {
			log.info("Current pool ended, starting a new one");
			poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));//complete current pool
			Pool pool = poolRepository.save(new Pool());//start new pool
			//Each user in the entire user base can be assigned a task
			candidates = userRepository.findAll().stream()
					.sorted(Comparator.comparing(User::getLastName))
					.toList();
			notificationService.emitPoolCreationEvent(pool); //<--- Broadcast pool creation event
		}
		User lead = designate(candidates.get(0));
		log.info("The next lead will be: {}", lead.getEmailAddress());
		return lead;
	}
	
	/**
	 * Validates that the designation has either been accepted or declined
	 * 
	 * @param response Designation response to be validated
	 */
	private void validateResponse(String response) {
		if(!(ACCEPT_RESPONSE.equalsIgnoreCase(response) || DECLINE_RESPONSE.equalsIgnoreCase(response))) {
			throw new InvalidDesignationResponseException(response);
		}
	}
	
}