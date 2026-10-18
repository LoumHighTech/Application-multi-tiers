package ugb.applicationMultitiers.gestionDeProjet.config;

import ugb.applicationMultitiers.gestionDeProjet.entity.Administrateur;
import ugb.applicationMultitiers.gestionDeProjet.repository.AdministrateurRepository;
import ugb.applicationMultitiers.gestionDeProjet.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements ApplicationRunner {

    private final AdministrateurRepository administrateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (utilisateurRepository.existsByEmail(normalizedEmail)) {
            return;
        }

        Administrateur admin = new Administrateur();
        admin.setNom("Administrateur");
        admin.setPrenom("Systeme");
        admin.setEmail(normalizedEmail);
        admin.setMotDePasse(passwordEncoder.encode(adminPassword));
        administrateurRepository.save(admin);
    }
}