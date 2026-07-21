package com.jeemobile.application.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeemobile.application.entity.Usine;

@Repository
public interface UsineRepository extends JpaRepository<Usine, Integer> {

    Optional<Usine> findByNamespaceK8s(String namespaceK8s);

    Optional<Usine> findByActifTrue();
}
