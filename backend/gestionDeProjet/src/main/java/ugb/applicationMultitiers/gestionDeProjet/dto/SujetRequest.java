package ugb.applicationMultitiers.gestionDeProjet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Création / modification d'un sujet. La disponibilité est calculée par le serveur, jamais envoyée par le client. */
public record SujetRequest(
        @NotBlank(message = "Le titre est obligatoire") @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères") String titre,
        @Size(max = 5000, message = "La description ne doit pas dépasser 5000 caractères") String description,
        @NotNull(message = "La capacité maximale est obligatoire") @Min(value = 1, message = "La capacité maximale doit être au moins 1") Integer capaciteMax) {
}
