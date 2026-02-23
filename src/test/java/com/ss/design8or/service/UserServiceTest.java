package com.ss.design8or.service;

import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author ezerbo
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private PoolService poolService;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindByEmailAddress() {
        String email = "test@example.com";
        User user = User.builder()
                .id(1L)
                .emailAddress(email)
                .firstName("Test")
                .lastName("User")
                .build();

        when(userRepository.findByEmailAddress(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmailAddress(email);

        assertThat(result).isNotNull();
        assertThat(result.getEmailAddress()).isEqualTo(email);
        verify(userRepository, times(1)).findByEmailAddress(email);
    }
}
