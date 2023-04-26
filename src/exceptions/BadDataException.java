package exceptions;

public class BadDataException extends Throwable{

    public BadDataException(){
        super();
    }

    public BadDataException(String message){
        super(message);
    }

}
