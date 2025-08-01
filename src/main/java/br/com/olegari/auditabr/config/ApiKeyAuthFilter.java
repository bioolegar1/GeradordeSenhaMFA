package br.com.olegari.auditabr.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.security.api-key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Busca o valor do header "X-API-KEY" na requisição
        String requestApiKey = request.getHeader("X-API-KEY");

        // 2. Verifica se a chave está presente e se é igual à chave esperada
        if (apiKey.equals(requestApiKey)) {
            // Se a chave for válida, permite que a requisição continue
            filterChain.doFilter(request, response);
        } else {
            // Se a chave for inválida ou ausente, retorna um erro 403 Forbidden
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("API Key inválida ou ausente.");
        }
    }
}