package ugb.applicationMultitiers.gestionDeProjet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** motDePasse : obligatoire à la création, facultatif à la modification (inchangé si absent). */
public record EtudiantRequest(
        @NotBlank(message = "Le nom est obligatoire") @Size(max = 100) String nom,
        @NotBlank(message = "Le prénom est obligatoire") @Size(max = 100) String prenom,
        @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") @Size(max = 150) String email,
        @Size(min = 8, max = 100, message = "Le mot de passe doit contenir au moins 8 caractères") String motDePasse,
        @NotBlank(message = "Le numéro d'étudiant est obligatoire") @Size(max = 30) String numeroEtudiant,
        @NotBlank(message = "La filière est obligatoire") @Size(max = 100) String filiere) {
}
