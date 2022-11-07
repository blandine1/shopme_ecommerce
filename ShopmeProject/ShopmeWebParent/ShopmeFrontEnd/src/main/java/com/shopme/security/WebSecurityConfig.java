package com.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shopme.security.oauth.CustomerAouth2UserService;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomerAouth2UserService aouth2UserService;
	
	@Autowired
	private OAuth2LoginSuccessHandler auth2LoginSuccessHandler;
	
	@Autowired
	private DatabaseSuccessLoginHandler dbLoginHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	   http.authorizeRequests()
	         .antMatchers("/account_details", "/update_account_details", "/orders/**", "/cart", "/address_book/**", "/checkout",
	        		      "/place_order","/reviews/**" ,"/process_paypal_order", "/write_review/**", "/post_review").authenticated()
	         .anyRequest().permitAll()
	         .and()
	         .formLogin()
	            .loginPage("/login")
	            .usernameParameter("email")
	            .successHandler(dbLoginHandler)
	            .permitAll()
	         .and()
	         .oauth2Login()
	            .loginPage("/login")
	            .userInfoEndpoint()
	            .userService(aouth2UserService)
	            .and()
	            .successHandler(auth2LoginSuccessHandler)
	         .and()
	         .logout().permitAll()
	         .and()
	         .rememberMe()
	           .key("945matrimoni&le_aniBersAoirE261rispa-KclboMipaser2bimiRaTTps")
	           .tokenValiditySeconds(20 * 24 * 60 * 60)
	           .and()
	             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
	         ;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailService();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		
		return provider;
	}



}
