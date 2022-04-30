package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

	/*
	 * cette classe est pour importer les images dans l'application
	 */
	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
      Path uploadPath = Paths.get(uploadDir);
      //on cree le repertoir sic'est absent
      if (!Files.exists(uploadPath)) {
		Files.createDirectories(uploadPath);
	  }
      //on enregistre  notre image actuelement
      try(InputStream inputStream=multipartFile.getInputStream()){
    	  Path filePath = uploadPath.resolve(fileName);
    	  Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
      }catch (IOException e) {
		throw new IOException("could not create directory with file "+fileName, e);
	}
	}

	//on suppriùe d'abord le fichier s'il existe avant d'enregostrer la nouvelle
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);

		try {
			Files.list(dirPath).forEach(file -> {
				 if(!Files.isDirectory(file)) {
					 try {
						 Files.delete(file);
					 }catch (IOException ex) {
						 LOGGER.error("could not delete file : "+ file);
						// System.out.print();
					}
				 }
		       });
		  } catch (IOException ex) {
			  LOGGER.error("could not list directory : "+dirPath);
			  //System.out.print("could not list directory : "+dirPath);
	        }
     }

	public static void removeDir(String dir) {
		cleanDir(dir);
		
		try {
			Files.delete(Paths.get(dir));
		} catch (IOException e) {
			LOGGER.error("could not remove directory "+dir);
		}
		
	}

}
