package com.ss.design8or.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.stereotype.Service;

import com.ss.design8or.error.DesignationNotFoundException;
import com.ss.design8or.error.InvalidDesignationResponseException;
import com.ss.design8or.error.UserNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationResponse;
import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@Transactional
public class DesignationService {
	
	private PoolRepository poolRepository;
	private UserRepository userRepository;
	private DesignationRepository designationRepository;
	private NotificationService notificationService;
	private BasicTextEncryptor basicTextEncryptor;
	private AssignmentRepository assignmentRepository;
	
	public DesignationService(PoolRepository poolRepository, UserRepository userRepository,
			DesignationRepository designationRepository, NotificationService notificationService,
			AssignmentRepository assignmentRepository, BasicTextEncryptor basicTextEncryptor) {
		this.poolRepository = poolRepository;
		this.userRepository = userRepository;
		this.designationRepository = designationRepository;
		this.notificationService = notificationService;
		this.assignmentRepository = assignmentRepository;
		this.basicTextEncryptor = basicTextEncryptor;
	}
	
	/**
	 * Designate user as lead
	 * @param user
	 */
	public User designate(User user) {
		final long userId = user.getId();
		designationRepository.findByCurrentTrue()
			.map(d -> designationRepository.save(d.status(DesignationStatus.REASSIGNED).current(false)));
		user = userRepository.findById(user.getId())
				.orElseThrow(() -> new UserNotFoundException(userId));
		Designation designation = Designation.builder().user(user).build();
		designationRepository.save(designation);
		notificationService.emitDesignationEvent(designation);
		return user;
	}
	
	public Designation processDesignationResponse(DesignationResponse designationResponse) {
		log.info("Processing designation response");
		validateResponse(designationResponse.getResponse());
		String emailAddress = basicTextEncryptor.decrypt(designationResponse.getToken());
		User user = userRepository.findByEmailAddress(emailAddress)
				.orElseThrow(() -> new UserNotFoundException(emailAddress));
		Designation designation = user.getDesignations().stream()
				.filter(d -> d.isCurrent()).findAny()
				.orElse(designationRepository.findCurrentAndDeclined()); //If user has not been designated, find a designation that's current and has been declined
		if(Objects.isNull(designation)) {
			throw new DesignationNotFoundException();
		}
		
		if("accept".equalsIgnoreCase(designationResponse.getResponse())) {
			designation.current(false).status(DesignationStatus.ACCPTED);//
			Assignment assignment = createAssignment(user);
			notificationService.emitAssignmentEvent(assignment);
		} else {
			designation.status(DesignationStatus.DECLINED);
			notificationService.emitDesignationDeclinationEvent(designation);//Broadcast to all users
		}
		return designationRepository.save(designation);
	}
	
	private Assignment createAssignment(User user) {
		userRepository.findByLeadTrue().map(u -> userRepository.save(u.lead(false)));
		userRepository.save(user.lead(true));
		AssignmentId assignmentId = AssignmentId.builder()
				.poolId(getCurrentPool().getId())
				.userId(user.getId())
				.build();
		Assignment assignment = Assignment.builder()
				.id(assignmentId)
				.build();
		return assignmentRepository.save(assignment);
	}
	
	/**
	 * Designate new lead
	 */
	public User designate() {
		log.info("Designating lead...");
		List<User> candidates = userRepository.getAssignmentCandidates();
		if(candidates.isEmpty()) {
			log.info("Current pool ended, starting a new one");
			poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));//complete current pool
			Pool pool = poolRepository.save(new Pool());//start new pool
			//Each user in the entire user base can be assigned a task
			candidates = userRepository.findAll().stream()
					.sorted((u, v) -> u.getLastName().compareTo(v.getLastName()))
					.collect(Collectors.toList());
			notificationService.sendPoolCreationEventAsWebSocketMessage(pool);
		}
		User lead = designate(candidates.get(0));
		log.info("Designated lead: {}", lead.getEmailAddress());
		return lead;
	}
	
	private Pool getCurrentPool() {
		Optional<Pool> poolOp = poolRepository.findCurrent();
		if(poolOp.isPresent()) {
			return poolOp.get();
		}
		return poolRepository.save(new Pool());
	}
	
	private void validateResponse(String response) {
		if(!("accept".equalsIgnoreCase(response) || "decline".equalsIgnoreCase(response))) {
			throw new InvalidDesignationResponseException(response);
		}
	}
	
}