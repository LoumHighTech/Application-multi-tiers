package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.AffectationResponse;
import ugb.applicationMultitiers.gestionDeProjet.service.CandidatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
@Tag(name = "Affectations")
public class AffectationController {

    private final CandidatureService service;

    @GetMapping
    @PreAuthorize("hasRole('RESPONSABLE')")
    @Operation(summary = "Liste des affectations (candidatures acceptées)")
    public List<AffectationResponse> findAll() {
        return service.affectations();
    }
}
