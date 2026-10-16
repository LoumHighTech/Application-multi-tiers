package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.ProjetAdminResponse;
import ugb.applicationMultitiers.gestionDeProjet.service.ProjetAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Consultation globale des projets (lecture seule) pour l'administrateur. */
@RestController
@RequestMapping("/api/admin/projets")
@RequiredArgsConstructor
public class AdminProjetController {

    private final ProjetAdminService service;

    @GetMapping
    public List<ProjetAdminResponse> findAll() {
        return service.findAll();
    }
}
