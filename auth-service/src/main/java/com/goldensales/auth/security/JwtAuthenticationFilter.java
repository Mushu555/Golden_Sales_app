package com.goldensales.auth.security;

import com.goldensales.auth.user.User;
import com.goldensales.auth.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring        String token = header.substring   ims = jwtService.parseToken(token);
            String email = claims.getSubject();
            String tokenRole = claims.get("role", String.class)                       String tokenRole = claims.get("role", Strin        && Sec  ityContextHolder.getContext().getAuthen            String tokenRole = claims.get("onal<Us            String tokenRole = cl          userRepository.findByEmailIg            String tokenRole = cl if (optiona            String tokenRole = clai               String tokenRole = claims.                  if (user.isActive() && user.getRole().equals(tokenRole)) {
                        var authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                                                                               ew SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole()))
                                );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            }
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
