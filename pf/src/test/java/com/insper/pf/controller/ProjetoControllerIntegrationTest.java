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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProjetoControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void createProject_withValidData_shouldReturnCreated() throws Exception {
        String payload = "{\"nome\":\"Projeto Alpha\",\"descricao\":\"Descrição do projeto\",\"usuarioIds\":[1]}";

        mockMvc.perform(post("/projetos")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Projeto Alpha"))
                .andExpect(jsonPath("$.usuarioIds[0]").value(1));
    }

    @Test
    void addAndRemoveUserFromProject_shouldReturnUpdatedProject() throws Exception {
        String userPayload = "{\"nome\":\"Colaborador\",\"cpf\":\"44455566677\",\"papel\":\"USER\"}";

        var userResponse = mockMvc.perform(post("/users")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPayload))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = JsonPath.parse(userResponse.getResponse().getContentAsString()).read("$.id", Long.class);

        String projectPayload = "{\"nome\":\"Projeto Beta\",\"descricao\":\"Projeto para testes\",\"usuarioIds\":[1]}";

        var projectResponse = mockMvc.perform(post("/projetos")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectPayload))
                .andExpect(status().isCreated())
                .andReturn();

        Long projectId = JsonPath.parse(projectResponse.getResponse().getContentAsString()).read("$.id", Long.class);

        mockMvc.perform(post(String.format("/projetos/%d/usuarios/%d", projectId, userId))
                        .header("X-USER-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioIds").isArray())
                .andExpect(jsonPath("$.usuarioIds.length()").value(2));

        mockMvc.perform(delete(String.format("/projetos/%d/usuarios/%d", projectId, userId))
                        .header("X-USER-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioIds.length()").value(1));
    }

    @Test
    void addUserToProject_missingProject_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/projetos/999/usuarios/1")
                        .header("X-USER-ID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeUserFromProject_missingUser_shouldReturnNotFound() throws Exception {
        String projectPayload = "{\"nome\":\"Projeto Gamma\",\"descricao\":\"Outro projeto\",\"usuarioIds\":[1]}";

        var projectResponse = mockMvc.perform(post("/projetos")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectPayload))
                .andExpect(status().isCreated())
                .andReturn();

        Long projectId = JsonPath.parse(projectResponse.getResponse().getContentAsString()).read("$.id", Long.class);

        mockMvc.perform(delete(String.format("/projetos/%d/usuarios/999", projectId))
                        .header("X-USER-ID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProject_withNonAdminHeader_shouldReturnForbidden() throws Exception {
        String userPayload = "{\"nome\":\"Usuário Teste 2\",\"cpf\":\"55566677788\",\"papel\":\"USER\"}";

        var userResponse = mockMvc.perform(post("/users")
                        .header("X-USER-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPayload))
                .andExpect(status().isCreated())
                .andReturn();

        Long userId = JsonPath.parse(userResponse.getResponse().getContentAsString()).read("$.id", Long.class);

        String projectPayload = "{\"nome\":\"Projeto Delta\",\"descricao\":\"Descrição Delta\",\"usuarioIds\":[1]}";

        mockMvc.perform(post("/projetos")
                        .header("X-USER-ID", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectPayload))
                .andExpect(status().isForbidden());
    }
}
