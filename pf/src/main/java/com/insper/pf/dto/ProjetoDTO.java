package com.insper.pf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjetoDTO {
    private Long id;

    @NotBlank(message = "O nome do projeto é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição do projeto é obrigatória")
    private String descricao;

    private List<Long> usuarioIds = new ArrayList<>();
}
