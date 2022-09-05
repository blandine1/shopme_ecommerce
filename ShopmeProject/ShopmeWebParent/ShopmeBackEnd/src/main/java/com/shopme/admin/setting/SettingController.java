package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;

@Controller
public class SettingController {

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();
		
		model.addAttribute("listCurrencies", listCurrencies);
		
		for(Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		return "settings/settings";
	}
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSetting(@RequestParam("fileImage") MultipartFile multipartFile,HttpServletRequest request
			,RedirectAttributes attributes) throws IOException {
		
		GeneralSettingBag settingBag = settingService.getGeneralSetting();
		
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);
		
       
		updateSettingValuesFromForm(request, settingBag.list());
		
		attributes.addFlashAttribute("message", "general setting has been saved");
		
		return "redirect:/settings";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag generalSetting) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename().replace(" ", "_"));
			String value = "/site-logo/"+fileName;
			
			generalSetting.updateSiteLogo(value);
			
			String uploadDir = "../site-logo";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}
	
	//on enreritre l(initial de la monaie
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		
		Optional<Currency> findByIDResult = currencyRepository.findById(currencyId);
		
		if (findByIDResult.isPresent()) {
			Currency currency = findByIDResult.get();
			settingBag.updateCurrencySymmbolValue(currency.getSymbol());
		}
	}
	
	@PostMapping("settings/save_payment")
	public String savePamentSettings(Model model, HttpServletRequest request, RedirectAttributes attributes) {
		List<Setting> paymentSettings = settingService.getPaymentSetting();
		updateSettingValuesFromForm(request, paymentSettings);
		
		attributes.addFlashAttribute("message", "payment have been saved");
		
		return "redirect:/settings#payment";
	}
	
	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSetting) {
		for(Setting setting : listSetting) {
			String value= request.getParameter(setting.getKey());
			if(value != null) {
				setting.setValue(value);
			}
		}
		settingService.saveAll(listSetting);
	}
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes attributes) {
		List<Setting> mailServerSetting = settingService.getMailServerSetting();
		
		updateSettingValuesFromForm(request, mailServerSetting);
		
		attributes.addFlashAttribute("message", "Mail server setting is successfully saved");
		
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_mail_tepmlates")
	public String saveMailTemplatesSetting(HttpServletRequest request, RedirectAttributes attributes) {
		 List<Setting> templatesSetting = settingService.getMailTemplatesSetting();
		 
		 updateSettingValuesFromForm(request, templatesSetting);
		 
		 attributes.addFlashAttribute("message", "Mail Template setting is successfully saved");
		
		return "redirect:/settings";
	}
}
