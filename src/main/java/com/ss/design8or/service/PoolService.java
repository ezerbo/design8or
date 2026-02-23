package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolService {

    private final PoolRepository poolRepository;

    private final UserRepository userRepository;

    private final AssignmentRepository assignmentRepository;

    public Pool getCurrent() {
        return poolRepository.findAll().stream()
                .filter(Pool::isActive)
                .filter(pool -> assignmentRepository.countByPoolAndDesignationStatus(pool, DesignationStatus.ACCEPTED) < userRepository.count())
                .findFirst()
                .orElseGet(() -> {
                    log.info("No current pool available or pool is full, creating new one...");
                    return poolRepository.save(new Pool());
                });
    }

    public Page<Pool> findAll(Pageable pageable) {
        return poolRepository.findAllByOrderByStartDateDesc(pageable);
    }

    public Optional<User> getLead(long poolId) {
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        if (!pool.isActive()) {
            throw new IllegalStateException("Pool has ended");
        }

        return assignmentRepository.findFirstByPoolAndDesignationStatusOrderByRespondedAtDesc(pool, DesignationStatus.ACCEPTED)
                .map(Assignment::getUser);
    }

    public List<User> getParticipants(long poolId) {
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        return assignmentRepository.findByPoolAndDesignationStatus(pool, DesignationStatus.ACCEPTED).stream()
                .map(Assignment::getUser)
                .toList();
    }

    public Page<User> getParticipants(long poolId, Pageable pageable) {
        List<User> allParticipants = getParticipants(poolId);
        return paginateList(allParticipants, pageable);
    }

    public List<User> getCandidates(long poolId) {
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        Optional<User> designatedUser = getDesignatedUser(pool);
        List<User> participants = getParticipants(poolId);

        return userRepository.findAll()
                .stream()
                .filter(Predicate.not(participants::contains))
                .filter(Predicate.not(user -> designatedUser.map(user::equals).orElse(false)))
                .toList();
    }

    public Page<User> getCandidates(long poolId, Pageable pageable) {
        List<User> allCandidates = getCandidates(poolId);
        return paginateList(allCandidates, pageable);
    }

    public List<User> getCurrentPoolCandidates() {
        return getCandidates(getCurrent().getId());
    }

    public List<Assignment> getAcceptedAssignments(long poolId) {
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        return assignmentRepository.findByPoolAndDesignationStatus(pool, DesignationStatus.ACCEPTED).stream()
                .sorted(Comparator.comparing(Assignment::getRespondedAt).reversed())
                .toList();
    }

    public Pool startNewPool() {
        log.info("Starting new pool...");

        // Find and end current active pool
        poolRepository.findAll().stream()
                .filter(Pool::isActive)
                .forEach(pool -> {
                    pool.setEndDate(new java.util.Date());
                    poolRepository.save(pool);
                    log.info("Ended pool with id {}", pool.getId());
                });

        // Create new pool
        Pool newPool = new Pool();
        newPool = poolRepository.save(newPool);
        log.info("Created new pool with id {}", newPool.getId());

        return newPool;
    }

    public void deletePool(long poolId) {
        log.info("Deleting pool with id {}...", poolId);

        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));

        if (pool.isActive()) {
            throw new IllegalStateException("Cannot delete active pool");
        }

        poolRepository.delete(pool);
        log.info("Deleted pool with id {}", poolId);
    }

    private Page<User> paginateList(List<User> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start > list.size()) {
            return new PageImpl<>(List.of(), pageable, list.size());
        }

        List<User> pageContent = list.subList(start, end);
        return new PageImpl<>(pageContent, pageable, list.size());
    }

    private Optional<User> getDesignatedUser(Pool pool) {
        return assignmentRepository.findFirstByPoolAndDesignationStatusOrderByRespondedAtDesc(pool, DesignationStatus.ACCEPTED)
                .map(Assignment::getUser);
    }
}
