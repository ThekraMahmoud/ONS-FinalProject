package org.example.ons.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/map/kiosks/{kioskId}/nearest-place"
                                ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/reports/self-service",
                                "/report/self-service",
                                "/api/v1/campaigns/add",
                                "/api/v1/payments/campaign/{campaignId}",
                                "/api/v1/payments/campaign/{campaignId}/{paymentId}/verify"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/authorized-staff/create/{campaignId}",
                                "/api/v1/authorized-staff/get-all",
                                "/api/v1/authorized-staff/get/{id:\\d+}",
                                "/api/v1/authorized-staff/update/{id:\\d+}",
                                "/api/v1/authorized-staff/delete/{id:\\d+}",
                                "/notification/add",
                                "/notification/get",
                                "/notification/get/{id}",
                                "/notification/update/{id}",
                                "/notification/delete/{id}"
                        ).hasAuthority("ADMIN")

                        .requestMatchers(
                                "/api/v1/authorized-staff/**"
                        ).hasAuthority("AuthorizedStaff")

//                        .requestMatchers("").hasAuthority("FIELDـSTAFF")
//
//                        .requestMatchers("").hasAnyAuthority(
//                                "ADMIN", "AuthorizedStaff"
//                        )
//
//                        .requestMatchers("").hasAnyAuthority(
//                                "ADMIN", "FIELDـSTAFF"
//                        )
//
//                        .requestMatchers("").hasAnyAuthority(
//                                "ADMIN", "AuthorizedStaff", "FIELDـSTAFF"
//                        )

                        .requestMatchers(
                                "/api/v1/reports",
                                "/api/v1/reports/**",
                                "/report",
                                "/report/**","/api/v1/map/markers",
                                "/api/v1/map/nearest-kiosk",
                                "/api/v1/map/route"
                        ).hasAnyAuthority(
                                "ADMIN",
                                "AuthorizedStaff",
                                "FIELDـSTAFF"
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