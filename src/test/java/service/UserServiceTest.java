package service;

import org.example.entity.User;
import org.example.entity.UserDto;
import org.example.entity.mapper.UserMapper;
import org.example.exception.NotFoundEntityException;
import org.example.exception.NotUniqueEntityException;
import org.example.repository.UserRepository;
import org.example.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userToCreate);

        User createduser = userService.createUser(userToCreate);

        Assertions.assertEquals(createduser, userToCreate);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void shouldThrowExceptionWhenUsernameIsNotUniqueCreateUser() {
        // Given
        User existingUser = new User(1L, "existing_user", "existing.user@example.com", "password");
        User newUser = new User(2L, "existing_user", "new.user@example.com", "password");

        Mockito.when(userRepository.existsByUsernameIgnoreCase(existingUser.getUsername())).thenReturn(true);

        assertThrows(NotUniqueEntityException.class, () -> userService.createUser(newUser),
                "Exception should be thrown when username is not unique.");
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(newUser.getUsername());
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsNotUnique() {
        User existingUser = new User(1L, "existing_user", "existing.user@example.com", "password");
        User newUser = new User(2L, "existing_user2", "existing.user@example.com", "password");

        Mockito.when(userRepository.existsByEmailIgnoreCase(existingUser.getEmail())).thenReturn(true);

        assertThrows(NotUniqueEntityException.class, () -> userService.createUser(newUser),
                "Exception should be thrown when email is not unique.");
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmailIgnoreCase(newUser.getEmail());
    }

    @Test
    public void shouldReturnUsersList() {
        User user1 = new User(1L, "john_doe", "john.doe@example.com", "password");
        User user2 = new User(2L, "peter_doe", "peterdoe@example.com", "password");
        List<User> expectedUserList = List.of(user1, user2);

        Mockito.when(userRepository.findAll()).thenReturn(expectedUserList);

        List<User> returnedUserList = userService.getAllUsers();

        Assertions.assertEquals(expectedUserList, returnedUserList);
    }

    @Test
    public void shouldReturnUserByUserId() {
        Long userId = 1L;
        User expectedUser = new User(userId, "john_doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User returnedUser = userService.getUser(userId);

        Assertions.assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingUserById() {
        Long userId = 999L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.getUser(userId),
                "Exception should be thrown when user with specified id is not found.");

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    public void shouldReturnUserByUsername() {
        String username = "john_doe";
        User expectedUser = new User(1L, username, "john.doe@example.com", "password");

        Mockito.when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(expectedUser));

        User returnedUser = userService.getUserByUsername(username);

        Assertions.assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingUserByUsername() {
        String username = "john_doe";

        Mockito.when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.getUserByUsername(username),
                "Exception should be thrown when user with specified username is not found.");

        Mockito.verify(userRepository, Mockito.times(1)).findByUsernameIgnoreCase(username);
    }

    @Test
    public void shouldReturnUserByEmail() {
        String email = "john.doe@example.com";
        User expectedUser = new User(1L, "john_doe", email, "password");

        Mockito.when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUser));

        User returnedUser = userService.getUserByEmail(email);

        Assertions.assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenGettingUserByEmail() {
        String userEmail = "john_doe";

        Mockito.when(userRepository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.getUserByEmail(userEmail),
                "Exception should be thrown when user with specified username is not found.");

        Mockito.verify(userRepository, Mockito.times(1)).findByEmailIgnoreCase(userEmail);
    }

    @Test
    public void shouldReturnUpdatedUserByUserId() {
        // Given
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");
        User existingUser = new User(userId, "john_doe", "john.doe@example.com", "password");

        User updatedUser = new User(1L, "updated_username", "updated.email@example.com", "updatedPassword");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        User returnedUser = userService.updateUserById(userId, updatedUserDto);

        Assertions.assertEquals(updatedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingUserById() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.updateUserById(userId, updatedUserDto),
                "Exception should be thrown when updating a user with specified ID that does not exist.");
    }

    @Test
    public void shouldReturnUpdatedUserByUsername() {
        // Given
        String username = "john_doe";
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");
        User existingUser = new User(1L, username, "john.doe@example.com", "password");

        User updatedUser = new User(1L, "updated_username", "updated.email@example.com", "updatedPassword");

        Mockito.when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        User returnedUser = userService.updateUserByUsername(username, updatedUserDto);

        Assertions.assertEquals(updatedUser, returnedUser);
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingUserByUsername() {
        String userName = "john_doe";
        UserDto updatedUserDto = new UserDto("updated_username", "updated.email@example.com", "updatedPassword");

        Mockito.when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> userService.updateUserByUsername(userName, updatedUserDto),
                "Exception should be thrown when updating a user with specified Username that does not exist.");
    }
}
