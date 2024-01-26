package org.example.controller;

import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    public void getAllUsers_shouldReturnListOfUsers() throws Exception {

        List<User> mockUsers = Arrays.asList(
                new User(1L, "user1", "user1@example.com", "password1", UserRole.ROLE_USER),
                new User(2L, "user2", "user2@example.com", "password2", UserRole.ROLE_ADMIN)
        );
        when(userRepository.findAll()).thenReturn(mockUsers);


        String jsonContent = new String(FileCopyUtils.copyToByteArray(
                new ClassPathResource("json/user-controller/user-get-all-response.json").getInputStream()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = "USER")
    public void getAllUsers_shoudReturn403StatusCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/all"))
                .andExpect(status().isForbidden());
    }
}
