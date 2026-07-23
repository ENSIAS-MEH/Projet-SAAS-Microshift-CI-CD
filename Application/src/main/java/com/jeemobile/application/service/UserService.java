package com.jeemobile.application.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jeemobile.application.entity.User;

public interface UserService {

    User creerUtilisateur(Integer usineId, String username, String motDePasseEnClair,
                           String nom, String contact, String email);

    User modifierUtilisateur(Integer userId, String nom, String contact, String email);

    User changerMotDePasse(Integer userId, String nouveauMotDePasseEnClair);

    User activer(Integer userId);

    User desactiver(Integer userId);

    void supprimer(Integer userId);

    Optional<User> trouverParId(Integer userId);

    Optional<User> trouverParUsernameEtUsine(Integer usineId, String username);

    Page<User> listerParUsine(Integer usineId, Pageable pageable);

    Page<User> listerActifsParUsine(Integer usineId, Pageable pageable);

    boolean usernameExisteDansUsine(Integer usineId, String username);

    boolean emailExiste(String email);
}
