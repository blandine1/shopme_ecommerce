package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserServices {

	public static final int USERS_PER_PAGE = 4;

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User getUserByEmail(String email) {
		return repository.getUserByEmail(email);
	}

	public List<User> listAll() {
		return (List<User>) repository.findAll(Sort.by("firstName").ascending());
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);

		if(keyword != null) {
			return repository.findAll(keyword, pageable);
		}
		return repository.findAll(pageable);
	}

	public List<Role> listRoles(){
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);

		if(isUpdatingUser) {
			//allons en bd recuperer l'utilisateur qui q cet idetifiant
			User existingUser = repository.findById(user.getId()).get();

			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}else {
				encodePasword(user);
			}
		}else {
			encodePasword(user);
		}

		 return repository.save(user);
	}

	public User updateAccount(User userInform) {
		User userInDb = repository.findById(userInform.getId()).get();

		if(!userInform.getPassword().isEmpty()) {
			userInDb.setPassword(userInform.getPassword());
			encodePasword(userInDb);
		}

		if(userInform.getPhotos() != null) {
			userInDb.setPhotos(userInform.getPhotos());
		}

		userInDb.setFirstName(userInform.getFirstName());
		userInDb.setLastName(userInform.getLastName());

		return repository.save(userInDb);
	}

	private void encodePasword(User user) {
		String passwordEncoded = passwordEncoder.encode(user.getPassword());
		user.setPassword(passwordEncoded);
	}

	//ici si ca retourne true ca signifie que cet email n'existe pas encore en bd
	public boolean isEmailUnique(Integer id, String email) {
		User userEmail = repository.getUserByEmail(email);

		if(userEmail == null) return true;

        boolean isCreatingUser = (id == null);
        if(isCreatingUser) {
        	if(userEmail != null) return false;
        }else {
        	if(userEmail.getId() != id) {
        		return false;
        	}
        }
		return true;
	}

	public User get(Integer id) throws UserNotFountException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFountException("no user found with the given ID "+id);
		}
	}

	public void deleteById(Integer id) throws UserNotFountException {
		Long countById=repository.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFountException("no user found with the given ID "+id);
		}
		repository.deleteById(id);
	}

	public void updateUserStatus(Integer id, boolean enabled) {
		 repository.updateUserStatus(id, enabled);
	}

}
