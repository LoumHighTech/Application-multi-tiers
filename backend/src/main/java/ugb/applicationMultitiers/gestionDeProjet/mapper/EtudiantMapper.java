package ugb.applicationMultitiers.gestionDeProjet.mapper;

import ugb.applicationMultitiers.gestionDeProjet.dto.EtudiantRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.EtudiantResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import org.springframework.stereotype.Component;

@Component
public class EtudiantMapper {

    /** Le mot de passe est encodé par le service, pas par le mapper. */
    public Etudiant toEntity(EtudiantRequest req) {
        Etudiant e = new Etudiant();
        updateEntity(req, e);
        return e;
    }

    public void updateEntity(EtudiantRequest req, Etudiant e) {
        e.setNom(req.nom().trim());
        e.setPrenom(req.prenom().trim());
        e.setEmail(req.email().trim().toLowerCase());
        e.setNumeroEtudiant(req.numeroEtudiant().trim());
        e.setFiliere(req.filiere().trim());
    }

    public EtudiantResponse toResponse(Etudiant e) {
        return new EtudiantResponse(e.getId(), e.getNom(), e.getPrenom(), e.getEmail(),
                e.getNumeroEtudiant(), e.getFiliere(), e.getRole());
    }
}
