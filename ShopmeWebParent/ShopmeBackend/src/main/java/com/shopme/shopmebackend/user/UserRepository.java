package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

interface UserRepository extends PagingAndSortingRepository<User,Integer>, CrudRepository<User,Integer> {

    @Query("select u from User u where  u.email=:email")
    public User getUserByEmail(@Param("email") String email);

    public Long countById(Integer id);

    @Query("Select  u From User u where upper(concat(u.id,' ',u.email,' ',u.firstName,' ',u.lastName))   LIKE upper(concat('%', ?1,'%')) ")
    public Page<User> findAll(String keyword, Pageable pageable);

    @Query("update User u set u.enabled = ?2 where u.id = ?1")
    @Modifying
    public void updateEnableStatus(Integer id,boolean enabled);





}
