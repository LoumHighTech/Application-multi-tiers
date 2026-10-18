package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.CandidatureResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Candidature;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import ugb.applicationMultitiers.gestionDeProjet.entity.ResponsablePedagogique;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import ugb.applicationMultitiers.gestionDeProjet.exception.BusinessConflictException;
import ugb.applicationMultitiers.gestionDeProjet.exception.DuplicateResourceException;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.mapper.CandidatureMapper;
import ugb.applicationMultitiers.gestionDeProjet.repository.CandidatureRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.SujetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidatureServiceTest {

    @Mock
    private CandidatureRepository candidatureRepository;
    @Mock
    private SujetRepository sujetRepository;
    @Mock
    private CurrentUserService currentUser;

    private CandidatureService service;

    private Enseignant enseignant;
    private Etudiant etudiant;
    private ResponsablePedagogique responsable;

    @BeforeEach
    void setUp() {
        service = new CandidatureService(candidatureRepository, sujetRepository, currentUser, new CandidatureMapper());
        enseignant = TestData.enseignant(1L);
        etudiant = TestData.etudiant(5L);
        responsable = TestData.responsable(9L);
    }

    // ---------------------------------------------------------------- dépôt

    @Test
    @DisplayName("Dépôt : la candidature est créée avec le statut EN_ATTENTE")
    void deposer_ok_statutEnAttente() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        when(currentUser.etudiant()).thenReturn(etudiant);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(candidatureRepository.existsByEtudiantIdAndSujetId(5L, 10L)).thenReturn(false);
        when(candidatureRepository.save(any(Candidature.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidatureResponse res = service.deposer(new CandidatureRequest(10L, "Je suis motivé"));

        assertThat(res.statut()).isEqualTo(Statut.EN_ATTENTE);
        assertThat(res.etudiantId()).isEqualTo(5L);
        assertThat(res.sujetId()).isEqualTo(10L);
        assertThat(res.motivation()).isEqualTo("Je suis motivé");
    }

    @Test
    @DisplayName("Règle : candidature unique par (étudiant, sujet) -> 409")
    void deposer_doublon_conflit() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        when(currentUser.etudiant()).thenReturn(etudiant);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(candidatureRepository.existsByEtudiantIdAndSujetId(5L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.deposer(new CandidatureRequest(10L, null)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(candidatureRepository, never()).save(any());
    }

    @Test
    @DisplayName("Règle : impossible de candidater à un sujet indisponible -> 409")
    void deposer_sujetIndisponible_conflit() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, false);
        when(currentUser.etudiant()).thenReturn(etudiant);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(candidatureRepository.existsByEtudiantIdAndSujetId(5L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> service.deposer(new CandidatureRequest(10L, null)))
                .isInstanceOf(BusinessConflictException.class)
                .hasMessageContaining("disponible");
        verify(candidatureRepository, never()).save(any());
    }

    @Test
    @DisplayName("Dépôt : sujet introuvable -> 404")
    void deposer_sujetIntrouvable() {
        when(currentUser.etudiant()).thenReturn(etudiant);
        when(sujetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deposer(new CandidatureRequest(99L, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ------------------------------------------------------------- acceptation

    @Test
    @DisplayName("Acceptation : EN_ATTENTE -> ACCEPTEE, responsable renseigné, sujet encore disponible")
    void accepter_ok_sujetResteDisponible() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(c));
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(0L);
        when(currentUser.responsable()).thenReturn(responsable);
        when(candidatureRepository.save(any(Candidature.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidatureResponse res = service.accepter(1L);

        assertThat(res.statut()).isEqualTo(Statut.ACCEPTEE);
        assertThat(res.dateDecision()).isNotNull();
        assertThat(c.getResponsable()).isSameAs(responsable);
        assertThat(sujet.isDisponible()).isTrue();
    }

    @Test
    @DisplayName("Règle : le sujet passe à indisponible quand la capacité est atteinte")
    void accepter_derniereplace_sujetDevientIndisponible() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(c));
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(1L);
        when(currentUser.responsable()).thenReturn(responsable);
        when(candidatureRepository.save(any(Candidature.class))).thenAnswer(inv -> inv.getArgument(0));

        service.accepter(1L);

        assertThat(sujet.isDisponible()).isFalse();
        verify(sujetRepository).save(sujet);
    }

    @Test
    @DisplayName("Règle : on ne peut pas accepter au-delà de capaciteMax -> 409")
    void accepter_capaciteAtteinte_conflit() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, false);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(c));
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(2L);

        assertThatThrownBy(() -> service.accepter(1L)).isInstanceOf(BusinessConflictException.class);
        assertThat(c.getStatut()).isEqualTo(Statut.EN_ATTENTE);
        verify(candidatureRepository, never()).save(any());
    }

    @Test
    @DisplayName("Règle : seule une candidature EN_ATTENTE peut être acceptée -> 409")
    void accepter_dejaTraitee_conflit() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.REJETEE);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> service.accepter(1L)).isInstanceOf(BusinessConflictException.class);
        assertThat(c.getStatut()).isEqualTo(Statut.REJETEE);
    }

    @Test
    @DisplayName("Acceptation : candidature introuvable -> 404")
    void accepter_introuvable() {
        when(candidatureRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.accepter(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    // ------------------------------------------------------------------ rejet

    @Test
    @DisplayName("Rejet : EN_ATTENTE -> REJETEE, la capacité du sujet n'est pas touchée")
    void rejeter_ok() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(c));
        when(currentUser.responsable()).thenReturn(responsable);
        when(candidatureRepository.save(any(Candidature.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidatureResponse res = service.rejeter(1L);

        assertThat(res.statut()).isEqualTo(Statut.REJETEE);
        assertThat(c.getResponsable()).isSameAs(responsable);
        assertThat(sujet.isDisponible()).isTrue();
        verify(sujetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Règle : seule une candidature EN_ATTENTE peut être rejetée -> 409")
    void rejeter_dejaAcceptee_conflit() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.ACCEPTEE);
        when(candidatureRepository.findById(1L)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> service.rejeter(1L)).isInstanceOf(BusinessConflictException.class);
        assertThat(c.getStatut()).isEqualTo(Statut.ACCEPTEE);
    }

    // ---------------------------------------------------------- consultations

    @Test
    @DisplayName("Candidatures d'un sujet : le propriétaire y a accès")
    void parSujet_proprietaire_ok() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(enseignant);
        when(candidatureRepository.findBySujetId(10L)).thenReturn(List.of(c));

        assertThat(service.parSujet(10L)).hasSize(1);
    }

    @Test
    @DisplayName("Candidatures d'un sujet : un autre enseignant est refusé (403)")
    void parSujet_autreEnseignant_refuse() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(TestData.enseignant(2L));

        assertThatThrownBy(() -> service.parSujet(10L)).isInstanceOf(AccessDeniedException.class);
        verify(candidatureRepository, never()).findBySujetId(any());
    }

    @Test
    @DisplayName("Mes candidatures : uniquement celles de l'étudiant connecté")
    void mesCandidatures_filtreParEtudiant() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(currentUser.etudiant()).thenReturn(etudiant);
        when(candidatureRepository.findByEtudiantId(5L)).thenReturn(List.of(c));

        assertThat(service.mesCandidatures()).extracting(CandidatureResponse::etudiantId).containsOnly(5L);
    }

    @Test
    @DisplayName("Affectations : correspondent aux candidatures ACCEPTEE")
    void affectations_candidaturesAcceptees() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature c = TestData.candidature(1L, etudiant, sujet, Statut.ACCEPTEE);
        when(candidatureRepository.findByStatut(Statut.ACCEPTEE)).thenReturn(List.of(c));

        assertThat(service.affectations()).hasSize(1);
        assertThat(service.affectations().get(0).sujetTitre()).isEqualTo("Sujet 10");
    }

    @Test
    @DisplayName("Liste du responsable : filtre par statut si fourni, sinon tout")
    void toutes_avecEtSansFiltre() {
        Sujet sujet = TestData.sujet(10L, enseignant, 2, true);
        Candidature attente = TestData.candidature(1L, etudiant, sujet, Statut.EN_ATTENTE);
        when(candidatureRepository.findAll()).thenReturn(List.of(attente));
        when(candidatureRepository.findByStatut(Statut.EN_ATTENTE)).thenReturn(List.of(attente));

        assertThat(service.toutes(null)).hasSize(1);
        assertThat(service.toutes(Statut.EN_ATTENTE)).hasSize(1);
        verify(candidatureRepository).findByStatut(Statut.EN_ATTENTE);
    }
}
