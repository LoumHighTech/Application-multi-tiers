package ugb.applicationMultitiers.gestionDeProjet.mapper;

import ugb.applicationMultitiers.gestionDeProjet.dto.EnseignantRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.EnseignantResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import org.springframework.stereotype.Component;

@Component
public class EnseignantMapper {

    public Enseignant toEntity(EnseignantRequest req) {
        Enseignant e = new Enseignant();
        updateEntity(req, e);
        return e;
    }

    public void updateEntity(EnseignantRequest req, Enseignant e) {
        e.setNom(req.nom().trim());
        e.setPrenom(req.prenom().trim());
        e.setEmail(req.email().trim().toLowerCase());
        e.setDepartement(req.departement().trim());
    }

    public EnseignantResponse toResponse(Enseignant e) {
        return new EnseignantResponse(e.getId(), e.getNom(), e.getPrenom(), e.getEmail(),
                e.getDepartement(), e.getRole());
    }
}
