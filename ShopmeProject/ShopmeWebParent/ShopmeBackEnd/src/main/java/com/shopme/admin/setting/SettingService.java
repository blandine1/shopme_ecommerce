package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository settingRepository;
	
	
   public List<Setting> listAllSettings(){
	   return (List<Setting>) settingRepository.findAll();
   }
   
   public GeneralSettingBag getGeneralSetting() {
	   List<Setting> settings = new ArrayList<>();
	   
	   List<Setting> generalSetting = settingRepository.findByCategory(SettingCategory.GENERAL);
	   List<Setting> currencySetting = settingRepository.findByCategory(SettingCategory.CURRENCY);
	   
	   settings.addAll(generalSetting);
	   settings.addAll(currencySetting);
	   
	   
	   return new GeneralSettingBag(settings);
	   
   }
   
   //enregistrer les parametres du site
   public void saveAll(Iterable<Setting> settings) {
	   settingRepository.saveAll(settings);
   }
   
   public List<Setting> getMailServerSetting(){
	   return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
   }
   
   public List<Setting> getMailTemplatesSetting(){
	   return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
   }
   
   public List<Setting> getCurrencySetting(){
	   return settingRepository.findByCategory(SettingCategory.CURRENCY);
   }
   
   public List<Setting> getPaymentSetting() {
	   return settingRepository.findByCategory(SettingCategory.PAYMENT);
   }
   
   

}
