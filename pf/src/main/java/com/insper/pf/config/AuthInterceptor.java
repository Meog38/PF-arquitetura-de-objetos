package com.insper.pf.config;

import com.insper.pf.enums.Papel;
import com.insper.pf.model.User;
import com.insper.pf.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }

        String userIdHeader = request.getHeader("X-USER-ID");
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "O cabeçalho X-USER-ID é obrigatório para esta requisição");
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "O cabeçalho X-USER-ID deve ser um número inteiro");
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não encontrado para o X-USER-ID informado");
            return false;
        }

        if (!"GET".equalsIgnoreCase(method) && !"OPTIONS".equalsIgnoreCase(method) && user.getPapel() != Papel.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado: apenas ADMIN pode realizar POST, PUT ou DELETE");
            return false;
        }

        return true;
    }
}