package com.profile.service;

import java.util.ArrayList;
import java.util.List;

import com.profile.dao.AddressDao;
import com.profile.model.DAOAddress;
import com.profile.config.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.profile.dao.UserDao;
import com.profile.model.DAOUser;
import com.profile.model.UserDTO;

import javax.validation.constraints.Email;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	private EmailUtil emailUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}
	
	public DAOUser saveNewUser(UserDTO user) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmailAddress(user.getEmailAddress());
		emailUtil.sendEmail(user.getEmailAddress(), user.getUsername());


		return userDao.save(newUser);
	}



	public DAOUser changePassword(UserDTO user, String newPassword) {
		DAOUser existingUser = userDao.findByUsername(user.getUsername());
		existingUser.setPassword(bcryptEncoder.encode(newPassword));
		return userDao.save(existingUser);
	}

	public DAOAddress addAddress(String username, String address) {
		DAOAddress newAddress = new DAOAddress();
		newAddress.setUsername(username);
		newAddress.setAddress(address);
		return addressDao.save(newAddress);
	}

	public List<String> getAddress(String username) {
		List<DAOAddress> user = addressDao.findByUsername(username);
		List<String> result = new ArrayList<>();
		for(DAOAddress temp : user) {
			result.add(temp.getAddress());
		}
		return result;
	}
}