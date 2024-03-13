package fireal.exception;

public class ConstantNotFoundException extends RuntimeException{

    public ConstantNotFoundException() {

    }

    public ConstantNotFoundException(String name) {
        super("Can't find Constant which named " + name + ".");
    }

}
