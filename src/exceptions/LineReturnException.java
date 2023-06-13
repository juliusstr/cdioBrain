package exceptions;

import misc.Line;

public class LineReturnException extends Throwable{
    public Line line;
    public LineReturnException(){
        super();
    }

    public LineReturnException(String message){
        super(message);
    }

    public LineReturnException(Line line) {
        super();
        this.line = line;
    }
}
