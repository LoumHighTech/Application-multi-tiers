package ugb.applicationMultitiers.gestionDeProjet.dto;

import ugb.applicationMultitiers.gestionDeProjet.entity.Role;

public record LoginResponse(
        String token,
        String type,
        Long id,
        String nom,
        String prenom,
        String email,
        Role role) {
}
