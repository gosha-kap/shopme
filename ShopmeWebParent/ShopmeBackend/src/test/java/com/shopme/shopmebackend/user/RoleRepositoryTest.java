package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.Role;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Collections;
import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;

    @Test
    public void testCreateFirstRole() {
        Role role = new Role("Admin", "manage everything");
        Role savedRole = repository.save(role);
        assertThat(savedRole.getId(), greaterThan(0));
    }
    @Test
    public void testCreateRestRoles(){
        Role roleSalesperson = new Role("Salesperson","manage product price," +
                "customers,shipping,orders sales and reports");
        Role roleEditor = new Role("Editor","manage categories,brands," +
                "products,articles and menus");
        Role roleShipper = new Role("Shipper","view products,view orders and update" +
                "order status");
        Role roleAssistant = new Role("Assistant","manage questions and reviews");
        repository.saveAll(List.of(roleAssistant,roleEditor,roleShipper,roleSalesperson));
    }
}