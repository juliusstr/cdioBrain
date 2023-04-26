package exceptions;

public class NoGoodCircleData extends Throwable{
    public NoGoodCircleData(){
        super();
    }

    public NoGoodCircleData(String message){
        super(message);
    }
}
