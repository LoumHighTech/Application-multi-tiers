package ugb.applicationMultitiers.gestionDeProjet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "etudiant")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
public class Etudiant extends Utilisateur {

    @Column(name = "numero_etudiant", nullable = false, unique = true, length = 30)
    private String numeroEtudiant;

    @Column(nullable = false, length = 100)
    private String filiere;

    public Etudiant() {
        setRole(Role.ETUDIANT);
    }
}
