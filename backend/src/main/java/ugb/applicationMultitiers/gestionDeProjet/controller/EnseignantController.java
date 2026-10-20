package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.EnseignantRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.EnseignantResponse;
import ugb.applicationMultitiers.gestionDeProjet.service.EnseignantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enseignants")
@RequiredArgsConstructor
public class EnseignantController {

    private final EnseignantService service;

    @GetMapping
    public List<EnseignantResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EnseignantResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnseignantResponse create(@Valid @RequestBody EnseignantRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public EnseignantResponse update(@PathVariable Long id, @Valid @RequestBody EnseignantRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
