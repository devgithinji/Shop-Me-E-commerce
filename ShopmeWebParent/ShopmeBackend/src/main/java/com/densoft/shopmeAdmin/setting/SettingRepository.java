package com.densoft.shopmeAdmin.setting;


import com.densoft.shopmecommon.SettingCategory;
import com.densoft.shopmecommon.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

}
