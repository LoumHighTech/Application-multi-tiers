package ugb.applicationMultitiers.gestionDeProjet.repository;

import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SujetRepository extends JpaRepository<Sujet, Long> {

    List<Sujet> findByEnseignantId(Long enseignantId);

    List<Sujet> findByDisponibleTrue();

    /** Tous les sujets avec leur enseignant et leurs candidatures (consultation admin). */
    @Query("select distinct s from Sujet s join fetch s.enseignant left join fetch s.candidatures order by s.id")
    List<Sujet> findAllAvecDetails();
}
