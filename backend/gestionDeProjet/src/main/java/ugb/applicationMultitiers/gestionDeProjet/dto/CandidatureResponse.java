package ugb.applicationMultitiers.gestionDeProjet.dto;

import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;

import java.time.LocalDate;

public record CandidatureResponse(
        Long id,
        Long sujetId,
        String sujetTitre,
        String enseignantNom,
        Long etudiantId,
        String etudiantNom,
        String numeroEtudiant,
        String filiere,
        String motivation,
        Statut statut,
        LocalDate dateCandidature,
        LocalDate dateDecision,
        String responsableNom) {
}
