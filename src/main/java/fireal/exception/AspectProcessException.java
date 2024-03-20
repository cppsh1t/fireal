package fireal.exception;

public class AspectProcessException extends RuntimeException {

    public AspectProcessException(String message) {
        super(message);
    }

    public AspectProcessException(Object target) {
        super(target + " does not conform to the aspect format.");
    }

}
