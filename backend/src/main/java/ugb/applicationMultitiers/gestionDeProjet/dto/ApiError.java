package ugb.applicationMultitiers.gestionDeProjet.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Format d'erreur JSON commun à toute l'API. */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> details) {

    public record FieldViolation(String field, String message) {
    }

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, List.of());
    }
}
