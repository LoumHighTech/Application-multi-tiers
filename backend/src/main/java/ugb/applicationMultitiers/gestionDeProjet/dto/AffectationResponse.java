package ugb.applicationMultitiers.gestionDeProjet.dto;

import java.time.LocalDate;

/** Une affectation = une candidature ACCEPTEE (étudiant <-> sujet). */
public record AffectationResponse(
        Long candidatureId,
        Long etudiantId,
        String etudiantNom,
        String numeroEtudiant,
        String filiere,
        Long sujetId,
        String sujetTitre,
        Long enseignantId,
        String enseignantNom,
        LocalDate dateDecision) {
}
