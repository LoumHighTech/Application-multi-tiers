package ugb.applicationMultitiers.gestionDeProjet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "candidature",
        uniqueConstraints = @UniqueConstraint(name = "uq_candidature",
                columnNames = {"etudiant_id", "sujet_id"}))
@Getter
@Setter
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    @Column(name = "date_candidature", nullable = false)
    private LocalDate dateCandidature;

    @Column(name = "date_decision")
    private LocalDate dateDecision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Statut statut = Statut.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sujet_id", nullable = false)
    private Sujet sujet;

    /** Responsable pédagogique qui a traité la candidature (0..1). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private ResponsablePedagogique responsable;

    @PrePersist
    void prePersist() {
        if (dateCandidature == null) {
            dateCandidature = LocalDate.now();
        }
        if (statut == null) {
            statut = Statut.EN_ATTENTE;
        }
    }

    /** Les règles métier (capacité, statut courant...) sont vérifiées dans CandidatureService (Membre 2). */
    public void accepter() {
        this.statut = Statut.ACCEPTEE;
        this.dateDecision = LocalDate.now();
    }

    public void rejeter() {
        this.statut = Statut.REJETEE;
        this.dateDecision = LocalDate.now();
    }
}
