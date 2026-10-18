package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.LoginRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.LoginResponse;
import ugb.applicationMultitiers.gestionDeProjet.entity.Utilisateur;
import ugb.applicationMultitiers.gestionDeProjet.exception.ResourceNotFoundException;
import ugb.applicationMultitiers.gestionDeProjet.repository.UtilisateurRepository;
import ugb.applicationMultitiers.gestionDeProjet.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    /** Lève BadCredentialsException (-> 401) si les identifiants sont incorrects. */
    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        UserDetails principal = (UserDetails) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, req.motDePasse()))
                .getPrincipal();
        Utilisateur u = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        String token = jwtService.generateToken(principal);
        return new LoginResponse(token, "Bearer", u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getRole());
    }
}
