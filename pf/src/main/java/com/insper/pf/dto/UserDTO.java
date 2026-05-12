package com.insper.pf.dto;

import com.insper.pf.enums.Papel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotNull(message = "O papel (ADMIN ou USER) é obrigatório")
    private Papel papel;

    private List<Long> projetoIds = new ArrayList<>();
}