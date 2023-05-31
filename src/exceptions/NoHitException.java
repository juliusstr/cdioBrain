package exceptions;

public class NoHitException extends Throwable{

    public NoHitException(){
        super();
    }

    public NoHitException(String message){
        super(message);
    }
}
