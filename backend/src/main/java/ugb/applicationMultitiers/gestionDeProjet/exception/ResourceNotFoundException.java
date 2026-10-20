package ugb.applicationMultitiers.gestionDeProjet.exception;

/** -> 404 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
