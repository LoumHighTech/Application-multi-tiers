package ugb.applicationMultitiers.gestionDeProjet.repository;

import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
}
