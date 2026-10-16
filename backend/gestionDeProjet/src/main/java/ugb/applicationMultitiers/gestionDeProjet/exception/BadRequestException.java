package ugb.applicationMultitiers.gestionDeProjet.exception;

/** -> 400 (réutilisable par le Membre 2 pour les règles métier) */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
