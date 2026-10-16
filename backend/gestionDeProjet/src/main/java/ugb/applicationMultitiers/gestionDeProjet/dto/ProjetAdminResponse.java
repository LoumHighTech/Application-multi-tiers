package ugb.applicationMultitiers.gestionDeProjet.dto;

import java.time.LocalDate;

/** Vue d'un projet (sujet) pour la consultation globale par l'administrateur. */
public record ProjetAdminResponse(
        Long id,
        String titre,
        String description,
        int capaciteMax,
        boolean disponible,
        LocalDate dateCreation,
        Long enseignantId,
        String enseignantNom,
        int nombreCandidatures,
        int nombreAcceptees,
        int placesRestantes) {
}
