package exceptions;

public class NoDataException extends Throwable{
    public NoDataException(){
        super();
    }

    public NoDataException(String message){
        super(message);
    }
}
