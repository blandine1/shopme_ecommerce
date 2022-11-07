package com.shopme.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;


public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String prefix) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timeStemp = dateFormat.format(new Date());
		String fileNAme = prefix + timeStemp + extension;

		response.setContentType(contentType);

		String headerKey = "Content-Disposition";
		String headreValue = "attachment; filename="+fileNAme;
		response.setHeader(headerKey, headreValue);
     }
}
