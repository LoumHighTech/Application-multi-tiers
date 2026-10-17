package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.AffectationResponse;
import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Candidature;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import ugb.applicationMultitiers.gestionDeProjet.entity.ResponsablePedagogique;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import ugb.applicationMultitiers.gestionDeProjet.exception.BusinessConflictException;
import ugb.applicationMultitiers.gestionDeProjet.exception.DuplicateResourceException;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.mapper.CandidatureMapper;
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
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final SujetRepository sujetRepository;
    private final CurrentUserService currentUser;
    private final CandidatureMapper mapper;

    /** ETUDIANT : dépose une candidature (statut initial EN_ATTENTE). */
    public CandidatureResponse deposer(CandidatureRequest req) {
        Etudiant etudiant = currentUser.etudiant();
        Sujet sujet = sujetRepository.findById(req.sujetId())
                .orElseThrow(() -> new ResourceNotFoundException("Sujet introuvable (id=" + req.sujetId() + ")"));

        // Règle : candidature unique par (étudiant, sujet) -> 409
        if (candidatureRepository.existsByEtudiantIdAndSujetId(etudiant.getId(), sujet.getId())) {
            throw new DuplicateResourceException("Vous avez déjà candidaté à ce sujet");
        }
        // Règle : impossible de candidater à un sujet indisponible -> 409
        if (!sujet.isDisponible()) {
            throw new BusinessConflictException("Ce sujet n'est plus disponible");
        }

        Candidature candidature = new Candidature();
        candidature.setEtudiant(etudiant);
        candidature.setSujet(sujet);
        candidature.setStatut(Statut.EN_ATTENTE);
        String motivation = req.motivation();
        candidature.setMotivation(motivation == null || motivation.isBlank() ? null : motivation.trim());
        return mapper.toResponse(candidatureRepository.save(candidature));
    }

    /** ETUDIANT : suivi de ses propres candidatures. */
    @Transactional(readOnly = true)
    public List<CandidatureResponse> mesCandidatures() {
        Etudiant etudiant = currentUser.etudiant();
        return candidatureRepository.findByEtudiantId(etudiant.getId()).stream().map(mapper::toResponse).toList();
    }

    /** ENSEIGNANT : candidatures reçues sur un de SES sujets (-> 403 sinon). */
    @Transactional(readOnly = true)
    public List<CandidatureResponse> parSujet(Long sujetId) {
        Sujet sujet = sujetRepository.findById(sujetId)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet introuvable (id=" + sujetId + ")"));
        Enseignant enseignant = currentUser.enseignant();
        if (!sujet.getEnseignant().getId().equals(enseignant.getId())) {
            throw new AccessDeniedException("Ce sujet appartient à un autre enseignant");
        }
        return candidatureRepository.findBySujetId(sujetId).stream().map(mapper::toResponse).toList();
    }

    /** RESPONSABLE : toutes les candidatures, filtre optionnel par statut. */
    @Transactional(readOnly = true)
    public List<CandidatureResponse> toutes(Statut statut) {
        List<Candidature> candidatures = statut == null
                ? candidatureRepository.findAll()
                : candidatureRepository.findByStatut(statut);
        return candidatures.stream().map(mapper::toResponse).toList();
    }

    /** RESPONSABLE : accepte une candidature EN_ATTENTE dans la limite de la capacité du sujet. */
    public CandidatureResponse accepter(Long id) {
        Candidature candidature = getOrThrow(id);
        verifierEnAttente(candidature);

        Sujet sujet = candidature.getSujet();
        long dejaAcceptees = candidatureRepository.countBySujetIdAndStatut(sujet.getId(), Statut.ACCEPTEE);
        // Règle : on ne dépasse jamais capaciteMax -> 409
        if (dejaAcceptees >= sujet.getCapaciteMax()) {
            throw new BusinessConflictException("La capacité maximale du sujet est déjà atteinte");
        }

        ResponsablePedagogique responsable = currentUser.responsable();
        candidature.accepter();
        candidature.setResponsable(responsable);

        // Règle : le sujet devient indisponible dès que la capacité est atteinte
        if (dejaAcceptees + 1 >= sujet.getCapaciteMax()) {
            sujet.setDisponible(false);
            sujetRepository.save(sujet);
        }
        return mapper.toResponse(candidatureRepository.save(candidature));
    }

    /** RESPONSABLE : rejette une candidature EN_ATTENTE. */
    public CandidatureResponse rejeter(Long id) {
        Candidature candidature = getOrThrow(id);
        verifierEnAttente(candidature);

        ResponsablePedagogique responsable = currentUser.responsable();
        candidature.rejeter();
        candidature.setResponsable(responsable);
        return mapper.toResponse(candidatureRepository.save(candidature));
    }

    /** RESPONSABLE : liste des affectations (= candidatures ACCEPTEES). */
    @Transactional(readOnly = true)
    public List<AffectationResponse> affectations() {
        return candidatureRepository.findByStatut(Statut.ACCEPTEE).stream().map(mapper::toAffectation).toList();
    }

    private Candidature getOrThrow(Long id) {
        return candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable (id=" + id + ")"));
    }

    /** Règle : seule une candidature EN_ATTENTE peut être acceptée ou rejetée -> 409. */
    private void verifierEnAttente(Candidature candidature) {
        if (candidature.getStatut() != Statut.EN_ATTENTE) {
            throw new BusinessConflictException("Cette candidature a déjà été traitée (statut : " + candidature.getStatut() + ")");
        }
    }
}
