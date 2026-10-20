package ugb.applicationMultitiers.gestionDeProjet.controller;

import ugb.applicationMultitiers.gestionDeProjet.dto.LoginRequest;
import ugb.applicationMultitiers.gestionDeProjet.dto.LoginResponse;
import ugb.applicationMultitiers.gestionDeProjet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
