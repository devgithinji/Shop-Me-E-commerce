package com.densoft.shopmeAdmin.setting;

import com.densoft.shopmecommon.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, String> {

}
