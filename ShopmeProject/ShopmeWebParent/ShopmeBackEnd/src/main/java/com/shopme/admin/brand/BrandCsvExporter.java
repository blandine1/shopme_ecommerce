package com.shopme.admin.brand;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Brand;

public class BrandCsvExporter extends AbstractExporter{

	public void export(Iterable<Brand> brands, HttpServletResponse response) throws IOException {
		 super.setResponseHeader(response, "text/csv", ".csv", "brands_");
		 
		 ICsvBeanWriter beanWriter =new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		 
		 String[] header = {"ID", "name"};
		 String[] values = {"id", "name"};
		 //values ici en haut represente les les colonnes dans la table Brand
		 
		 beanWriter.writeHeader(header);
		 
		 for(Brand brand : brands) {
			 brand.setId(brand.getId());
			 brand.setName(brand.getName());
			 //brand.setCategories(brand.getCategories().toString());
			 beanWriter.write(brand, values);
		 }
		 beanWriter.close();
	}
}
