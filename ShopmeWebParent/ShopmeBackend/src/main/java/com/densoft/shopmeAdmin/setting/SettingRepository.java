package com.densoft.shopmeAdmin.setting;


import com.densoft.shopmecommon.entity.setting.SettingCategory;
import com.densoft.shopmecommon.entity.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

}
