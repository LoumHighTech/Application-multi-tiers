package ugb.applicationMultitiers.gestionDeProjet.exception;

/** -> 409 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
