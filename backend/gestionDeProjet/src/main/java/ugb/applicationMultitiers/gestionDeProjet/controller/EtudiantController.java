package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.EtudiantRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.EtudiantResponse;
import ugb.applicationMultitiers.gestionDeProjet.service.EtudiantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService service;

    @GetMapping
    public List<EtudiantResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EtudiantResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EtudiantResponse create(@Valid @RequestBody EtudiantRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public EtudiantResponse update(@PathVariable Long id, @Valid @RequestBody EtudiantRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
