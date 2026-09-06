package schwartz.spring.auth.infra.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Criando o guardinha customizado
public class CustomInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CustomInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.warn("Alguém chamou a rota: {}", request.getRequestURI());
        // Se retornar true, a requisição continua. Se fosse false, seria bloqueada aqui.
        return true;
    }
}

