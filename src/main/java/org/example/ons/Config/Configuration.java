package org.example.ons.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
public class Configuration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                .authorizeHttpRequests(auth -> auth


                         .requestMatchers("").permitAll()

                         .requestMatchers("").hasAuthority("ADMIN")

                         .requestMatchers("").hasAuthority("AuthorizedStaff")

                         .requestMatchers("").hasAuthority("FIELDـSTAFF")

                         .requestMatchers("").hasAnyAuthority(
                                 "ADMIN", "AuthorizedStaff"
                         )

                         .requestMatchers("").hasAnyAuthority(
                                 "ADMIN", "FIELDـSTAFF"
                         )

                         .requestMatchers("").hasAnyAuthority(
                                 "ADMIN", "AuthorizedStaff", "FIELDـSTAFF"
                         )

                        .anyRequest().authenticated()
                )

                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .invalidateHttpSession(true)
                )

                .httpBasic(basic -> {})

                .build();
    }
}