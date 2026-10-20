package ugb.applicationMultitiers.gestionDeProjet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** L'étudiant est déduit du token JWT : seul le sujet visé est fourni. */
public record CandidatureRequest(
        @NotNull(message = "Le sujet est obligatoire") Long sujetId,
        @Size(max = 2000, message = "La motivation ne doit pas dépasser 2000 caractères") String motivation) {
}
