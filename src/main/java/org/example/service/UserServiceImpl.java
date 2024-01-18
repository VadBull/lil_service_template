package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.entity.UserDto;
import org.example.entity.mapper.UserMapper;
import org.example.exception.NotFoundEntityException;
import org.example.exception.NotUniqueEntityException;
import org.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new NotUniqueEntityException("Login is already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new NotUniqueEntityException("Email is already exists");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id){
        userRepository.deleteById(id);
    };

    @Override
    @Transactional
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundEntityException(String.format("User %d does not exists. User can't be found", userId)));
    }

    @Override
    @Transactional
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> new NotFoundEntityException(String.format("%s does not exists. User can't be found", username)));
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new NotFoundEntityException(String.format("%s does not exist. User can't be found", email)));
    }

    @Override
    @Transactional
    public User updateUserById(Long id, UserDto userDto) {
        Optional<User> userToUpdateCandidate = userRepository.findById(id);
        if (userToUpdateCandidate.isPresent()) {
            User user = userToUpdateCandidate.get();
            userMapper.updateEntity(userDto, user);
            return createUser(user);
        } else {
            throw new NotFoundEntityException(String.format("Not found user, id: %d", id));
        }
    }

    @Override
    @Transactional
    public User updateUserByUsername(String username, UserDto userDto) {
        Optional<User> userToUpdateCandidate = userRepository.findByUsernameIgnoreCase(username);
        if (userToUpdateCandidate.isPresent()) {
            User user = userToUpdateCandidate.get();
            userMapper.updateEntity(userDto, user);
            return userRepository.save(user);
        } else {
            throw new NotFoundEntityException(String.format("User not found, username: %s", username));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }
}

//TODO: user to edit equal current user
