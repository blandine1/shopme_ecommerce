package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testCreateFirstUser() {
		Role getRole = entityManager.find(Role.class, 1);
		User userDivine = new User("divine@gmail.com", "divine21","Divine", "Noella");
		userDivine.addRole(getRole);
		User savedUser =repository.save(userDivine);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateUserWithTwoRoles() {
		User userRavi = new User("ravi@gamil.com", "ravi2020", "Ravi", "Kumar");
		Role roleEditor = new Role(4);
		Role roleAssistant = new Role(5);
		userRavi.addRole(roleEditor);
		userRavi.addRole(roleAssistant);
		
		User savedUser = repository.save(userRavi);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAll() {
		Iterable<User> listUsers= repository.findAll();
		listUsers.forEach(user ->System.out.println(user));
	}
	
	@Test
	public void testGetSingleUser() {
		//Integer userId = 1;
		User userId1 = repository.findById(1).get();
		System.out.println("single user "+ userId1);
		
		assertThat(userId1).isNotNull();
	}
	
	@Test
	public void testUpdateUserInfo() {
		User userId = repository.findById(1).get();
		userId.setEnabled(true);
		
		repository.save(userId);
		
		assertThat(userId.isEnabled()).isEqualTo(true);
	}
	
	@Test
	public void testUpdateUserRole() {
		User userRavi = repository.findById(2).get();
		Role roleShipper = new Role(4);
		userRavi.getRoles().remove(roleShipper);
		Role roleEditor = new Role(3);
		userRavi.addRole(roleEditor);
		 repository.save(userRavi);
	}
	
	@Test
	public void testDeleteUser() {
		Integer uId = 2;
		repository.deleteById(uId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email= "divine@gmail.com";
		
		User getUser = repository.getUserByEmail(email);
		
		assertThat(getUser).isNotNull();
	}
	
	
	
	
	
}
