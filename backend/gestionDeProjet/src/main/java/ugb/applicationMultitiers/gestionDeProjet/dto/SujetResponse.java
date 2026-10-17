package ugb.applicationMultitiers.gestionDeProjet.dto;

import java.time.LocalDate;

public record SujetResponse(
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
