package org.botoff.service;

import lombok.RequiredArgsConstructor;
import org.botoff.entity.User;
import org.botoff.repository.RoleRepository;
import org.botoff.repository.UserRepository;
import org.botoff.security.dto.RegistrationUserDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//компонент spring security для поиска пользователей
@Service
@RequiredArgsConstructor(onConstructor_={@Lazy})
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    //метод для поиска пользователей в базе
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //реализация метода интерфейса для приведения найденного user к типу UserDetails
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    //создаем нового пользователя
    //TODO проверить наличие пользователя
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        return userRepository.save(user);
    }
}
