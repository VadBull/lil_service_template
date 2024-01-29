package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.User;
import org.example.entity.UserDto;
import org.example.entity.UserRole;
import org.example.entity.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    private static Logger log = Logger.getLogger(UserControllerIntegrationTest.class.getName());
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void getAllUsers_shouldReturnListOfUsers() throws Exception {

        String url = "/api/user/all";

        List<User> mockUsers = Arrays.asList(
                new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER),
                new User(2L, "user2", "user2@example.com", "password2", UserRole.ROLE_ADMIN)
        );
        when(userRepository.findAll()).thenReturn(mockUsers);


        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-get-all-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void getAllUsers_shouldReturn403StatusCode() throws Exception {
        String url = "/api/user/all";
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void deleteUserById_shouldSuccessfullyDelete() throws Exception {
        String url = "/api/user/id/1";
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void deleteUserById_shouldReturn403StatusCode() throws Exception {
        String url = "/api/user/id/1";
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void getUserById_shouldReturnUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(
                Optional.of(new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER))
        );

        String url = "/api/user/id/1";

        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-get-user-by-id-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void getUserByUsername_shouldReturnUser() throws Exception {
        when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(
                Optional.of(new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER))
        );

        String url = "/api/user/username/user1";

        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-get-user-by-username-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));

    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void getUserByEmail_shouldReturnUser() throws Exception {
        when(userRepository.findByEmailIgnoreCase("user1@example.com")).thenReturn(
                Optional.of(new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER))
        );

        String url = "/api/user/email/user1@example.com";

        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-get-user-by-email-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));

    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void createUser_shouldReturnUser() throws Exception {
        UserDto userToSave = UserDto.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .build();
        when(userRepository.existsByUsernameIgnoreCase(userToSave.getUsername())).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(userToSave.getEmail())).thenReturn(false);
        when(userRepository.save(any()))
                .thenReturn(new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER));

        String url = "/api/user";

        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-create-user-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(new ObjectMapper().writeValueAsString(userToSave))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));

    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void createUser_shouldThrowUsernameIsAlreadyExistException() throws Exception {
        UserDto userToSave = UserDto.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .build();
        when(userRepository.existsByUsernameIgnoreCase(userToSave.getUsername())).thenReturn(true);

        String url = "/api/user";


        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(new ObjectMapper().writeValueAsString(userToSave))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void createUser_shouldThrowEmailIsAlreadyExistException() throws Exception {
        UserDto userToSave = UserDto.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .build();
        when(userRepository.existsByUsernameIgnoreCase(userToSave.getUsername())).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(userToSave.getEmail())).thenReturn(true);

        String url = "/api/user";


        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(new ObjectMapper().writeValueAsString(userToSave))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void updateUserById_shouldReturnUser() throws Exception {
        Long id = 1L;
        UserDto userToUpdate = UserDto.builder()
                .username("user1")
                .email("user2@example.com")
                .password("password2")
                .build();


        User updatedUser = new User(id, "user1", "user2@example.com", "password2", UserRole.ROLE_USER);
        when(userRepository.findById(id))
                .thenReturn(Optional.of(new User(id, "user1", "user1@example.com", "password1", UserRole.ROLE_USER)));

        when(userRepository.save(any())).thenReturn(updatedUser);

        String url = "/api/user/id/1";

        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-update-user-by-id-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .content(new ObjectMapper().writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void updateUserById_shouldThrowUserNotFound() throws Exception {
        Long id = 1L;
        UserDto userToUpdate = UserDto.builder()
                .username("user1")
                .email("user2@example.com")
                .password("password2")
                .build();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        String url = "/api/user/id/1";
        mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .content(new ObjectMapper().writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void updateUserByUUsername_shouldReturnUser() throws Exception {
        String username = "user1";
        UserDto userToUpdate = UserDto.builder()
                .email("user2@example.com")
                .password("password2")
                .build();


        User updatedUser = new User(1L, username, "user2@example.com", "password2", UserRole.ROLE_USER);
        when(userRepository.findByUsernameIgnoreCase(username))
                .thenReturn(Optional.of(new User(1L, username, "user1@example.com", "password1", UserRole.ROLE_USER)));

        when(userRepository.save(any())).thenReturn(updatedUser);

        String url = "/api/user/username/" + username;

        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-update-user-by-username-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .content(new ObjectMapper().writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void updateUserByUsername_shouldThrowUserNotFound() throws Exception {
        String username = "user1";
        UserDto userToUpdate = UserDto.builder()
                .email("user2@example.com")
                .password("password2")
                .build();

        when(userRepository.findByUsernameIgnoreCase(username))
                .thenReturn(Optional.empty());

        String url = "/api/user/username/" + username;
        mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .content(new ObjectMapper().writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
