package com.instamic.authservice.security;

import com.instamic.authservice.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final String serviceUsername;

    public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, JwtTokenProvider tokenProvider, UserService userService, String serviceUsername) {
        this.jwtConfig = jwtConfig;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.serviceUsername = serviceUsername;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader(jwtConfig.getHeader());

        // 2. validate the header and check the prefix
        if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        // If there is no token provided and hence the user won't be authenticated.
        // It's Ok. Maybe the user accessing a public path or asking for a token.

        // All secured paths that needs a token are already defined and secured in config class.
        // And If user tried to access without access token, then he won't be authenticated and an exception will be thrown.

        // 3. Get the token
        String token = header.replace(jwtConfig.getPrefix(), "");

        if(tokenProvider.validateToken(token)) {
            Claims claims = tokenProvider.getClaimsFromJWT(token);
            String username = claims.getSubject();

            UsernamePasswordAuthenticationToken auth;

            if(username.equals(serviceUsername)) { // If it is service account don't load user details

                List<String> authorities = (List<String>) claims.get("authorities");

                auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            } else {
                auth = userService.findByUsername(username)
                        .map(UserDetailsImpl::new)
                        .map(userDetailsImpl -> {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetailsImpl,
                                            null,
                                            userDetailsImpl.getAuthorities());

                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            return authenticationToken;
                        })
                        .orElse(null);
            }

            SecurityContextHolder.getContext().setAuthentication(auth);

        } else {
            SecurityContextHolder.clearContext();
        }
        // go to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }

}
