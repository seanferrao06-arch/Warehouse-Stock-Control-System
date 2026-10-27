package warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import warehouse.service.UserService;
import warehouse.util.PasswordUtil;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/products", "/products/**",
                                         "/categories", "/categories/**",
                                         "/suppliers", "/suppliers/**",
                                         "/users", "/users/**",
                                         "/reports", "/reports/**").hasRole("ADMIN")
                        .requestMatchers("/", "/stock", "/stock/**", "/access-denied").hasAnyRole("ADMIN", "STAFF")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied"))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(UserService users) {
        return username -> {
            warehouse.model.User account = users.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
            return User.withUsername(account.getUsername())
                    .password(account.getPasswordHash())
                    .roles(account.getRole().name())
                    .build();
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return PasswordUtil.sha256(rawPassword != null ? rawPassword.toString() : "");
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null) {
                    return false;
                }
                return encode(rawPassword).equalsIgnoreCase(encodedPassword.trim());
            }
        };
    }
}

