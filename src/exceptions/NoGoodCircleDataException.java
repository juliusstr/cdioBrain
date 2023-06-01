package exceptions;

public class NoGoodCircleDataException extends Throwable{
    public NoGoodCircleDataException(){
        super();
    }

    public NoGoodCircleDataException(String message){
        super(message);
    }
}
