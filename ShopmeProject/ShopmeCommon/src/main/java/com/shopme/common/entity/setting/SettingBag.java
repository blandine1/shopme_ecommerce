package com.shopme.common.entity.setting;

import java.util.List;

public class SettingBag {

	private List<Setting> listSettings;

	public SettingBag(List<Setting> listSettings) {
		this.listSettings = listSettings;
	}
	
	//on recupere un setting a partir du key
	public Setting get(String key) {
		int index = listSettings.indexOf(new Setting(key));
		
		System.out.println("la valeur de l'index : " +index);
		
		if(index >=0) {
			return listSettings.get(index);
		}
		
		return null;
	}
	
	//on recupere un setting a partir du value
	public String getValue(String key) {
		Setting setting = get(key);
		if(setting != null) {
			return setting.getValue();
		}
		return null;
	}
	
	//mettre a jour un seting
	public void updateSetting(String key, String value) {
		Setting setting = get(key);
		if(setting != null && value != null) {
			setting.setValue(value);
		}
	}
	
	//on retourne la liste des setting
	public List<Setting> list(){
		return listSettings;
	}
	
}
