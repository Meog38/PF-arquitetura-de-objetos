package com.insper.pf.config;

import com.insper.pf.enums.Papel;
import com.insper.pf.model.User;
import com.insper.pf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Se o banco estiver vazio, cria o primeiro ADMIN para possibilitar os testes 
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setNome("Administrador Padrão");
            admin.setCpf("00000000000");
            admin.setPapel(Papel.ADMIN);
            userRepository.save(admin);
            
            System.out.println("-----------------------------------------------------");
            System.out.println("Usuário ADMIN inicial criado com ID: " + admin.getId());
        }
    }
}