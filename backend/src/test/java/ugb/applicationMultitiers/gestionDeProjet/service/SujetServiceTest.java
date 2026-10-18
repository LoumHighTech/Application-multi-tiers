package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.SujetRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.SujetResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Candidature;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;
import ugb.applicationMultitiers.gestionDeProjet.exception.BadRequestException;
import ugb.applicationMultitiers.gestionDeProjet.exception.BusinessConflictException;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.mapper.SujetMapper;
import ugb.applicationMultitiers.gestionDeProjet.repository.CandidatureRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.SujetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class SujetServiceTest {

    @Mock
    private SujetRepository sujetRepository;
    @Mock
    private CandidatureRepository candidatureRepository;
    @Mock
    private CurrentUserService currentUser;

    private SujetService service;

    private Enseignant proprietaire;
    private Enseignant autreEnseignant;

    @BeforeEach
    void setUp() {
        service = new SujetService(sujetRepository, candidatureRepository, currentUser, new SujetMapper());
        proprietaire = TestData.enseignant(1L);
        autreEnseignant = TestData.enseignant(2L);
    }

    @Test
    @DisplayName("Création : le sujet est rattaché à l'enseignant connecté et disponible")
    void create_rattacheALEnseignantConnecte() {
        when(currentUser.enseignant()).thenReturn(proprietaire);
        when(sujetRepository.save(any(Sujet.class))).thenAnswer(inv -> inv.getArgument(0));

        SujetResponse res = service.create(new SujetRequest("  Application web  ", "Desc", 2));

        ArgumentCaptor<Sujet> captor = ArgumentCaptor.forClass(Sujet.class);
        verify(sujetRepository).save(captor.capture());
        Sujet saved = captor.getValue();
        assertThat(saved.getEnseignant()).isSameAs(proprietaire);
        assertThat(saved.isDisponible()).isTrue();
        assertThat(saved.getTitre()).isEqualTo("Application web");
        assertThat(res.capaciteMax()).isEqualTo(2);
        assertThat(res.enseignantId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Modification : le propriétaire peut modifier son sujet")
    void update_proprietaire_ok() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(proprietaire);
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(0L);
        when(sujetRepository.save(any(Sujet.class))).thenAnswer(inv -> inv.getArgument(0));

        SujetResponse res = service.update(10L, new SujetRequest("Nouveau titre", "Nouvelle desc", 3));

        assertThat(res.titre()).isEqualTo("Nouveau titre");
        assertThat(res.capaciteMax()).isEqualTo(3);
        assertThat(res.disponible()).isTrue();
    }

    @Test
    @DisplayName("Modification : un autre enseignant est refusé (403)")
    void update_autreEnseignant_refuse() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(autreEnseignant);

        assertThatThrownBy(() -> service.update(10L, new SujetRequest("X", null, 2)))
                .isInstanceOf(AccessDeniedException.class);
        verify(sujetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Modification : sujet introuvable (404)")
    void update_introuvable() {
        when(sujetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new SujetRequest("X", null, 2)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Modification : capacité inférieure aux candidatures acceptées refusée (400)")
    void update_capaciteInferieureAuxAcceptees() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 3, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(proprietaire);
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(2L);

        assertThatThrownBy(() -> service.update(10L, new SujetRequest("X", null, 1)))
                .isInstanceOf(BadRequestException.class);
        verify(sujetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Capacité : un sujet complet redevient disponible si la capacité est augmentée")
    void update_augmenterCapacite_reactiveLeSujet() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, false);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(proprietaire);
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(2L);
        when(sujetRepository.save(any(Sujet.class))).thenAnswer(inv -> inv.getArgument(0));

        SujetResponse res = service.update(10L, new SujetRequest("Sujet 10", "Description", 3));

        assertThat(res.disponible()).isTrue();
    }

    @Test
    @DisplayName("Capacité : un sujet reste indisponible si la capacité est exactement atteinte")
    void update_capaciteEgaleAuxAcceptees_resteIndisponible() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 3, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(proprietaire);
        when(candidatureRepository.countBySujetIdAndStatut(10L, Statut.ACCEPTEE)).thenReturn(2L);
        when(sujetRepository.save(any(Sujet.class))).thenAnswer(inv -> inv.getArgument(0));

        SujetResponse res = service.update(10L, new SujetRequest("Sujet 10", "Description", 2));

        assertThat(res.disponible()).isFalse();
    }

    @Test
    @DisplayName("Suppression : le propriétaire supprime un sujet sans candidature")
    void delete_proprietaire_ok() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(proprietaire);

        service.delete(10L);

        verify(sujetRepository).delete(sujet);
    }

    @Test
    @DisplayName("Suppression : un autre enseignant est refusé (403)")
    void delete_autreEnseignant_refuse() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(autreEnseignant);

        assertThatThrownBy(() -> service.delete(10L)).isInstanceOf(AccessDeniedException.class);
        verify(sujetRepository, never()).delete(any(Sujet.class));
    }

    @Test
    @DisplayName("Suppression : un sujet ayant des candidatures ne peut pas être supprimé (409)")
    void delete_sujetAvecCandidatures_conflit() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        Candidature c = TestData.candidature(1L, TestData.etudiant(5L), sujet, Statut.EN_ATTENTE);
        sujet.getCandidatures().add(c);
        when(sujetRepository.findById(10L)).thenReturn(Optional.of(sujet));
        when(currentUser.enseignant()).thenReturn(proprietaire);

        assertThatThrownBy(() -> service.delete(10L)).isInstanceOf(BusinessConflictException.class);
        verify(sujetRepository, never()).delete(any(Sujet.class));
    }

    @Test
    @DisplayName("Mes sujets : uniquement ceux de l'enseignant connecté")
    void findMesSujets_filtreParEnseignant() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        when(currentUser.enseignant()).thenReturn(proprietaire);
        when(sujetRepository.findByEnseignantId(1L)).thenReturn(List.of(sujet));

        List<SujetResponse> res = service.findMesSujets();

        assertThat(res).hasSize(1);
        assertThat(res.get(0).id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Sujets disponibles : délègue au filtre disponible = true")
    void findDisponibles() {
        Sujet sujet = TestData.sujet(10L, proprietaire, 2, true);
        when(sujetRepository.findByDisponibleTrue()).thenReturn(List.of(sujet));

        assertThat(service.findDisponibles()).extracting(SujetResponse::disponible).containsOnly(true);
    }
}
