package exceptions;

import misc.Vector2Dv1;
import misc.Zone;

public class ZoneReturnException extends Throwable{
    public Zone zone;
    public ZoneReturnException(){
        super();
    }

    public ZoneReturnException(String message){
        super(message);
    }

    public ZoneReturnException(Zone zone) {
        super();
        this.zone = zone;
    }
}
