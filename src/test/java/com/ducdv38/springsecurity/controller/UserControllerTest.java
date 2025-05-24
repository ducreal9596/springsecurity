package com.ducdv38.springsecurity.controller;

import com.ducdv38.springsecurity.dto.request.UserCreationRequest;
import com.ducdv38.springsecurity.dto.response.UserResponse;
import com.ducdv38.springsecurity.service.impl.UserServiceIpml;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceIpml userServiceIpml;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1990, 1, 1);
        userCreationRequest = UserCreationRequest.builder()
                .username("David")
                .birthDate(dob)
                .fistName("Duc")
                .lastName("Duong")
                .password("123456")
                .build();
        userResponse = UserResponse.builder()
                .id("5afdf7224cca")
                .username("David")
                .birthDate(dob)
                .fistName("Duc")
                .lastName("Duong")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userServiceIpml.creeateUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/user/post-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("1000"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("5afdf7224cca"));
    }

}
