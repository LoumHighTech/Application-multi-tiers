package ugb.applicationMultitiers.gestionDeProjet.repository;

import ugb.applicationMultitiers.gestionDeProjet.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
}
