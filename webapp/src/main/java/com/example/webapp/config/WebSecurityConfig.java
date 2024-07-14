package com.example.webapp.config;

import com.example.webapp.entity.Usuario;
import com.example.webapp.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebSecurityConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());

    final UsuarioRepository usuarioRepository;
    final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource, UsuarioRepository usuarioRepository) {
        this.dataSource = dataSource;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/submitLoginForm")
                .successHandler((request, response, authentication) -> {
                    DefaultSavedRequest defaultSavedRequest =
                            (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", usuarioRepository.findByCorreo(authentication.getName()));

                    if (defaultSavedRequest != null) {
                        String targetURl = defaultSavedRequest.getRequestURL();
                        new DefaultRedirectStrategy().sendRedirect(request, response, targetURl);
                    } else {
                        String rol = "";
                        for (GrantedAuthority role : authentication.getAuthorities()) {
                            rol = role.getAuthority();
                            break;
                        }

                        if (rol.equals("Paciente")) {
                            response.sendRedirect("/paciente/inicio");
                        }
                        if (rol.equals("Superadmin")) {
                            response.sendRedirect("/superadmin/");
                        }
                        if (rol.equals("Farmacista")) {
                            response.sendRedirect("/farmacista/medicamentos");
                        }
                        if (rol.equals("Admin")) {
                            response.sendRedirect("/admin/paginainicio");
                        }
                    }
                })

                .failureHandler((request, response, exception) -> {
                    request.getSession().setAttribute("error", "Credenciales incorrectas. Inténtalo de nuevo.");
                    response.sendRedirect("/login");
                });

        http.authorizeHttpRequests()
                .requestMatchers("/paciente", "/paciente/**").hasAnyAuthority("Paciente")
                .requestMatchers("/superadmin", "/superadmin/**").hasAnyAuthority("Superadmin")
                .requestMatchers("/sistema", "/sistema/**").hasAnyAuthority("Superadmin")
                .requestMatchers("/submitLoginForm").hasAnyAuthority("Superadmin")
                .requestMatchers("/farmacista", "/farmacista/**").hasAnyAuthority("Farmacista")
                .requestMatchers("/admin", "/admin/**").hasAnyAuthority("Admin")
                .anyRequest().permitAll();

        http.logout()
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        http.csrf().disable(); // Deshabilitar CSRF temporalmente para pruebas

        return http.build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        //para loguearse sqlAuth -> correo | password | enable
        String sqlAuth = "SELECT correo, contrasena, estado FROM gticsbd.usuario where correo = ? AND cuenta_activada = 1";

        //para autenticación -> correo, nombre del rol
        String sqlAuto = "SELECT u.correo, r.nombre FROM gticsbd.usuario u \n"
                + "               inner join gticsbd.roles r on u.id_roles = r.id_roles \n"
                + "               where u.correo = ?";

        users.setUsersByUsernameQuery(sqlAuth);
        users.setAuthoritiesByUsernameQuery(sqlAuto);

        return users;
    }
}








