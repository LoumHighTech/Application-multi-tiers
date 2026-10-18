package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import ugb.applicationMultitiers.gestionDeProjet.service.CandidatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Contrôleur fin : aucune logique métier, tout est dans CandidatureService. */
@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
@Tag(name = "Candidatures")
public class CandidatureController {

    private final CandidatureService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Déposer une candidature")
    public CandidatureResponse deposer(@Valid @RequestBody CandidatureRequest request) {
        return service.deposer(request);
    }

    @GetMapping("/mes-candidatures")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Mes candidatures et leur statut")
    public List<CandidatureResponse> mesCandidatures() {
        return service.mesCandidatures();
    }

    @GetMapping("/sujet/{sujetId}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @Operation(summary = "Candidatures reçues sur un de mes sujets")
    public List<CandidatureResponse> parSujet(@PathVariable Long sujetId) {
        return service.parSujet(sujetId);
    }

    @GetMapping
    @PreAuthorize("hasRole('RESPONSABLE')")
    @Operation(summary = "Toutes les candidatures (filtre optionnel ?statut=EN_ATTENTE|ACCEPTEE|REJETEE)")
    public List<CandidatureResponse> toutes(@RequestParam(required = false) Statut statut) {
        return service.toutes(statut);
    }

    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('RESPONSABLE')")
    @Operation(summary = "Accepter une candidature EN_ATTENTE")
    public CandidatureResponse accepter(@PathVariable Long id) {
        return service.accepter(id);
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasRole('RESPONSABLE')")
    @Operation(summary = "Rejeter une candidature EN_ATTENTE")
    public CandidatureResponse rejeter(@PathVariable Long id) {
        return service.rejeter(id);
    }
}
