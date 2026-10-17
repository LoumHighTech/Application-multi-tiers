package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import ugb.applicationMultitiers.gestionDeProjet.entity.ResponsablePedagogique;
import ugb.applicationMultitiers.gestionDeProjet.entity.Utilisateur;
import ugb.applicationMultitiers.gestionDeProjet.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Retrouve l'utilisateur connecté à partir du JWT (le "username" du token est l'email).
 * Permet aux services de ne jamais faire confiance à un identifiant envoyé par le client.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UtilisateurRepository utilisateurRepository;

    public Enseignant enseignant() {
        if (current() instanceof Enseignant e) {
            return e;
        }
        throw new AccessDeniedException("Réservé aux enseignants");
    }

    public Etudiant etudiant() {
        if (current() instanceof Etudiant e) {
            return e;
        }
        throw new AccessDeniedException("Réservé aux étudiants");
    }

    public ResponsablePedagogique responsable() {
        if (current() instanceof ResponsablePedagogique r) {
            return r;
        }
        throw new AccessDeniedException("Réservé au responsable pédagogique");
    }

    private Utilisateur current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }
        return utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Utilisateur introuvable"));
    }
}
