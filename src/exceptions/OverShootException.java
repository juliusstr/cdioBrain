package exceptions;

public class OverShootException extends Throwable{

    public OverShootException(){
        super();
    }

    public OverShootException(String message){
        super(message);
    }
}
