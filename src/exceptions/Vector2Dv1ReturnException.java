package exceptions;

import misc.Vector2Dv1;

public class Vector2Dv1ReturnException extends Throwable{
    public Vector2Dv1 vector2D;
    public Vector2Dv1ReturnException(){
        super();
    }

    public Vector2Dv1ReturnException(String message){
        super(message);
    }

    public Vector2Dv1ReturnException(Vector2Dv1 vector2D) {
        super();
        this.vector2D = vector2D;
    }
}
