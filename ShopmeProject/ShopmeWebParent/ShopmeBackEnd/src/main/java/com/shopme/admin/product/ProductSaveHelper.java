package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.product.ProductImage;

public class ProductSaveHelper {
	
   //ici on va gerer un fichier log a partir de la class qui genere ce log
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);
	
	static void deleteExtraImageRemoveInForm(Product product) {
		String imageDir = "product-images/"+product.getId()+"/extras";
        List<String> listObjetKeys = AmazonS3Util.listFolder(imageDir);		
		
          for(String objectKey: listObjetKeys) {
        	  int lastIndexOfSlash = objectKey.lastIndexOf("/");
        	  String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());
        	  
        	  if(!product.containImageName(fileName)) {
        		  AmazonS3Util.deleteFile(objectKey);
        		  System.out.print("deleted extra image : " + objectKey);
        	  }
          }
	}

	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		
		if(imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for(int count =0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
		
	}

	//<Brand> jhuste ôur les signe inferieur et supperieurs
	static void setProductDetails(String[] detailIDs,String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length <0 ) return;
		
		for(int count =0 ;count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			//System.out.println("nammmmmmmmmmmm "+name);
			//System.out.println("valueeeeeeeeeeee "+ value);
			
			if(id != 0) {
				product.addDetails(id, name, value);
			}else if(!name.isEmpty() && !value.isEmpty()) {
				product.addDetails(name, value);
			}
		}
	}

	static void saveUploadImages(MultipartFile mainImageMultiPart, MultipartFile[] extraImageMultipart,
			Product savedProduct) throws IOException {
		
		if(!mainImageMultiPart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultiPart.getOriginalFilename().replace(" ", "_"));
		    String uploadDir = "product-images/"+ savedProduct.getId();
		    
		    List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir + "/");
		    for(String objectKey : listObjectKeys) {
		    	if(!objectKey.contains("/extras/")) {
		    		AmazonS3Util.deleteFile(objectKey);
		    	}
		    }
		  
		 
	      AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultiPart.getInputStream());
		  
		  //FileUploadUtil.cleanDir(uploadDir);
		  //FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultiPart);
		}
		
		if(extraImageMultipart.length > 0) {
			 String uploadDir = "product-images/"+ savedProduct.getId() + "/extras";
			for(MultipartFile multipartFile : extraImageMultipart) {
				if(multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename().replace(" ", "_"));
				 AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
				
				 //FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
	
	}

	static void setMainImageName(MultipartFile mainImageMultiPart, Product product) {
		if(!mainImageMultiPart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultiPart.getOriginalFilename().replace(" ", "_"));
			product.setMainImage(fileName);
		}
	}
	
	static void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
		if(extraImageMultipart.length > 0) {
			for(MultipartFile multipartFile : extraImageMultipart) {
				if(!multipartFile.isEmpty()) {
					String extName = StringUtils.cleanPath(multipartFile.getOriginalFilename().replace(" ", "_"));
					
					if(!product.containImageName(extName)) {
						product.addExtraImages(extName);
					}
					
				}
			}
		}
	}
	
}
