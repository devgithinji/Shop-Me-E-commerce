package com.densoft.shopmeAdmin.user;

import com.densoft.shopmecommon.entity.Role;
import com.densoft.shopmecommon.entity.User;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateUser() {
        Role role = roleRepository.findById(1).get();
        User user = new User("testuser@gmail.com", "password", "test", "user", "photoone.png", true);
        user.addRole(role);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void createUserWithTwoRoles() {
        User user = new User("usertwo@gmail.com", "password", "User", "Two", "usertwo.png", true);
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        user.addRoles(List.of(roleEditor, roleAssistant));
        User savedUser = userRepository.save(user);
        assertThat(userRepository.findByEmail(user.getEmail()).get().getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    public void testListAllUsers() {
        List<User> userList = userRepository.findAll();
        userList.forEach(System.out::println);
        assertThat(userList).isNotNull();
    }

    @Test
    public void testFindUserById() {
        User user = userRepository.findById(1).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User user = userRepository.findById(1).get();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("user@gmail.com");
        User updatedUser = userRepository.save(user);
        assertThat(updatedUser.getEmail()).isEqualTo("user@gmail.com");
    }

    @Test
    public void testUpdateRoles() {
        User user = userRepository.findById(2).get();
        user.getRoles().remove(new Role(3));
        user.getRoles().add(new Role(2));
        User updatedUser = userRepository.save(user);
        System.out.println(updatedUser);
        assertThat(updatedUser.getRoles()).doesNotContain(new Role(3));
    }

    @Test
    public void testDeleteUser() {
        userRepository.deleteById(3);
        assertThat(userRepository.findById(3).isEmpty()).isEqualTo(true);
    }

}