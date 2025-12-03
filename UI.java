import java.util.ArrayList;

public class UI {
    private Mode initial;

    public UI(){}

    public UI(Mode initialMode){
        initial = initialMode;
    }

    public void setInitialMode(Mode mode){
        initial = mode;
    }

    public void run(){
        initial.capture();
    }
}