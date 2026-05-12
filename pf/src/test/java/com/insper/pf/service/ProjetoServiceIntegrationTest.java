package com.insper.pf.service;

import com.insper.pf.dto.ProjetoDTO;
import com.insper.pf.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ProjetoServiceIntegrationTest {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UserService userService;

    @Test
    void addUser_toMissingProjeto_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class,
                () -> projetoService.addUser(999L, 1L));
    }

    @Test
    void removeUser_withMissingUser_shouldThrowEntityNotFoundException() {
        var projetoDto = new ProjetoDTO();
        projetoDto.setNome("Projeto Service");
        projetoDto.setDescricao("Teste serviço");
        var createdProjeto = projetoService.create(projetoDto);

        assertThrows(EntityNotFoundException.class,
                () -> projetoService.removeUser(createdProjeto.getId(), 999L));
    }

    @Test
    void addUser_andRemoveUser_shouldUpdateProjetoMembers() {
        var userDto = new UserDTO();
        userDto.setNome("Novo Colaborador");
        userDto.setCpf("66677788899");
        userDto.setPapel(com.insper.pf.enums.Papel.USER);
        var createdUser = userService.create(userDto);

        var projetoDto = new ProjetoDTO();
        projetoDto.setNome("Projeto Service 2");
        projetoDto.setDescricao("Teste associado");
        var createdProjeto = projetoService.create(projetoDto);

        var updatedProjeto = projetoService.addUser(createdProjeto.getId(), createdUser.getId());
        assertEquals(1, updatedProjeto.getUsuarioIds().size());
        assertEquals(createdUser.getId(), updatedProjeto.getUsuarioIds().get(0));

        var removedProjeto = projetoService.removeUser(createdProjeto.getId(), createdUser.getId());
        assertEquals(0, removedProjeto.getUsuarioIds().size());
    }
}
