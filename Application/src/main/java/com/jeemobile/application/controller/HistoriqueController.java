package com.jeemobile.application.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jeemobile.application.entity.Historique;
import com.jeemobile.application.enums.TypeAction;
import com.jeemobile.application.security.UserSession;
import com.jeemobile.application.service.HistoriqueService;

/**
 * Consultation des journaux d'activité (historique) de l'usine
 * connectée. Lecture seule — les entrées sont créées uniquement via
 * HistoriqueService.logAction/logStock/logCommande par les modules
 * eux-mêmes, jamais directement par un utilisateur.
 */
@Controller
@RequestMapping("/admin/logs")
public class HistoriqueController {

    private final HistoriqueService historiqueService;
    private final UserSession userSession;

    public HistoriqueController(HistoriqueService historiqueService, UserSession userSession) {
        this.historiqueService = historiqueService;
        this.userSession = userSession;
    }

    @GetMapping
    public String lister(
            @RequestParam(required = false) TypeAction type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Historique> logs = historiqueService.rechercherAvecFiltres(
                userSession.getUsineId(), type, debut, fin, pageable);

        model.addAttribute("logs", logs);
        model.addAttribute("types", TypeAction.values());
        model.addAttribute("typeSelectionne", type);
        model.addAttribute("debut", debut);
        model.addAttribute("fin", fin);

        return "admin/logs";
    }
}