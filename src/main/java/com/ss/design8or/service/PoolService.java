package com.ss.design8or.service;

import com.ss.design8or.model.*;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.response.GetPoolsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Optional<User> getCurrentLead() {
        return userRepository.findByLeadTrue();
    }

    public Pool getCurrent() {
        return poolRepository.findOneByStatus(PoolStatus.STARTED)
                .filter(pool -> pool.getAssignments().size() < userRepository.count())
                .orElseGet(() -> {
                    log.info("No current pool available or pool is full, creating new one...");
                    return poolRepository.save(new Pool());
                });
    }

    public GetPoolsResponse getPools() {
        Pool currentPool = getCurrent();
        long assignmentsCount = currentPool.getAssignments().size();
        return GetPoolsResponse.builder()
                .pools(poolRepository.findAllByOrderByStartDateDesc())
                .currentLead(userRepository.findByLeadTrue().orElse(null))
                .progress(calculateProgress(assignmentsCount))
                .participantsCount(assignmentsCount)
                .build();
    }

    public List<User> getCurrentPoolCandidates() {
        List<User> participants = getParticipants();
        return userRepository.findAll()
                .stream()
                .filter(Predicate.not(participants::contains))
                .toList();
    }

    private List<User> getParticipants() {
        List<User> participants = new ArrayList<>(getCurrent()
                .getAssignments()
                .stream()
                .map(Assignment::getUser)
                .toList());
        designationRepository.findOneByStatusNotIn(List.of(DesignationStatus.ACCEPTED))
                .map(Designation::getUser)
                .ifPresent(participants::add);
        return participants;
    }

    private long calculateProgress(long participantsCount) {
        long usersCount = userRepository.count();
        return usersCount == 0 ? 0 : 100 * (participantsCount / usersCount);
    }

}
