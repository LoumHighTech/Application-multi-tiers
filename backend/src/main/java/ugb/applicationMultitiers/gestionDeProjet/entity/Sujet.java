package ugb.applicationMultitiers.gestionDeProjet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sujet")
@Getter
@Setter
public class Sujet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "capacite_max", nullable = false)
    private int capaciteMax;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enseignant_id", nullable = false)
    private Enseignant enseignant;

    @OneToMany(mappedBy = "sujet")
    private List<Candidature> candidatures = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
    }

    /** Nombre de candidatures acceptées. */
    public long nombreAcceptees() {
        return candidatures.stream().filter(c -> c.getStatut() == Statut.ACCEPTEE).count();
    }

    public int placesRestantes() {
        return (int) Math.max(0, capaciteMax - nombreAcceptees());
    }

    public boolean estComplet() {
        return placesRestantes() == 0;
    }
}
