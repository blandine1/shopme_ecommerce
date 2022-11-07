package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {


	@Test
	public void passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String pass="mike2020";
		String encodepass = bCryptPasswordEncoder.encode(pass);

		System.out.println(encodepass);

	    boolean match =	bCryptPasswordEncoder.matches(pass, encodepass);

	    assertThat(match).isTrue();
	}
}
