package com.insper.pf.model;

import com.insper.pf.enums.Papel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    // Relação Mapeada: um projeto tem vários usuários e vice-versa.
    @ManyToMany(mappedBy = "usuarios")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Projeto> projetos = new ArrayList<>();
}