package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {

	//on retourne la liste de setting d'apres une certaines categorie
	public List<Setting> findByCategory(SettingCategory category);
}
