package exceptions;

public class NoWaypointException extends Throwable{

    public NoWaypointException(){
        super();
    }

    public NoWaypointException(String message){
        super(message);
    }

}
