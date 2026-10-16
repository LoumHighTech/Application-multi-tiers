package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.EtudiantRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.EtudiantResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import ugb.applicationMultitiers.gestionDeProjet.exception.BadRequestException;
import ugb.applicationMultitiers.gestionDeProjet.exception.DuplicateResourceException;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.mapper.EtudiantMapper;
import ugb.applicationMultitiers.gestionDeProjet.repository.EtudiantRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EtudiantResponse> findAll() {
        return etudiantRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EtudiantResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    public EtudiantResponse create(EtudiantRequest req) {
        if (req.motDePasse() == null || req.motDePasse().isBlank()) {
            throw new BadRequestException("Le mot de passe est obligatoire à la création");
        }
        String email = req.email().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Cet email est déjà utilisé : " + email);
        }
        if (etudiantRepository.existsByNumeroEtudiant(req.numeroEtudiant().trim())) {
            throw new DuplicateResourceException("Ce numéro d'étudiant existe déjà : " + req.numeroEtudiant());
        }
        Etudiant etudiant = mapper.toEntity(req);
        etudiant.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        return mapper.toResponse(etudiantRepository.save(etudiant));
    }

    public EtudiantResponse update(Long id, EtudiantRequest req) {
        Etudiant etudiant = getOrThrow(id);
        String email = req.email().trim().toLowerCase();
        if (utilisateurRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Cet email est déjà utilisé : " + email);
        }
        if (etudiantRepository.existsByNumeroEtudiantAndIdNot(req.numeroEtudiant().trim(), id)) {
            throw new DuplicateResourceException("Ce numéro d'étudiant existe déjà : " + req.numeroEtudiant());
        }
        mapper.updateEntity(req, etudiant);
        if (req.motDePasse() != null && !req.motDePasse().isBlank()) {
            etudiant.setMotDePasse(passwordEncoder.encode(req.motDePasse()));
        }
        return mapper.toResponse(etudiantRepository.save(etudiant));
    }

    public void delete(Long id) {
        Etudiant etudiant = getOrThrow(id);
        etudiantRepository.delete(etudiant);
        etudiantRepository.flush(); // déclenche tout de suite une éventuelle violation de clé étrangère (-> 409)
    }

    private Etudiant getOrThrow(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable (id=" + id + ")"));
    }
}
