package com.jeemobile.application.security;

import com.jeemobile.application.dao.UserRepository;
import com.jeemobile.application.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Charge un utilisateur pour Spring Security au moment du login.
 *
 * NOTE MULTI-TENANT : Spring Security n'a qu'un seul champ "username"
 * dans son formulaire de login standard, alors que l'unicité réelle
 * est (usine_id, username). Pour l'instant, on recherche par username
 * seul en supposant qu'il est globalement unique dans l'usage réel de
 * l'app (un seul tenant actif par login). Si plusieurs usines doivent
 * partager des noms d'utilisateur identiques, il faudra adapter le
 * formulaire de login pour inclure l'usine (ex: sous-domaine ou champ
 * dédié) — à documenter comme limitation connue.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + username));
        return new UserDetailsImpl(user);
    }
}