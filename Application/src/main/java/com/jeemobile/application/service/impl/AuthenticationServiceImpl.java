package com.jeemobile.application.service.impl;

import com.jeemobile.application.dao.UserRepository;
import com.jeemobile.application.entity.User;
import com.jeemobile.application.exception.CompteDesactiveException;
import com.jeemobile.application.exception.CompteVerrouilleException;
import com.jeemobile.application.exception.IdentifiantsInvalidesException;
import com.jeemobile.application.service.AuthenticationService;
import com.jeemobile.application.service.HistoriqueService;
import com.jeemobile.application.enums.TypeAction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int MAX_TENTATIVES = 5;
    private static final int DUREE_VERROUILLAGE_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HistoriqueService historiqueService;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      HistoriqueService historiqueService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.historiqueService = historiqueService;
    }

    @Override
    @Transactional
    public User authentifier(String username, String motDePasseEnClair) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();

        if (userOpt.isEmpty()) {
            throw new IdentifiantsInvalidesException("Identifiants invalides.");
        }

        User user = userOpt.get();

        if (!Boolean.TRUE.equals(user.getActif())) {
            throw new CompteDesactiveException("Ce compte est désactivé.");
        }

        if (user.getVerrouilleJusqua() != null && user.getVerrouilleJusqua().isAfter(LocalDateTime.now())) {
            throw new CompteVerrouilleException("Compte verrouillé suite à trop de tentatives échouées. Réessayez plus tard.");
        }

        if (!passwordEncoder.matches(motDePasseEnClair, user.getPassword())) {
            enregistrerEchecConnexion(username);
            historiqueService.logAction(user.getUsine().getId(), user.getId(),
                    "Tentative de connexion échouée pour " + username, TypeAction.SECURITE);
            throw new IdentifiantsInvalidesException("Identifiants invalides.");
        }

        reinitialiserTentatives(user.getId());
        historiqueService.logAction(user.getUsine().getId(), user.getId(),
                "Connexion réussie", TypeAction.USER);

        return user;
    }

    @Override
    @Transactional
    public void enregistrerEchecConnexion(String username) {
        userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .ifPresent(user -> {
                    int tentatives = user.getTentativesEchouees() == null ? 0 : user.getTentativesEchouees();
                    tentatives++;
                    user.setTentativesEchouees(tentatives);
                    if (tentatives >= MAX_TENTATIVES) {
                        user.setVerrouilleJusqua(LocalDateTime.now().plusMinutes(DUREE_VERROUILLAGE_MINUTES));
                    }
                    userRepository.save(user);
                });
    }

    @Override
    @Transactional
    public void reinitialiserTentatives(Integer userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setTentativesEchouees(0);
            user.setVerrouilleJusqua(null);
            userRepository.save(user);
        });
    }
}