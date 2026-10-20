package ugb.applicationMultitiers.gestionDeProjet.repository;

import ugb.applicationMultitiers.gestionDeProjet.entity.Candidature;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    boolean existsByEtudiantIdAndSujetId(Long etudiantId, Long sujetId);

    List<Candidature> findByEtudiantId(Long etudiantId);

    List<Candidature> findBySujetId(Long sujetId);

    long countBySujetIdAndStatut(Long sujetId, Statut statut);

    List<Candidature> findByStatut(Statut statut);
}
