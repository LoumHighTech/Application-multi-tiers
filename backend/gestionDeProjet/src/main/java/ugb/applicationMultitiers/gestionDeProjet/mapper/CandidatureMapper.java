package ugb.applicationMultitiers.gestionDeProjet.mapper;

import ugb.applicationMultitiers.gestionDeProjet.dto.AffectationResponse;
import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Candidature;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import org.springframework.stereotype.Component;

@Component
public class CandidatureMapper {

    /** À appeler dans une transaction (associations LAZY : étudiant, sujet, enseignant, responsable). */
    public CandidatureResponse toResponse(Candidature c) {
        Etudiant e = c.getEtudiant();
        Sujet s = c.getSujet();
        Enseignant ens = s.getEnseignant();
        return new CandidatureResponse(
                c.getId(),
                s.getId(), s.getTitre(), ens.getPrenom() + " " + ens.getNom(),
                e.getId(), e.getPrenom() + " " + e.getNom(), e.getNumeroEtudiant(), e.getFiliere(),
                c.getMotivation(), c.getStatut(), c.getDateCandidature(), c.getDateDecision(),
                c.getResponsable() == null ? null : c.getResponsable().getPrenom() + " " + c.getResponsable().getNom());
    }

    public AffectationResponse toAffectation(Candidature c) {
        Etudiant e = c.getEtudiant();
        Sujet s = c.getSujet();
        Enseignant ens = s.getEnseignant();
        return new AffectationResponse(
                c.getId(),
                e.getId(), e.getPrenom() + " " + e.getNom(), e.getNumeroEtudiant(), e.getFiliere(),
                s.getId(), s.getTitre(),
                ens.getId(), ens.getPrenom() + " " + ens.getNom(),
                c.getDateDecision());
    }
}
