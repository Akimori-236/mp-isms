package tfip.akimori.server.configs;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tfip.akimori.server.services.JwtService;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtSvc;
    private final UserDetailsService userDetailsSvc;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // IF THERE IS NO AUTH IN REQUESTENTITY
            filterChain.doFilter(request, response);
            return;
        }
        // remove "Bearer " from jwt
        jwt = authHeader.substring(7);
        userEmail = jwtSvc.extractUsername(jwt);
        // check for email and NOT already authenticated
        // if already authenticated dont need check?
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // get userdetails from db
            UserDetails userDetails = this.userDetailsSvc.loadUserByUsername(userEmail);
            // check validity
            if (jwtSvc.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // pass hand to next filters to execute
        filterChain.doFilter(request, response);
    }

}
