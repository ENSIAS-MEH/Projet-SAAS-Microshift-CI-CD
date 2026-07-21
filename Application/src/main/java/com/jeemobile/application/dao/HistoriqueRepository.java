package com.jeemobile.application.dao;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeemobile.application.entity.Historique;
import com.jeemobile.application.enums.TypeAction;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Integer> {

    Page<Historique> findByUsineIdOrderByTimestampDesc(Integer usineId, Pageable pageable);

    Page<Historique> findByUsineIdAndTypeActionOrderByTimestampDesc(
            Integer usineId, TypeAction typeAction, Pageable pageable);

    Page<Historique> findByUsineIdAndUserIdOrderByTimestampDesc(
            Integer usineId, Integer userId, Pageable pageable);

    Page<Historique> findByUsineIdAndTimestampBetweenOrderByTimestampDesc(
            Integer usineId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}