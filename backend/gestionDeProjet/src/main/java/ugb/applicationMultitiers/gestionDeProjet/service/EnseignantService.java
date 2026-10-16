package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.EnseignantRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.EnseignantResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.exception.BadRequestException;
import ugb.applicationMultitiers.gestionDeProjet.exception.DuplicateResourceException;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.mapper.EnseignantMapper;
import ugb.applicationMultitiers.gestionDeProjet.repository.EnseignantRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnseignantService {

    private final EnseignantRepository enseignantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EnseignantMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EnseignantResponse> findAll() {
        return enseignantRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EnseignantResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    public EnseignantResponse create(EnseignantRequest req) {
        if (req.motDePasse() == null || req.motDePasse().isBlank()) {
            throw new BadRequestException("Le mot de passe est obligatoire à la création");
        }
        String email = req.email().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Cet email est déjà utilisé : " + email);
        }
        Enseignant enseignant = mapper.toEntity(req);
        enseignant.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        return mapper.toResponse(enseignantRepository.save(enseignant));
    }

    public EnseignantResponse update(Long id, EnseignantRequest req) {
        Enseignant enseignant = getOrThrow(id);
        String email = req.email().trim().toLowerCase();
        if (utilisateurRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Cet email est déjà utilisé : " + email);
        }
        mapper.updateEntity(req, enseignant);
        if (req.motDePasse() != null && !req.motDePasse().isBlank()) {
            enseignant.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        }
        return mapper.toResponse(enseignantRepository.save(enseignant));
    }

    public void delete(Long id) {
        Enseignant enseignant = getOrThrow(id);
        enseignantRepository.delete(enseignant);
        enseignantRepository.flush(); // violation FK (enseignant ayant des sujets) -> 409
    }

    private Enseignant getOrThrow(Long id) {
        return enseignantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable (id=" + id + ")"));
    }
}
