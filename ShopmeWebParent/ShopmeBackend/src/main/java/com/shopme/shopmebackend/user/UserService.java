package com.shopme.shopmebackend.user;

import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class UserService {
    public static final int USERS_FOR_PAGE = 4;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USERS_FOR_PAGE, sort);
        if (keyword != null)
            return userRepository.findAll(keyword, pageable);
        return userRepository.findAll(pageable);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public User save(User user) {
        boolean isUpdate = (user.getId() != null);
        if (isUpdate) {
            User updatedUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            } else {
                encodePass(user);
            }
        } else
            encodePass(user);
        return userRepository.save(user);
    }

    private void encodePass(User user) {
        String encoided = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoided);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User user = userRepository.getUserByEmail(email);
        if (Objects.isNull(user))
            return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (user != null) return false;
        } else {
            if (user.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("could not find user with id = " + id + ".");
        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("could not find user with id = " + id + ".");
        }
        userRepository.deleteById(id);
    }

    public void updateUserEnableStatus(Integer id, boolean enabled) {
        userRepository.updateEnableStatus(id, enabled);
    }

    public List<User> listAll() {
        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }
}
