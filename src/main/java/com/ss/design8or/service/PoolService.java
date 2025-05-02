package com.ss.design8or.service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.PoolStatus;
import com.ss.design8or.rest.response.CurrentPoolStats;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.DesignationResource;
import com.ss.design8or.rest.response.GetPoolsResponse;
import com.ss.design8or.rest.response.PoolDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolService {

    private final PoolRepository poolRepository;

    private final UserRepository userRepository;

    public Optional<User> getCurrentLead() {
        return userRepository.findByLeadTrue();
    }

    //TODO Handle case where there's no lead for a newly created pool
    public CurrentPoolStats getCurrentPoolStats() {
        Pool pool = getCurrent();
        long assignmentsCount = pool.getAssignments().size();
        return CurrentPoolStats.builder()
                .count(poolRepository.count())
                .pool(PoolDTO.builder()
                        .id(pool.getId())
                        .progress(calculateProgress(assignmentsCount))
                        .participantsCount(assignmentsCount)
                        .startDate(pool.getStartDate())
                        .endDate(pool.getEndDate())
                        .lead(userRepository.findByLeadTrue()
                                .map(user -> DesignationResource.UserDTO.builder()
                                        .firstName(user.getFirstName())
                                        .lastName(user.getLastName())
                                        .emailAddress(user.getEmailAddress())
                                        .build()).orElse(null))
                        .build())
                .build();
    }

    public Pool getCurrent() {
        return poolRepository.findOneByStatus(PoolStatus.STARTED)
                .filter(pool -> pool.getAssignments().size() < userRepository.count())
                .orElseGet(() -> {
                    log.info("No current pool available or pool is full, creating new one...");
                    return poolRepository.save(new Pool());
                });
    }

    public GetPoolsResponse getPastPools() {
        Pool currentPool = getCurrent();
        long assignmentsCount = currentPool.getAssignments().size();
        return GetPoolsResponse.builder()
                .current(currentPool)
                .past(poolRepository.findPast(PageRequest.of(0, 2)).getContent())
                .progress(calculateProgress(assignmentsCount))
                .participantsCount(assignmentsCount)
                .build();
    }

    public List<User> getCurrentPoolCandidates() {
        List<User> participants = getCurrent()
                .getAssignments()
                .stream()
                .map(Assignment::getUser)
                .toList();
        return userRepository.findAll()
                .stream()
                .filter(Predicate.not(participants::contains))
                .toList();
    }

    private long calculateProgress(long participantsCount) {
        long usersCount = userRepository.count();
        return usersCount == 0 ? 0 : 100 * (participantsCount / usersCount);
    }

}
