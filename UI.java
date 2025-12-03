import java.util.ArrayList;

public class UI {
    private Menu initial;

    public UI() {
    }

    public UI(Menu initialMode) {
        initial = initialMode;
    }

    public void setInitialMode(Menu mode) {
        initial = mode;
    }

    public void run() {
        initial.capture();
    }
}