package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;


import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreatUser() {
        Role adminRole = entityManager.find(Role.class, 7);
        User user = new User("test@mail.ru", "3453", "firstname", "lastname");
        user.addRole(adminRole);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId(), greaterThan(0));
    }

    @Test
    public void testCreatUserWithRoles() {
        User user = new User("test2@mail.ru", "3453", "firstname2", "lastname2");
        Role edirotRole = new Role(9);
        Role assistantRole = new Role(8);
        user.addRole(edirotRole);
        user.addRole(assistantRole);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId(), greaterThan(0));
    }

    @Test
    public void testListAllUsers() {
        List<User> userList = (List<User>) userRepository.findAll();
        userList.forEach(System.out::println);
        assertThat(userList.size(), greaterThan(0));
    }

    @Test
    public void testGetUserById() {
        User user = userRepository.findById(1).get();
        System.out.println(user.getEmail());
        assertThat(user, notNullValue());
    }

    @Test
    public void updateUser() {
        User user = userRepository.findById(1).get();
        user.setEnabled(true);
        user.setEmail("changed@mail.ru");
        User updatedUser = userRepository.save(user);
        assertThat(updatedUser.isEnabled(), is(true));

    }

    @Test
    public void updateRole() {
        User user = userRepository.findById(2).get();
        user.getRoles().remove(new Role(9));
        user.addRole(new Role(11));
        userRepository.save(user);
    }

    @Test
    public void deleteUser() {
        userRepository.deleteById(2);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "test2@mail.ru";
        User user = userRepository.getUserByEmail(email);
        assertThat(user, notNullValue());
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long countById = userRepository.countById(id);
        assertThat(countById, notNullValue());
        assertThat(countById, greaterThan(0L));
    }

    @Test
    public void testDisable() {
        Integer id = 1;
        userRepository.updateEnableStatus(1, false);
    }

    @Test
    public void testPageList() {
        int pageNum = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);
    }

    @Test
    public void testSearch(){
        String keyword = "bruce";
        int pageNum = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<User> page = userRepository.findAll(keyword, pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);
        assertThat(listUsers.size(),greaterThan(0));
    }
}
