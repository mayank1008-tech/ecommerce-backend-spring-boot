package com.example.ecommerce.Security.Config;

import com.example.ecommerce.Model.AppRoles;
import com.example.ecommerce.Model.Role;
import com.example.ecommerce.Model.User;
import com.example.ecommerce.Repository.RoleRepository;
import com.example.ecommerce.Repository.UserRepository;
import com.example.ecommerce.Security.Services.UserDetailsServiceImpl;
import com.example.ecommerce.Security.jwt.AuthEntryPointJwt;
import com.example.ecommerce.Security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Set;

@Configuration
@EnableWebSecurity

public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                //Unauth ko exception mai daal do AuthEntruyPointJwt mai
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                //Stateless bnanae ke liye
                .sessionManagement(session  //cookie nhi bnegi
                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(("/v3/api-docs/**")).permitAll()
                        .requestMatchers(("/swagger-ui/**")).permitAll()
                        .requestMatchers(("/api/test/**")).permitAll()
                        .requestMatchers(("/api/images/**")).permitAll()
                .anyRequest().authenticated());

        //This sets our dao as a worker for AuthManager at login
        http.authenticationProvider(daoAuthenticationProvider());   //Defined for calls at login page at AuthManager

        //Setting our filter before every other chod bhangra
        http.addFilterBefore(authJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        http.headers(headers ->   //To disable login on h2
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {    //BYpasses Every fuckinn spring filter
        return (web ->  web.ignoring().requestMatchers(
                "/configyration/ui",
                "/swagger-resources/**",
                "configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {

        return args -> {

            // --- 1. Create Roles (using your AppRoles enum) ---

            Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRoles.ROLE_USER)));

            Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRoles.ROLE_SELLER)));

            Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRoles.ROLE_ADMIN)));


            // --- 2. Define Role Sets ---

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // --- 3. Create Users (with correct constructor and method names) ---

            // Check if user exists (using correct "existsByUsername" method)
            if (!userRepository.existsByUsername("user1")) {
                // Use correct constructor: (username, password, email)
                User user1 = new User(
                        "user1",
                        passwordEncoder.encode("password123"), // 2. Password
                        "user1@example.com"                    // 3. Email
                );
                user1.setRoles(userRoles); // Set roles *before* saving
                userRepository.save(user1);  // Save only once
            }

            if (!userRepository.existsByUsername("seller1")) {
                User seller1 = new User(
                        "seller1",
                        passwordEncoder.encode("password123"), // 2. Password
                        "seller1@example.com"                  // 3. Email
                );
                seller1.setRoles(sellerRoles);
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("adminpass"), // 2. Password
                        "admin@example.com"                  // 3. Email
                );
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            }
        };
    }
}
