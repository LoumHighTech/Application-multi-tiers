package ugb.applicationMultitiers.gestionDeProjet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enseignant")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
public class Enseignant extends Utilisateur {

    @Column(nullable = false, length = 100)
    private String departement;

    public Enseignant() {
        setRole(Role.ENSEIGNANT);
    }
}
