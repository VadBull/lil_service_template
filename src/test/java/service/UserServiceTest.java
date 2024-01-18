package service;

import org.example.entity.User;
import org.example.entity.UserDto;
import org.example.entity.UserRole;
import org.example.entity.mapper.UserMapper;
import org.example.exception.NotFoundEntityException;
import org.example.exception.NotUniqueEntityException;
import org.example.repository.UserRepository;
import org.example.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @Test
    public void shouldSuccessfullyCreateUser() {
        User userToCreate = User.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .password("password")
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenReturn(userToCreate);

        User createduser = userService.createUser(userToCreate);

        assertEquals(createduser, userToCreate);
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void shouldReturnAllUsers() {
        List<User> mockUsers = Arrays.asList(
                new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER),
                new User(2L, "user2", "user2@example.com", "password2", UserRole.ROLE_ADMIN)
        );

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(mockUsers.size(), result.size());
        assertEquals(mockUsers.get(0), result.get(0));
        assertEquals(mockUsers.get(1), result.get(1));
    }

    @Test
    public void shouldDeleteUser() {
        Long userIdToDelete = 1L;

        userService.deleteById(userIdToDelete);

        verify(userRepository, times(1)).deleteById(userIdToDelete);
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsNotUniqueCreateUser() {
        // Given
        User existingUser = new User(1L, "existing_user", "existing.user@example.com", "password", UserRole.ROLE_USER);
        User newUser = new User(2L, "existing_user", "new.user@example.com", "password", UserRole.ROLE_USER);

        when(userRepository.existsByUsernameIgnoreCase(existingUser.getUsername())).thenReturn(true);

        assertThrows(NotUniqueEntityException.class, () -> userService.createUser(newUser),
                "Exception should be thrown when username is not unique.");
        verify(userRepository, times(1)).existsByUsernameIgnoreCase(newUser.getUsername());
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsNotUnique() {
        User existingUser = new User(1L, "existing_user", "existing.user@example.com", "password", UserRole.ROLE_USER);
        User newUser = new User(2L, "existing_user2", "existing.user@example.com", "password", UserRole.ROLE_USER);

        when(userRepository.existsByEmailIgnoreCase(existingUser.getEmail())).thenReturn(true);

        assertThrows(NotUniqueEntityException.class, () -> userService.createUser(newUser),
                "Exception should be thrown when email is not unique.");
        verify(userRepository, times(1)).existsByEmailIgnoreCase(newUser.getEmail());
    }

    @Test
    public void shouldReturnUsersList() {
        User user1 = new User(1L, "john_doe", "john.doe@example.com", "password", UserRole.ROLE_USER);
        User user2 = new User(2L, "peter_doe", "peterdoe@example.com", "password", UserRole.ROLE_USER);
        List<User> expectedUserList = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUserList);

        List<User> returnedUserList = userService.getAllUsers();

        assertEquals(expectedUserList, returnedUserList);
    }

    @Test
    public void shouldReturnUserByUserId() {
        Long userId = 1L;
        User expectedUser = new User(userId, "john_doe", "john.doe@example.com", "password", UserRole.ROLE_USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User returnedUser = userService.getUser(userId);

        assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingUserById() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.getUser(userId),
                "Exception should be thrown when user with specified id is not found.");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void shouldReturnUserByUsername() {
        String username = "john_doe";
        User expectedUser = new User(1L, username, "john.doe@example.com", "password", UserRole.ROLE_USER);

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(expectedUser));

        User returnedUser = userService.getUserByUsername(username);

        assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingUserByUsername() {
        String username = "john_doe";

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.getUserByUsername(username),
                "Exception should be thrown when user with specified username is not found.");

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
    }

    @Test
    public void shouldReturnUserByEmail() {
        String email = "john.doe@example.com";
        User expectedUser = new User(1L, "john_doe", email, "password", UserRole.ROLE_USER);

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUser));

        User returnedUser = userService.getUserByEmail(email);

        assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingUserByEmail() {
        String userEmail = "john_doe";

        when(userRepository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.getUserByEmail(userEmail),
                "Exception should be thrown when user with specified username is not found.");

        verify(userRepository, times(1)).findByEmailIgnoreCase(userEmail);
    }

    @Test
    public void shouldReturnUpdatedUserByUserId() {
        // Given
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");
        User existingUser = new User(userId, "john_doe", "john.doe@example.com", "password", UserRole.ROLE_USER);

        User updatedUser = new User(1L, "updated_username", "updated.email@example.com", "updatedPassword", UserRole.ROLE_USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        User returnedUser = userService.updateUserById(userId, updatedUserDto);

        assertEquals(updatedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingUserById() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.updateUserById(userId, updatedUserDto),
                "Exception should be thrown when updating a user with specified ID that does not exist.");
    }

    @Test
    public void shouldReturnUpdatedUserByUsername() {
        // Given
        String username = "john_doe";
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");
        User existingUser = new User(1L, username, "john.doe@example.com", "password", UserRole.ROLE_USER);

        User updatedUser = new User(1L, "updated_username", "updated.email@example.com", "updatedPassword", UserRole.ROLE_USER);

        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        User returnedUser = userService.updateUserByUsername(username, updatedUserDto);

        assertEquals(updatedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingUserByUsername() {
        String userName = "john_doe";
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");

        when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.updateUserByUsername(userName, updatedUserDto),
                "Exception should be thrown when updating a user with specified Username that does not exist.");
    }
}
