package sv.edu.udb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sv.edu.udb.security.JwtFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // Dentro de .authorizeHttpRequests(auth -> auth ...
                        .requestMatchers(
                                "/",                  // Raíz
                                "/index.html",         // Archivo principal
                                "/static/**",          // Carpeta de estilos y JS
                                "/css/**",
                                "/js/**",
                                "/api/auth/**",        // Endpoints de login
                                "/v3/api-docs/**",     // Swagger
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll() // ESTO QUITA EL 403
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}