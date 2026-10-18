package ugb.applicationMultitiers.gestionDeProjet.dto;

import ugb.applicationMultitiers.gestionDeProjet.entity.Role;

public record EtudiantResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String numeroEtudiant,
        String filiere,
        Role role) {
}
