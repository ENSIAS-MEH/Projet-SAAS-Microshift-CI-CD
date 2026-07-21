package com.jeemobile.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jeemobile.application.entity.Roles;
import com.jeemobile.application.entity.User;
import com.jeemobile.application.entity.Usine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class Module3SmokeTest implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        Usine usine = new Usine("Usine Demo", "usine-demo");
        em.persist(usine);
        em.flush();

        User u = new User(usine, "test.module3", "$2a$10$fakehashfortestonly", "Test User", "test.module3@demo.local");
        u.setRoles(new Roles());
        u.getRoles().setPeutAjouterProduit(true);
        em.persist(u);
        em.flush();

        System.out.println("OK — Usine id=" + usine.getId() + ", User id=" + u.getId() + ", Roles id=" + u.getRoles().getId());
    }
}