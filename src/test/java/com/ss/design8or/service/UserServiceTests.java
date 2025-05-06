package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceInUseException;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.controller.request.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

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

	@Autowired
	private DesignationRepository designationRepository;
	
	private UserService service;
	
	@BeforeEach
	public void init() {
		service = new UserService(repository, designationRepository);
	}
	
	@Test
	public void createThrowsEmailInUseException() {
		UserRequest request = UserRequest.builder()
				.emailAddress("chopper.tonytony@onepiece.com")
				.build();
		ResourceInUseException exception = assertThrows(ResourceInUseException.class,
				() -> service.create(request));
		assertThat(exception.getMessage())
				.isEqualTo("Email address already in use");
	}
	
	@Test
	public void createAddsANewUser() {
		UserRequest request = UserRequest.builder()
				.firstName("NewUser")
				.lastName("NewUser")
				.emailAddress("newuser.newuser@onepiece.com")
				.build();
		service.create(request);
		assertThat(repository.findByEmailAddress("newuser.newuser@onepiece.com")).isPresent();
	}
	
	@Test
	public void deleteThrowsUserNotFoundException() {
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> service.delete(0L));
		assertThat(exception.getMessage()).isEqualTo("User with id 0 not found");
	}
	
	@Test
	public void deleteRemovesUser() {
		assertThat(repository.findById(3L)).isPresent();
		service.delete(3L);
		assertThat(repository.findById(3L)).isNotPresent();
	}
	
	@Test
	public void findAllReturnsAllUsers() {
		assertThat(service.findAll(PageRequest.of(0, 5))).hasSize(5);
	}

}