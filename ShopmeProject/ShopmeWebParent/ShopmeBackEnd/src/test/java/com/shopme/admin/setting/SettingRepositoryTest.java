package com.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {

	@Autowired
	private SettingRepository repository;
	
	@Test
	public void testCreateGeneralSettings() {
		//Setting setting =new Setting("Nom_du_site","AfroShop", SettingCategory.GENERAL);
		Setting settingLogo =new Setting("logo du site","logo.png", SettingCategory.GENERAL);
		Setting copyRigth =new Setting("COPYRIGTH","copyrigth (c) SONORE TANDOUM 2022 LTD.", SettingCategory.GENERAL);
		
		Iterable<Setting> saveAll = repository.saveAll(List.of(settingLogo, copyRigth));
		
		assertThat(saveAll).size().isGreaterThan(0);
	}
	
	@Test
	public void testCreateCurrencySetting() {
		Setting setting = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
		Setting settingSymbole = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
		Setting settingSymbolePosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
		Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
		Setting thousendsAndPointType = new Setting("THOUSAND_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
		
		repository.saveAll(List.of(setting,settingSymbole,settingSymbolePosition,decimalPointType,decimalDigits, thousendsAndPointType));
		
	}
	
	@Test
	public void getSettingByCategory() {
		List<Setting> categories = repository.findByCategory(SettingCategory.GENERAL);
		
		categories.forEach(System.out::println);
	}
	
}
