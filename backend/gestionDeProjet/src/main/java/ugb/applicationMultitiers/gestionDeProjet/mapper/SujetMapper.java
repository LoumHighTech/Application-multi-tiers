package ugb.applicationMultitiers.gestionDeProjet.mapper;

import ugb.applicationMultitiers.gestionDeProjet.dto.SujetRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.SujetResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import org.springframework.stereotype.Component;

@Component
public class SujetMapper {

    public void applyRequest(SujetRequest req, Sujet s) {
        s.setTitre(req.titre().trim());
        s.setDescription(req.description() == null || req.description().isBlank() ? null : req.description().trim());
        s.setCapaciteMax(req.capaciteMax());
    }

    /** À appeler dans une transaction (accès aux candidatures et à l'enseignant du sujet). */
    public SujetResponse toResponse(Sujet s) {
        return new SujetResponse(
                s.getId(), s.getTitre(), s.getDescription(), s.getCapaciteMax(), s.isDisponible(),
                s.getDateCreation(),
                s.getEnseignant().getId(),
                s.getEnseignant().getPrenom() + " " + s.getEnseignant().getNom(),
                s.getCandidatures().size(),
                (int) s.nombreAcceptees(),
                s.placesRestantes());
    }
}
