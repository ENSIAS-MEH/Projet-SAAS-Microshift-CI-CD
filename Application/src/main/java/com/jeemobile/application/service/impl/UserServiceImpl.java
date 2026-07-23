package com.jeemobile.application.service.impl;

import com.jeemobile.application.dao.UserRepository;
import com.jeemobile.application.entity.Roles;
import com.jeemobile.application.entity.User;
import com.jeemobile.application.entity.Usine;
import com.jeemobile.application.exception.RessourceIntrouvableException;
import com.jeemobile.application.service.UserService;
import com.jeemobile.application.service.UsineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final int LONGUEUR_MIN_MOT_DE_PASSE = 8;

    private final UserRepository userRepository;
    private final UsineService usineService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UsineService usineService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.usineService = usineService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User creerUtilisateur(Integer usineId, String username, String motDePasseEnClair,
                                  String nom, String contact, String email) {
        validerUsername(username);
        validerMotDePasse(motDePasseEnClair);
        validerEmail(email);

        Usine usine = usineService.trouverParId(usineId)
                .orElseThrow(() -> new RessourceIntrouvableException("Usine introuvable, id=" + usineId));

        if (usernameExisteDansUsine(usineId, username)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà pour cette usine.");
        }
        if (StringUtils.hasText(email) && emailExiste(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        String hash = passwordEncoder.encode(motDePasseEnClair);
        User user = new User(usine, username, hash, nom, email);
        user.setContact(contact);
        user.setRoles(new Roles());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User modifierUtilisateur(Integer userId, String nom, String contact, String email) {
        User user = trouverParIdOuLever(userId);

        if (StringUtils.hasText(email) && !email.equalsIgnoreCase(user.getEmail()) && emailExiste(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        user.setNom(nom);
        user.setContact(contact);
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User changerMotDePasse(Integer userId, String nouveauMotDePasseEnClair) {
        validerMotDePasse(nouveauMotDePasseEnClair);
        User user = trouverParIdOuLever(userId);
        user.setPassword(passwordEncoder.encode(nouveauMotDePasseEnClair));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User activer(Integer userId) {
        User user = trouverParIdOuLever(userId);
        user.setActif(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User desactiver(Integer userId) {
        User user = trouverParIdOuLever(userId);
        user.setActif(false);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void supprimer(Integer userId) {
        User user = trouverParIdOuLever(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> trouverParId(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> trouverParUsernameEtUsine(Integer usineId, String username) {
        return userRepository.findByUsineIdAndUsername(usineId, username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> listerParUsine(Integer usineId, Pageable pageable) {
        return userRepository.findByUsineId(usineId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> listerActifsParUsine(Integer usineId, Pageable pageable) {
        return userRepository.findByUsineIdAndActifTrue(usineId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExisteDansUsine(Integer usineId, String username) {
        return userRepository.existsByUsineIdAndUsername(usineId, username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExiste(String email) {
        return userRepository.existsByEmail(email);
    }

    private User trouverParIdOuLever(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable, id=" + userId));
    }

    private void validerUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Le nom d'utilisateur est requis.");
        }
    }

    private void validerMotDePasse(String motDePasse) {
        if (!StringUtils.hasText(motDePasse) || motDePasse.length() < LONGUEUR_MIN_MOT_DE_PASSE) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins " + LONGUEUR_MIN_MOT_DE_PASSE + " caractères.");
        }
    }

    private void validerEmail(String email) {
        if (StringUtils.hasText(email) && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Format d'email invalide.");
        }
    }
}