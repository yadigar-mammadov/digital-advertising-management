package com.digitaladvertisingmanagement.auth.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;

  private final String requestBody =
      """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

  @Test
  void registerShouldCreateUserAndReturnToken() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void registerShouldFailWhenEmailAlreadyExists() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginShouldReturnTokenWhenCredentialsAreCorrect() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void loginShouldFailWhenPasswordIsIncorrect() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.replace("password123", "wrong-password")))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void loginShouldFailWhenUserDoesNotExist() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().is4xxClientError());
  }
}
