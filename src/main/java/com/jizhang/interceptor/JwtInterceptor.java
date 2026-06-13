package com.jizhang.interceptor;

import com.jizhang.enums.ErrorCode;
import com.jizhang.exception.BusinessException;
import com.jizhang.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        
        log.info("==> Request: {} {} from {}", method, uri, remoteAddr);
        
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("OPTIONS request, skipping authentication");
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            log.warn("No token provided for {} {}", method, uri);
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid token for {} {}", method, uri);
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        
        log.info("Authenticated user {} for {} {}", userId, method, uri);

        return true;
    }
}
