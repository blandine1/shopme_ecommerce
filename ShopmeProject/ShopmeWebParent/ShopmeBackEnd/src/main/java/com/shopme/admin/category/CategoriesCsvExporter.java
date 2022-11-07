package com.shopme.admin.category;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Category;

public class CategoriesCsvExporter extends AbstractExporter {

	
	public void export(List<Category> categories, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "text/csv", ".csv", "categories_");
		
		ICsvBeanWriter beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"ID", "name"};
		String[] csvFileMapping = {"id", "name"};
		
		beanWriter.writeHeader(csvHeader);
		
		for(Category c : categories) {
			c.setName(c.getName().replace("--", "  "));
			beanWriter.write(c, csvFileMapping);
		}
		
		beanWriter.close();
		
	}
}
