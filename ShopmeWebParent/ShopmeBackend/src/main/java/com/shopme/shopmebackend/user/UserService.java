package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listUsers(){
        return userRepository.findAll();
    }

    public List<Role> listRoles(){
        return roleRepository.findAll();
    }

    public void save(User user) {
        encodePass(user);
        userRepository.save(user);
    }

    private void encodePass(User user){
        String encoided = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoided);
    }

    public boolean isEmailUnique(String email){
       return Objects.isNull(userRepository.getUserByEmail(email));
    }
}
