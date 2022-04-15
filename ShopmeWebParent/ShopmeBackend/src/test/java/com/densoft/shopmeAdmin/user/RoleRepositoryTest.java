package com.densoft.shopmeAdmin.user;

import com.densoft.shopmecommon.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole() {
        Role roleAdmin = new Role("Admin", "Manage everything");
        Role savedRole = roleRepository.save(roleAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles() {
        Role roleSalesPerson = new Role("Sales Person", "Manage product price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor", "Manage categories, brands, products, articles and menus");
        Role roleShipper = new Role("Shipper", "View Products, view orders and update order status");
        Role roleAssistant = new Role("Assistant", "manage questions and reviews");
        List<Role> newRoles = List.of(roleSalesPerson, roleEditor, roleShipper, roleAssistant);
        List<Role> roles = roleRepository.saveAll(newRoles);
        assertThat(roles.size()).isEqualTo(newRoles.size());
    }
}