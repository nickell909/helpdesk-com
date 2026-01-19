package com.helpdesk.service;

import com.helpdesk.dto.UserRequest;
import com.helpdesk.entity.Role;
import com.helpdesk.entity.User;
import com.helpdesk.repository.RoleRepository;
import com.helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + login));
    }

    public User createUser(UserRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Пароль не может быть пустым");
        }
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Логин уже занят: " + request.getLogin());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже занят: " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + request.getRoleId()));

        User user = new User();
        user.setLogin(request.getLogin());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);

        if (!user.getLogin().equals(request.getLogin()) && userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("Логин уже занят: " + request.getLogin());
        }
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email уже занят: " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Роль не найдена: " + request.getRoleId()));

        user.setLogin(request.getLogin());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public List<User> getOperators() {
        return userRepository.findByRoleNameIn(List.of("OPERATOR", "ADMIN"));
    }
}
