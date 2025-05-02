package com.ss.design8or.service;

import com.ss.design8or.error.exception.EmailAddressInUseException;
import com.ss.design8or.error.exception.UserNotFoundException;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author ezerbo
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserServiceTests {
	
	@Autowired
	private UserRepository repository;
	
	private UserService service;
	
	@BeforeEach
	public void init() {
		service = new UserService(repository);
	}
	
	@Test
	public void createThrowsEmailInUseException() {
		CreateUserRequest request = CreateUserRequest.builder()
				.emailAddress("chopper.tonytony@onepiece.com")
				.build();
		EmailAddressInUseException exception = assertThrows(EmailAddressInUseException.class,
				() -> service.create(request));
		assertThat(exception.getMessage())
				.isEqualTo("Email address 'chopper.tonytony@onepiece.com' is already in use");
	}
	
	@Test
	public void createAddsANewUser() {
		CreateUserRequest request = CreateUserRequest.builder()
				.firstName("NewUser")
				.lastName("NewUser")
				.emailAddress("newuser.newuser@onepiece.com")
				.build();
		service.create(request);
		assertThat(repository.findByEmailAddress("newuser.newuser@onepiece.com")).isPresent();
	}
	
	@Test
	public void deleteThrowsUserNotFoundException() {
		UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.delete(0L));
		assertThat(exception.getMessage()).isEqualTo("No user found with identifier: '0'");
	}
	
	@Test
	public void deleteRemovesUser() {
		assertThat(repository.findById(3L)).isPresent();
		service.delete(3L);
		assertThat(repository.findById(3L)).isNotPresent();
	}
	
	@Test
	public void findAllReturnsAllUsers() {
		assertThat(service.findAll()).hasSize(5);
	}
	
//	@Test
//	public void getCurrentPoolCandidatesReturnAllCandidates() {
//		assertThat(service.getCurrentPoolCandidates()).hasSize(3); //One Lead and one designated
//	}
	
	@Test
	public void getcurrentLeadReturnLead() {
		assertThat(service.getCurrentLead()).isPresent();
		assertThat(service.getCurrentLead().get().getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
	}

}