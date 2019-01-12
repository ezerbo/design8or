package com.ss.design8or.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
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
	
	private AssignmentRepository assignmentRepository;
	
	private NotificationService notificationService;
	
	public DesignationService(PoolRepository poolRepository, UserRepository userRepository,
			AssignmentRepository assignmentRepository, NotificationService notificationService) {
		this.poolRepository = poolRepository;
		this.userRepository = userRepository;
		this.assignmentRepository = assignmentRepository;
		this.notificationService = notificationService;
	}
	
	/**
	 * Designate user as lead
	 * @param user
	 */
	public User designate(User user) {
		userRepository.findByLeadTrue().map(u -> userRepository.save(u.lead(false)));
		user = userRepository.findById(user.getId())
			.orElseThrow(() -> new RuntimeException()); //TODO throw user-not-found exception
		user.setLead(true);
		userRepository.save(user);
		AssignmentId assignmentId = AssignmentId.builder()
				.poolId(getCurrentPool().getId())
				.userId(user.getId())
				.build();
		Assignment assignment = Assignment.builder()
				.id(assignmentId)
				.build();
		assignmentRepository.save(assignment);
		notificationService.emitDesignationEvent(user);
		return user;
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
			notificationService.notifyPoolCreation(pool);
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
	
}