package com.insper.pf.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void getUsersWithoutHeader_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_withValidData_shouldReturnCreated() throws Exception {
        String payload = "{\"nome\":\"Usuário Teste\",\"cpf\":\"12345678901\",\"papel\":\"USER\"}";

        mockMvc.perform(post("/users")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Usuário Teste"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.papel").value("USER"));
    }

    @Test
    void createUser_missingCpf_shouldReturnBadRequest() throws Exception {
        String payload = "{\"nome\":\"Usuário Sem CPF\",\"papel\":\"USER\"}";

        mockMvc.perform(post("/users")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("cpf: O CPF é obrigatório"));
    }

    @Test
    void createUser_withoutHeader_shouldReturnUnauthorized() throws Exception {
        String payload = "{\"nome\":\"Usuário Sem Header\",\"cpf\":\"11122233344\",\"papel\":\"USER\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUser_withNonAdminHeader_shouldReturnForbidden() throws Exception {
        String newUserPayload = "{\"nome\":\"Usuário Normal\",\"cpf\":\"22233344455\",\"papel\":\"USER\"}";

        var response = mockMvc.perform(post("/users")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserPayload))
                .andExpect(status().isCreated())
                .andReturn();

        Long normalUserId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.id", Long.class);

        String payload = "{\"nome\":\"Segundo Usuário\",\"cpf\":\"33344455566\",\"papel\":\"USER\"}";

        mockMvc.perform(post("/users")
                        .header("X-USER-ID", String.valueOf(normalUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }
}
