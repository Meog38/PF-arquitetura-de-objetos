package com.insper.pf.service;

import com.insper.pf.dto.ProjetoDTO;
import com.insper.pf.model.Projeto;
import com.insper.pf.model.User;
import com.insper.pf.repository.ProjetoRepository;
import com.insper.pf.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UserRepository userRepository;

    public ProjetoService(ProjetoRepository projetoRepository, UserRepository userRepository) {
        this.projetoRepository = projetoRepository;
        this.userRepository = userRepository;
    }

    public List<ProjetoDTO> findAll() {
        return projetoRepository.findAll().stream().map(this::toDto).toList();
    }

    public ProjetoDTO findById(Long id) {
        return toDto(getEntity(id));
    }

    public ProjetoDTO create(ProjetoDTO dto) {
        Projeto projeto = toEntity(dto);
        attachUsers(projeto, dto);
        return toDto(projetoRepository.save(projeto));
    }

    public ProjetoDTO update(Long id, ProjetoDTO dto) {
        Projeto projeto = getEntity(id);
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.getUsuarios().clear();
        attachUsers(projeto, dto);
        return toDto(projetoRepository.save(projeto));
    }

    public void delete(Long id) {
        projetoRepository.delete(getEntity(id));
    }

    public ProjetoDTO addUser(Long projetoId, Long userId) {
        Projeto projeto = getEntity(projetoId);
        User user = getUser(userId);
        if (!projeto.getUsuarios().contains(user)) {
            projeto.getUsuarios().add(user);
        }
        return toDto(projetoRepository.save(projeto));
    }

    public ProjetoDTO removeUser(Long projetoId, Long userId) {
        Projeto projeto = getEntity(projetoId);
        User user = getUser(userId);
        projeto.getUsuarios().remove(user);
        return toDto(projetoRepository.save(projeto));
    }

    public Projeto getEntity(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com id " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id " + id));
    }

    private void attachUsers(Projeto projeto, ProjetoDTO dto) {
        if (dto.getUsuarioIds() != null) {
            dto.getUsuarioIds().stream()
                    .map(this::getUser)
                    .forEach(projeto.getUsuarios()::add);
        }
    }

    private Projeto toEntity(ProjetoDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setId(dto.getId());
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        return projeto;
    }

    private ProjetoDTO toDto(Projeto projeto) {
        ProjetoDTO dto = new ProjetoDTO();
        dto.setId(projeto.getId());
        dto.setNome(projeto.getNome());
        dto.setDescricao(projeto.getDescricao());
        dto.setUsuarioIds(projeto.getUsuarios().stream().map(User::getId).toList());
        return dto;
    }
}
