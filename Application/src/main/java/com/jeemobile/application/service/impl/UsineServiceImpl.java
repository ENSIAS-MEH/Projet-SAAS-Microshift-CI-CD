package com.jeemobile.application.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeemobile.application.dao.UsineRepository;
import com.jeemobile.application.entity.Usine;
import com.jeemobile.application.exception.RessourceIntrouvableException;
import com.jeemobile.application.service.UsineService;

@Service
public class UsineServiceImpl implements UsineService {

    private final UsineRepository usineRepository;

    public UsineServiceImpl(UsineRepository usineRepository) {
        this.usineRepository = usineRepository;
    }

    @Override
    @Transactional
    public Usine creerUsine(String nom, String namespaceK8s) {
        Usine usine = new Usine(nom, namespaceK8s);
        return usineRepository.save(usine);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usine> trouverParId(Integer id) {
        return usineRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usine> trouverParNamespace(String namespaceK8s) {
        return usineRepository.findByNamespaceK8s(namespaceK8s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usine> listerToutes() {
        return usineRepository.findAll();
    }

    @Override
    @Transactional
    public Usine activer(Integer id) {
        Usine usine = usineRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Usine introuvable, id=" + id));
        usine.setActif(true);
        return usineRepository.save(usine);
    }

    @Override
    @Transactional
    public Usine desactiver(Integer id) {
        Usine usine = usineRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Usine introuvable, id=" + id));
        usine.setActif(false);
        return usineRepository.save(usine);
    }
}
