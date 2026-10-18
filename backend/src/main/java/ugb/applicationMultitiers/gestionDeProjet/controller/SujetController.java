package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.SujetRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.SujetResponse;
import ugb.applicationMultitiers.gestionDeProjet.service.SujetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sujets")
@RequiredArgsConstructor
@Tag(name = "Sujets")
public class SujetController {

    private final SujetService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    @Operation(summary = "Tous les sujets")
    public List<SujetResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Sujets disponibles (candidatures ouvertes)")
    public List<SujetResponse> findDisponibles() {
        return service.findDisponibles();
    }

    @GetMapping("/mes-sujets")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @Operation(summary = "Sujets de l'enseignant connecté")
    public List<SujetResponse> findMesSujets() {
        return service.findMesSujets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','ENSEIGNANT','ETUDIANT')")
    @Operation(summary = "Détail d'un sujet")
    public SujetResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @Operation(summary = "Créer un sujet")
    public SujetResponse create(@Valid @RequestBody SujetRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @Operation(summary = "Modifier un sujet (propriétaire uniquement)")
    public SujetResponse update(@PathVariable Long id, @Valid @RequestBody SujetRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @Operation(summary = "Supprimer un sujet (propriétaire uniquement)")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
