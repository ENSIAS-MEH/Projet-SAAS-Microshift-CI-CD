package com.jeemobile.application.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeemobile.application.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Utilisé par l'authentification : un username est unique par usine,
     * pas globalement (uq_user_usine dans le schéma).
     */
    Optional<User> findByUsineIdAndUsername(Integer usineId, String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsineIdAndUsername(Integer usineId, String username);

    boolean existsByEmail(String email);

    Page<User> findByUsineIdAndActifTrue(Integer usineId, Pageable pageable);

    Page<User> findByUsineId(Integer usineId, Pageable pageable);
}