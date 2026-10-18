package ugb.applicationMultitiers.gestionDeProjet.config;

import ugb.applicationMultitiers.gestionDeProjet.entity.ResponsablePedagogique;
import ugb.applicationMultitiers.gestionDeProjet.repository.ResponsablePedagogiqueRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Crée un responsable pédagogique de test au premier démarrage (aucune API ne permet d'en créer un).
 * Même principe que DefaultAdminInitializer. À remplacer par le jeu de test de Membre 1 s'il en fournit un.
 * Identifiants par défaut : responsable@local.test / Resp123! (variables RESPONSABLE_EMAIL, RESPONSABLE_PASSWORD).
 */
@Component
@RequiredArgsConstructor
public class DefaultResponsableInitializer implements ApplicationRunner {

    private final ResponsablePedagogiqueRepository responsableRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.responsable.email:${RESPONSABLE_EMAIL:responsable@local.test}}")
    private String responsableEmail;

    @Value("${app.responsable.password:${RESPONSABLE_PASSWORD:Resp123!}}")
    private String responsablePassword;

    @Override
    public void run(ApplicationArguments args) {
        String email = responsableEmail.trim().toLowerCase(Locale.ROOT);
        if (utilisateurRepository.existsByEmail(email)) {
            return;
        }
        ResponsablePedagogique responsable = new ResponsablePedagogique();
        responsable.setNom("Pedagogique");
        responsable.setPrenom("Responsable");
        responsable.setEmail(email);
        responsable.setMotDePasse(passwordEncoder.encode(responsablePassword));
        responsableRepository.save(responsable);
    }
}
