package com.ss.design8or.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ss.design8or.error.DesignationNotFoundException;
import com.ss.design8or.error.InvalidDesignationResponseException;
import com.ss.design8or.error.UserNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationResponse;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.notification.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@Transactional
public class DesignationService {
	
	private final static String ACCEPT_RESPONSE = "accept";
	private final static String DECLINE_RESPONSE = "decline";
	
	private PoolRepository poolRepository;
	private UserRepository userRepository;
	private DesignationRepository designationRepository;
	private NotificationService notificationService;
	private AssignmentService assignmentService;
	
	public DesignationService(PoolRepository poolRepository, UserRepository userRepository,
			DesignationRepository designationRepository, NotificationService notificationService,
			AssignmentService assignmentService) {
		this.poolRepository = poolRepository;
		this.userRepository = userRepository;
		this.designationRepository = designationRepository;
		this.notificationService = notificationService;
		this.assignmentService = assignmentService;
	}
	
	/**
	 * Designate user as lead
	 * @param user
	 */
	public User designate(User user) {
		final long userId = user.getId();
		user = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException(userId));
		designationRepository.findCurrent().map(d -> designationRepository.save(d.reassign())); //<--- reassign() sets status to 'REASSINGED' and nullify token
		Designation designation =  designationRepository.save(Designation.builder().user(user).build());
		notificationService.emitDesignationEvent(designation);
		return user;
	}
	
	public Designation processDesignationResponse(DesignationResponse designationResponse) {
		log.info("Processing designation response {}", designationResponse);
		Designation designation = getDesignationfromResponse(designationResponse);
		if(ACCEPT_RESPONSE.equalsIgnoreCase(designationResponse.getResponse())) {
			Assignment assignment = assignmentService.create(designation.accept().getUser(), getCurrentPool());
			notificationService.emitAssignmentEvent(assignment);
		} else {
			notificationService.emitDesignationDeclinationEvent(designation.decline());//Broadcast to all users
		}
		return designationRepository.save(designation.token(null));
	}
	
	private Designation getDesignationfromResponse(DesignationResponse designationResponse) {
		validateResponse(designationResponse.getResponse());
		User user = userRepository.findByEmailAddress(designationResponse.getEmailAddress())
				.orElseThrow(() -> new UserNotFoundException(designationResponse.getEmailAddress()));
		Designation designation = user.getDesignations().stream()
				.filter(d -> d.isPending() && Objects.equals(d.getToken(), designationResponse.getToken())).findAny()
				.orElse(designationRepository.findCurrentAndDeclined()); //If user has not been designated, find a designation that's current and has been declined
		if(Objects.isNull(designation)) {
			throw new DesignationNotFoundException(user.getEmailAddress());
		}
		return designation;
	}
	
	private Pool getCurrentPool() {
		Optional<Pool> poolOp = poolRepository.findCurrent();
		if(poolOp.isPresent()) {
			return poolOp.get();
		}
		return poolRepository.save(new Pool());
	}
	
	/**
	 * Designate new lead
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
					.sorted((u, v) -> u.getLastName().compareTo(v.getLastName()))
					.collect(Collectors.toList());
			notificationService.emitPoolCreationEvent(pool); //<--- Broadcast pool creation event
		}
		User lead = designate(candidates.get(0));
		log.info("The next lead will be: {}", lead.getEmailAddress());
		return lead;
	}
	
	private void validateResponse(String response) {
		if(!(ACCEPT_RESPONSE.equalsIgnoreCase(response) || DECLINE_RESPONSE.equalsIgnoreCase(response))) {
			throw new InvalidDesignationResponseException(response);
		}
	}
	
}