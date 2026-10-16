package ugb.applicationMultitiers.gestionDeProjet.mapper;

import ugb.applicationMultitiers.gestionDeProjet.dto.ProjetAdminResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import org.springframework.stereotype.Component;

@Component
public class ProjetMapper {

    /** À appeler dans une transaction (accès aux candidatures du sujet). */
    public ProjetAdminResponse toAdminResponse(Sujet s) {
        return new ProjetAdminResponse(
                s.getId(), s.getTitre(), s.getDescription(), s.getCapaciteMax(), s.isDisponible(),
                s.getDateCreation(),
                s.getEnseignant().getId(),
                s.getEnseignant().getPrenom() + " " + s.getEnseignant().getNom(),
                s.getCandidatures().size(),
                (int) s.nombreAcceptees(),
                s.placesRestantes());
    }
}
