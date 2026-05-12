package com.insper.pf.controller;

import com.insper.pf.dto.ProjetoDTO;
import com.insper.pf.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @GetMapping
    public List<ProjetoDTO> findAll() {
        return projetoService.findAll();
    }

    @GetMapping("/{id}")
    public ProjetoDTO findById(@PathVariable Long id) {
        return projetoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjetoDTO create(@Valid @RequestBody ProjetoDTO dto) {
        return projetoService.create(dto);
    }

    @PutMapping("/{id}")
    public ProjetoDTO update(@PathVariable Long id, @Valid @RequestBody ProjetoDTO dto) {
        return projetoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projetoService.delete(id);
    }

    @PostMapping("/{projetoId}/usuarios/{userId}")
    public ProjetoDTO addUser(@PathVariable Long projetoId, @PathVariable Long userId) {
        return projetoService.addUser(projetoId, userId);
    }

    @DeleteMapping("/{projetoId}/usuarios/{userId}")
    public ProjetoDTO removeUser(@PathVariable Long projetoId, @PathVariable Long userId) {
        return projetoService.removeUser(projetoId, userId);
    }
}
