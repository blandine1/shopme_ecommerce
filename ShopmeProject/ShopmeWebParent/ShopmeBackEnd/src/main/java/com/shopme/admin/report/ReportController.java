package com.shopme.admin.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.setting.Setting;

@Controller
public class ReportController {
	
	@Autowired private MasterOrderReportService masterOrderReportService;
	@Autowired private SettingService settingService;
	
	@GetMapping("/reports")
	public String viewSaleReportHome(HttpServletRequest request) {
		loadCurrencySetting(request);
		return "reports/reports";
	}
	
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySetting = settingService.getCurrencySetting();
		
		for (Setting setting : currencySetting) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
		
	}

}
