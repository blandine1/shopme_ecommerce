package com.shopme.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository settingRepository;
	

   public  List<Setting> getGeneralSetting() {
	   return settingRepository.findByTwoCtagories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	   
   }
   
   public EmailSettingBag getEmailSettings() {
	   List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	   settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
	   
	   return new EmailSettingBag(settings);
   }

}
