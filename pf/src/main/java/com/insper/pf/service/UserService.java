package com.insper.pf.service;

import com.insper.pf.dto.UserDTO;
import com.insper.pf.model.User;
import com.insper.pf.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDTO findById(Long id) {
        return toDto(getEntity(id));
    }

    public UserDTO create(UserDTO dto) {
        userRepository.findByCpf(dto.getCpf()).ifPresent(existing -> {
            throw new IllegalArgumentException("CPF já cadastrado");
        });
        return toDto(userRepository.save(toEntity(dto)));
    }

    public UserDTO update(Long id, UserDTO dto) {
        User user = getEntity(id);
        userRepository.findByCpf(dto.getCpf())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("CPF já cadastrado");
                });
        user.setNome(dto.getNome());
        user.setCpf(dto.getCpf());
        user.setPapel(dto.getPapel());
        return toDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.delete(getEntity(id));
    }

    public User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id " + id));
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setNome(dto.getNome());
        user.setCpf(dto.getCpf());
        user.setPapel(dto.getPapel());
        return user;
    }

    private UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setCpf(user.getCpf());
        dto.setPapel(user.getPapel());
        dto.setProjetoIds(user.getProjetos().stream().map(projeto -> projeto.getId()).toList());
        return dto;
    }
}
