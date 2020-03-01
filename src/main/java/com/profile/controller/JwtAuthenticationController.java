package com.profile.controller;

import com.profile.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.profile.service.JwtUserDetailsService;


import com.profile.config.JwtTokenUtil;

import java.util.List;

@RestController
@Api(value = "Account Management System", description = "Operations pertaining to profile information")
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@ApiOperation(value = "Add address")
	@RequestMapping(value = "/addAddress", method = RequestMethod.POST)
	public void addAddress(@RequestBody AddressDTO address) throws Exception {

		userDetailsService.addAddress(address.getUsername(), address.getAddress());
	}

	@ApiOperation(value = "retrieve inventories given username")
	@RequestMapping(value = "/getAddress", method = RequestMethod.GET)
	public List<?> getAddress(@ApiParam(value = "given username", required = true)@RequestParam String username) throws Exception {
		return userDetailsService.getAddress(username);
	}

	@ApiOperation(value = "Authenticate user with provided username and password")
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	@ApiOperation(value = "allow user to register using email, username, and password.")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		return ResponseEntity.ok(userDetailsService.saveNewUser(user));
	}

	@ApiOperation(value = "Modify password, given user credential and newpassword")
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@RequestBody UserDTO user, @ApiParam(value = "new password", required = true)@RequestParam String newPassword) throws Exception {
		return ResponseEntity.ok(userDetailsService.changePassword(user, newPassword));
	}


	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}