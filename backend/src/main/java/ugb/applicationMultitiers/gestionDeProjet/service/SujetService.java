package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.SujetRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.SujetResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import ugb.applicationMultitiers.gestionDeProjet.exception.BadRequestException;
import ugb.applicationMultitiers.gestionDeProjet.exception.BusinessConflictException;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.mapper.SujetMapper;
import ugb.applicationMultitiers.gestionDeProjet.repository.CandidatureRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.SujetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SujetService {

    private final SujetRepository sujetRepository;
    private final CandidatureRepository candidatureRepository;
    private final CurrentUserService currentUser;
    private final SujetMapper mapper;

    /** Tous les sujets (ADMIN, RESPONSABLE). */
    @Transactional(readOnly = true)
    public List<SujetResponse> findAll() {
        return sujetRepository.findAllAvecDetails().stream().map(mapper::toResponse).toList();
    }

    /** Sujets encore ouverts aux candidatures (ETUDIANT). */
    @Transactional(readOnly = true)
    public List<SujetResponse> findDisponibles() {
        return sujetRepository.findByDisponibleTrue().stream().map(mapper::toResponse).toList();
    }

    /** Sujets de l'enseignant connecté. */
    @Transactional(readOnly = true)
    public List<SujetResponse> findMesSujets() {
        Enseignant enseignant = currentUser.enseignant();
        return sujetRepository.findByEnseignantId(enseignant.getId()).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SujetResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    /** Le sujet est rattaché à l'enseignant connecté, jamais à un id fourni par le client. */
    public SujetResponse create(SujetRequest req) {
        Sujet sujet = new Sujet();
        mapper.applyRequest(req, sujet);
        sujet.setEnseignant(currentUser.enseignant());
        sujet.setDisponible(true);
        return mapper.toResponse(sujetRepository.save(sujet));
    }

    public SujetResponse update(Long id, SujetRequest req) {
        Sujet sujet = getOrThrow(id);
        verifierProprietaire(sujet);

        long acceptees = candidatureRepository.countBySujetIdAndStatut(id, Statut.ACCEPTEE);
        if (req.capaciteMax() < acceptees) {
            throw new BadRequestException("La capacité maximale ne peut pas être inférieure au nombre de candidatures déjà acceptées (" + acceptees + ")");
        }

        mapper.applyRequest(req, sujet);
        // Règle : un sujet est disponible tant que la capacité maximale n'est pas atteinte.
        sujet.setDisponible(acceptees < sujet.getCapaciteMax());
        return mapper.toResponse(sujetRepository.save(sujet));
    }

    public void delete(Long id) {
        Sujet sujet = getOrThrow(id);
        verifierProprietaire(sujet);
        if (!sujet.getCandidatures().isEmpty()) {
            throw new BusinessConflictException("Impossible de supprimer un sujet qui a déjà reçu des candidatures");
        }
        sujetRepository.delete(sujet);
    }

    private Sujet getOrThrow(Long id) {
        return sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet introuvable (id=" + id + ")"));
    }

    /** Règle : un enseignant ne modifie / supprime que ses propres sujets (-> 403). */
    private void verifierProprietaire(Sujet sujet) {
        Enseignant enseignant = currentUser.enseignant();
        if (!sujet.getEnseignant().getId().equals(enseignant.getId())) {
            throw new AccessDeniedException("Ce sujet appartient à un autre enseignant");
        }
    }
}
