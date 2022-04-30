package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	
   //ici on va gerer un fichier log a partir de la class qui genere ce log
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);
	
	static void deleteExtraImageRemoveInForm(Product product) {
		String imageDir = "../product-images/"+product.getId()+"/extras";
		Path dirPath = Paths.get(imageDir);
		
		try{
			Files.list(dirPath).forEach(file ->{
				String fileName = file.toFile().getName();
				
				if(!product.containImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("deleted extraImage "+ fileName);
					}catch (IOException e) {
						LOGGER.error("could not delete extra image "+ fileName);
					}
				}
			});
		}catch (IOException e) {
			LOGGER.error("could not delete directory "+ dirPath);
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
		  String uploadDir = "../product-images/"+ savedProduct.getId();
		
		  FileUploadUtil.cleanDir(uploadDir);
		  FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultiPart);
		}
		
		if(extraImageMultipart.length > 0) {
			 String uploadDir = "../product-images/"+ savedProduct.getId() + "/extras";
			for(MultipartFile multipartFile : extraImageMultipart) {
				if(multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename().replace(" ", "_"));
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
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
