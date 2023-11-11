package org.bot0ff.service;

import org.bot0ff.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findOne(Long userId);
    Long findIdByUsername(String username);
    Boolean existsByUsername(String username);
    void saveUser(User user);
}
