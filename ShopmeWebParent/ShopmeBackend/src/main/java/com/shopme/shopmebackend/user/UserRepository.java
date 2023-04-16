package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserRepository extends JpaRepository<User,Integer> {

    @Query("select u from User u where  u.email=:email")
    public User getUserByEmail(@Param("email") String email);
}