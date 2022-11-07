package com.shopme;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfing implements WebMvcConfigurer {

    /* cette classe est cree pour rendre visible les images sur le navigateur */

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	
//		exposeDirectory("../category-image", registry);
//		exposeDirectory("../brand-logo", registry);
//		exposeDirectory("../product-images", registry);
//		exposeDirectory("../site-logo", registry);
//			
//			/*//l'ancienne methode avant la refactorisation 
//			 * String brandLogoDirName = "../brand-logo"; 
//			 * Path brandPath = Paths.get(brandLogoDirName); 
//			 * String brandImagePath = brandPath.toFile().getAbsolutePath();
//			 * registry.addResourceHandler("/brand-logo/**").addResourceLocations("file:/"+ brandImagePath + "/");
//			 */
//	}
//	
//	private void exposeDirectory(String pathPathern, ResourceHandlerRegistry registry) {
//		Path path = Paths.get(pathPathern);
//		String absolutePath = path.toFile().getAbsolutePath();
//		
//		String logicalPath = pathPathern.replace("../", "") + "/**";
//		
//		registry.addResourceHandler(logicalPath).addResourceLocations("file:/"+ absolutePath + "/");
//	}


}
