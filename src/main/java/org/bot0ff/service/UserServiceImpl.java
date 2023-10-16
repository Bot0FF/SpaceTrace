package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import org.bot0ff.entity.User;
import org.bot0ff.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findOne(Long userId) {
        return userRepository.findById(userId).orElseThrow(IllegalAccessError::new);
    }

    @Override
    public Long findIdByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(IllegalAccessError::new);
        return user.getId();
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
