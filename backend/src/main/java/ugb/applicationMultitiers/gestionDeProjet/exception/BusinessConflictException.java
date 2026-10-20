package ugb.applicationMultitiers.gestionDeProjet.exception;

/**
 * Règle métier violée alors que la requête est syntaxiquement correcte -> 409 Conflict
 * (sujet indisponible, capacité atteinte, candidature déjà traitée...).
 * Hérite de DuplicateResourceException : le GlobalExceptionHandler existant (Membre 1)
 * la traduit déjà en 409, sans modifier ce fichier.
 */
public class BusinessConflictException extends DuplicateResourceException {
    public BusinessConflictException(String message) {
        super(message);
    }
}
