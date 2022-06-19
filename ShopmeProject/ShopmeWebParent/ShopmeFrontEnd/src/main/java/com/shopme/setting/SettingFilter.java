package com.shopme.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.Setting;

@Component
public class SettingFilter implements Filter {

	@Autowired
	private SettingService service;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		
		
		if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jfif")) {
			chain.doFilter(request, response);
			return;
		}
		
		List<Setting> settings = service.getGeneralSetting();
		settings.forEach(setting ->{
			
			System.out.println(setting);
			
			request.setAttribute(setting.getKey(), setting.getValue());
		});
		
		chain.doFilter(request, response);
	}

}
