package com.zerobase.fastlms.admin.handler;

import com.zerobase.fastlms.admin.entity.LoginHistory;
import com.zerobase.fastlms.admin.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 로그인 성공 시 로그인 히스토리 테이블에 해당 정보를 저장하기 위한 Handler
 */
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , Authentication authentication) throws IOException, ServletException {

        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = httpServletRequest.getRemoteAddr();
        }

        String userAgent = httpServletRequest.getHeader("USER-AGENT");

        //성공적으로 들어왔으므로 로그히스토리에 저장할 것이다.
        loginHistoryRepository.save(LoginHistory.builder()
                .userId(authentication.getName())
                .loggedDt(LocalDateTime.now())
                .clientIp(clientIp)
                .userAgent(userAgent)
                .build());
        httpServletResponse.sendRedirect("/");
    }
}
