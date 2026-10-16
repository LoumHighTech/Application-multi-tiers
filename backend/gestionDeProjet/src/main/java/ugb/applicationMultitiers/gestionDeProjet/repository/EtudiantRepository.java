package ugb.applicationMultitiers.gestionDeProjet.repository;

import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    boolean existsByNumeroEtudiant(String numeroEtudiant);

    boolean existsByNumeroEtudiantAndIdNot(String numeroEtudiant, Long id);
}
