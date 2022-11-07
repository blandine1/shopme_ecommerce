package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider autProvider = new DaoAuthenticationProvider();
        autProvider.setUserDetailsService(userDetailsService());
        autProvider.setPasswordEncoder(passwordEncoder());

        return autProvider;

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	   http.authorizeRequests()
	       .antMatchers("/states/list_by_country/**").hasAnyAuthority("Admin","Salesperson")
	       
	       .antMatchers("/users/**", "/settings/**", "/states/**","/countries/**").hasAuthority("Admin")
	       
	       .antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin","Editor")
	       
	       .antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
	       
	       .antMatchers("/products/edit/**", "/products/save", "/products/checkUnique").hasAnyAuthority("Admin", "Editor", "Salesperson")
	       
	       .antMatchers("/products", "/products/", "/products/page/**", "/products/details/**")
	          .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
	          
	       .antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
	       
	       .antMatchers("/orders","/orders/", "/orders/page/**","/orders/detail/**").hasAnyAuthority("Admin", "Salesperson","Shipper")
	       
	       .antMatchers("/products/detail/**", "/customers/detail/**").hasAnyAuthority("Admin", "Editor", "Salesperson","Assistant")
	       
	       .antMatchers("/customer/**", "/orders/**", "/get_shipping_cost", "/reports/**").hasAnyAuthority("Admin", "Salesperson")
	       
	       .antMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
	       
	       .antMatchers("/reviews/**").hasAnyAuthority("Admin", "Assistant")
	       
	       .anyRequest()
	       .authenticated()
	       .and()
	       .formLogin()
	          .loginPage("/login")
	          .usernameParameter("email")
	          .permitAll()
	          .and()
	          .logout()
	          .permitAll()
	          .and()
	          .rememberMe()
	             .key("AbcDEFghIjkLmnOpqrSTuvwXyM_123123233")
	             .tokenValiditySeconds(7 * 24 * 60 * 60);
	   http.headers().frameOptions().sameOrigin();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}



}
