package exceptions;

public class NoRouteException extends Throwable{

    public NoRouteException(){
        super();
    }

    public NoRouteException(String message){
        super(message);
    }
}
