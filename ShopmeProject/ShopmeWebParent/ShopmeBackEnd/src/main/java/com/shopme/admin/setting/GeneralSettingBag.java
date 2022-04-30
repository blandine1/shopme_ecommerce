package com.shopme.admin.setting;

import java.util.List;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}
	
	public void updateCurrencySymmbolValue(String value) {
	   super.updateSetting("CURRENCY_SYMBOL", value);	
	}
	
	public void updateSiteLogo(String logo) {
		super.updateSetting("SITE_LOGO", logo);
	}

}
