package org.example.service;

import org.example.entity.User;
import org.example.entity.UserDto;

import java.util.List;

public interface UserService {
    public User createUser(User user);
    public List<User> getAllUsers();
    public void deleteById(Long id);
    public User getUser(Long userId);
    public User getUserByUsername(String username);
    public User getUserByEmail(String email);
    public User updateUserById(Long id, UserDto userDto);
    public User updateUserByUsername(String username, UserDto userDto);

}
