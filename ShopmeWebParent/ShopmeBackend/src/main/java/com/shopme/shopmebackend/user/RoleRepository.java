package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
}
