package com.globaltrust.bank.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/", "/css/", "/js/", "/images/", "/index.html", "/favicon.ico"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (path.equals("/") || PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/user/")) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\": \"Unauthorized - User session required\"}");
                return;
            }
        }

        if (path.startsWith("/api/admin/")) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("adminId") == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\": \"Unauthorized - Admin session required\"}");
                return;
            }
        }

        if (path.endsWith(".html") && !path.equals("/index.html")) {
            HttpSession session = req.getSession(false);
            if (session == null || (session.getAttribute("userId") == null && session.getAttribute("adminId") == null)) {
                res.sendRedirect("/index.html");
                return;
            }
            if (path.contains("admin") && session.getAttribute("adminId") == null) {
                res.sendRedirect("/index.html");
                return;
            }
            if (!path.contains("admin") && session.getAttribute("userId") == null) {
                res.sendRedirect("/index.html");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
