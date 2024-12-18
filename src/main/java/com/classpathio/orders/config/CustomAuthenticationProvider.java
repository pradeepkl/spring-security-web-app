package com.classpathio.orders.config;

import com.classpathio.orders.model.User;
import com.classpathio.orders.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider  implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        if(user.isAccountLocked()){
            throw new IllegalArgumentException("Account is locked");
        }
        if(!this.passwordEncoder.matches(password, user.getPassword())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if(user.getFailedAttempts() >= 3) {
                user.setAccountLocked(true);
            }
            this.userRepository.save(user);
            throw new IllegalArgumentException("Invalid password");
        } else {
            user.setFailedAttempts(0);
            this.userRepository.save(user);
            return new UsernamePasswordAuthenticationToken(user, password, user.getRoles().stream().map(role -> role.getRoleName().name()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        if(authentication.isInstance(UsernamePasswordAuthenticationToken.class)) {
            return true;
        }
        return false;
    }
}
