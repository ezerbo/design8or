package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.*;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.model.enums.PoolStatus;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    private final DesignationRepository designationRepository;

    public Pool getCurrent() {
        return poolRepository.findOneByStatus(PoolStatus.STARTED)
                .filter(pool -> pool.getAssignments().size() < userRepository.count())
                .orElseGet(() -> {
                    log.info("No current pool available or pool is full, creating new one...");
                    return poolRepository.save(new Pool());
                });
    }

    public Page<Pool> findAll(Pageable pageable) {
        return poolRepository.findAllByOrderByStartDateDesc(pageable);
    }

    public Optional<User> getLead(long poolId) {
        return poolRepository.findById(poolId)
                .filter(Predicate.not(Pool::hasEnded))
                .map(pool -> pool.getAssignments().stream()
                        .max(Comparator.comparing(Assignment::getAssignmentDate)))
                .map(assignment -> assignment.map(Assignment::getUser))
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found or has ended"));
    }

    public List<User> getParticipants(long poolId) {
        return poolRepository.findById(poolId)
                .map(pool -> pool.getAssignments().stream()
                        .map(Assignment::getUser).toList())
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
    }

    public List<User> getCandidates(long poolId) {
        Optional<User> designatedUser = getDesignatedUser();
        List<User> participants = getParticipants(poolId);
        return userRepository.findAll()
                .stream()
                .filter(Predicate.not(participants::contains))
                .filter(Predicate.not(user -> designatedUser.map(user::equals).orElse(false)))
                .toList();
    }

    public List<User> getCurrentPoolCandidates() {
        return getCandidates(getCurrent().getId());
    }

    private Optional<User> getDesignatedUser() {
        return designationRepository.findOneByStatusNotIn(List.of(DesignationStatus.ACCEPTED))
                .map(Designation::getUser);
    }

}
