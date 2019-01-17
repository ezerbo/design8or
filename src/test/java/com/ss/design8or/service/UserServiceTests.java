package com.ss.design8or.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ss.design8or.error.EmailAddressInUseException;
import com.ss.design8or.error.UserNotFoundException;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.UserRepository;

/**
 * @author ezerbo
 *
 */
@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserServiceTests {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Autowired
	private UserRepository repository;
	
	private UserService service;
	
	@Before
	public void init() {
		service = new UserService(repository);
	}
	
	@Test
	public void createThrowsEmailInUseException() {
		expectedException.expect(EmailAddressInUseException.class);
		expectedException.expectMessage("Email address 'chopper.tonytony@onepiece.com' is already in use");
		User user = User.builder()
				.emailAddress("chopper.tonytony@onepiece.com")
				.build();
		service.create(user);
	}
	
	@Test
	public void createAddsANewUser() {
		User user = User.builder()
				.firstName("NewUser")
				.lastName("NewUser")
				.emailAddress("newuser.newuser@onepiece.com")
				.build();
		service.create(user);
		assertThat(repository.findByEmailAddress("newuser.newuser@onepiece.com")).isPresent();
	}
	
	@Test
	public void deleteThrowsUserNotFoundException() {
		expectedException.expect(UserNotFoundException.class);
		expectedException.expectMessage("No user found with identifier: '0'");
		service.delete(0l);
	}
	
	@Test
	public void deleteRemovesUser() {
		assertThat(repository.findById(3l)).isPresent();
		service.delete(3l);
		assertThat(repository.findById(3l)).isNotPresent();
	}
	
	@Test
	public void getAllReturnsAllUsers() {
		assertThat(service.getAll()).hasSize(5);
	}
	
	@Test
	public void getCurrentPoolCandidatesReturnAllCandidates() {
		assertThat(service.getCurrentPoolCandidates()).hasSize(3); //One Lead and one designated
	}
	
	@Test
	public void getcurrentLeadReturnLead() {
		assertThat(service.getCurrentLead().get().getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
	}
	
	@Test
	public void mapReturnsCorrectMapping() throws Exception {
		Method map = UserService.class.getDeclaredMethod("map", User.class, User.class);
		map.setAccessible(true);
		User to = new User();
		User from = User.builder()
				.firstName("Tintin")
				.lastName("Tintin")
				.emailAddress("tintin@ezerbo.com")
				.build();
		User user = (User) map.invoke(service, from, to);
		assertThat(user.getEmailAddress()).isEqualTo(from.getEmailAddress());
		assertThat(user.getFirstName()).isEqualTo(from.getFirstName());
		assertThat(user.getLastName()).isEqualTo(from.getLastName());
		
		assertThat(user.getEmailAddress()).isEqualTo(to.getEmailAddress());
		assertThat(user.getFirstName()).isEqualTo(to.getFirstName());
		assertThat(user.getLastName()).isEqualTo(to.getLastName());
	}

}