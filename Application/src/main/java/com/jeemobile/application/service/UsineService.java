package com.jeemobile.application.service;

import java.util.List;
import java.util.Optional;

import com.jeemobile.application.entity.Usine;

public interface UsineService {

    Usine creerUsine(String nom, String namespaceK8s);

    Optional<Usine> trouverParId(Integer id);

    Optional<Usine> trouverParNamespace(String namespaceK8s);

    List<Usine> listerToutes();

    Usine activer(Integer id);

    Usine desactiver(Integer id);
}