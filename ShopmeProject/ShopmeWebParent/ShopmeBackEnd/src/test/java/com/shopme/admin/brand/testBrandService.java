package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Brand;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class testBrandService {

	@MockBean
	private BrandsRepository repository;
	
	@InjectMocks
	private BrandService brandService;
	
	
	@Test
	public void chechUniqueInNewMode() {
		Integer id = null;
		String name = "Acer";
		
		Brand brand =new Brand(name);
		
		Mockito.when(repository.findByName(name)).thenReturn(brand);
		
		String unique = brandService.checKUnique(id, name);
		
		assertThat(unique).isEqualTo("Duplicate");
	}
	
	@Test
	public void testDuplicateOnNewModeOk() {
		Integer id = null;
		String name = "AMD";
		//Brand brand = new Brand(id, name);
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		
		String findByName = brandService.checKUnique(id, name);
		assertThat(findByName).isEqualTo("ok");
	}
	
	@Test
	public void tsetDuplicateInEditModeReturnDuplicate() {
		//ici on 
		Integer id= 9;
		String name = "Apple";
		//initialisation permet en fait de recrer un Brand dont le id est celui du Name deja existant en bd
		Brand brand = new Brand(id, name);
		
		Mockito.when(repository.findByName(name)).thenReturn(brand);
		
		String checUnique = brandService.checKUnique(8, "Apple");
		assertThat(checUnique).isEqualTo("Duplicate");
	}
	
	@Test
	public void TestDuplicateBrandInEditModeOk(){
		//dans ce test on essaie de mettre a jour un Brand avec un id deja existant
		Integer id = 9;
		String name = "AMD";
		
		Brand brand = new Brand(id, name);
		
		Mockito.when(repository.findByName(name)).thenReturn(brand);
		
		String unique = brandService.checKUnique(id, name);
		
		assertThat(unique).isEqualTo("ok");
	}
	
	
}
