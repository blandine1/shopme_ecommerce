package com.shopme.admin.currency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.setting.CurrencyRepository;
import com.shopme.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace= Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

	@Autowired
	private CurrencyRepository repository;
	
	@Test
	public void testCreateCurrency() {
		Currency currency1 = new Currency("united State dollar", "$", "USD");
		Currency currency2 = new Currency("EURO", "€", "EUR");
		Currency currency3 = new Currency("BRAZILIAN REAL", "R$", "BRL");
		Currency currency4 = new Currency("BRITISH POUND", "£", "GBP");
		Currency currency5 = new Currency("USTRALIA DOLLAR", "$", "AUD");
		Currency currency6 = new Currency("CANADIAN DOLLAR", "$", "CAD");
		
		Iterable<Currency> saveAll = repository.saveAll(List.of(currency1, currency2, currency3, currency4, currency5, currency6));
		
		assertThat(saveAll).size().isGreaterThan(0);
	}
	
	
	@Test
	public void testFindAllCurrency() {
		
		List<Currency> findAllByOrderByNameAsc = repository.findAllByOrderByNameAsc();
		
		findAllByOrderByNameAsc.forEach(c ->{
			System.out.println(c.getId());
		});
		
		assertThat(findAllByOrderByNameAsc).size().isGreaterThan(0);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
