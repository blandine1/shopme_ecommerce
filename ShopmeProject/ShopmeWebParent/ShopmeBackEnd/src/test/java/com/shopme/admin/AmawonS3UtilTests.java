package com.shopme.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AmawonS3UtilTests {

	
	@Test
	public void testListFolder() {
		String folderName = "test-upload";
		List<String> listFolder = AmazonS3Util.listFolder(folderName);
		listFolder.forEach(System.out:: println);
	}
	
	@Test
	public void testUploadFileToAmazonS3() throws FileNotFoundException {
		
		String folderName = "test-upload";
		String fileName = "contrat.docx";
		String filePath = "E:\\" + fileName;
		
		InputStream inputStream = new FileInputStream(filePath);
		
		AmazonS3Util.uploadFile(folderName, fileName, inputStream);
	}
	
	@Test
	public void testDEleteFile() {
		String fileName = "test-upload/contrat.docx";
		AmazonS3Util.deleteFile(fileName);
	}
	
	@Test
	public void testRemoveFoler() {
		
		String folderName = "test-upload";
		AmazonS3Util.removeFolder(folderName);
	}
	
}
