package ugb.applicationMultitiers.gestionDeProjet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "administrateur")
@PrimaryKeyJoinColumn(name = "id")
public class Administrateur extends Utilisateur {

    public Administrateur() {
        setRole(Role.ADMIN);
    }
}
