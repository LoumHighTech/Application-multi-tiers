package ugb.applicationMultitiers.gestionDeProjet.dto;

import ugb.applicationMultitiers.gestionDeProjet.entity.Role;

public record EnseignantResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String departement,
        Role role) {
}
