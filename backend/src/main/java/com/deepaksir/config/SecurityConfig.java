.authorizeHttpRequests(auth -> auth
    // Public endpoints
    .requestMatchers("/auth/**", "/public/**").permitAll()
    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
    
    // Admin-only endpoints
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/courses/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/courses/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/courses/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/subjects/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/subjects/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/subjects/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/questions/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/questions/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/questions/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/notes/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT, "/notes/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/notes/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/presentations/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/videos/upload").hasRole("ADMIN")
    
    // Premium content - enrolled users only
    .requestMatchers("/premium/**").authenticated()
    .requestMatchers("/classroom/**").hasAnyRole("ADMIN", "INSTRUCTOR")
    
    // Everything else requires authentication
    .anyRequest().authenticated()
)