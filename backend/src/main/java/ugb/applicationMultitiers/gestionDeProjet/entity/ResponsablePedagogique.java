package ugb.applicationMultitiers.gestionDeProjet.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "responsable_pedagogique")
@PrimaryKeyJoinColumn(name = "id")
public class ResponsablePedagogique extends Utilisateur {

    public ResponsablePedagogique() {
        setRole(Role.RESPONSABLE);
    }
}
