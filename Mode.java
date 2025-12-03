import java.util.ArrayList;
import java.util.Scanner;

public class Mode {
    final private String name;
    final private ArrayList<Mode> submodes = new ArrayList<>();
    private Runnable callback;
    private static final Scanner scanner = new Scanner(System.in);

    public Mode(String modeName) {
        name = modeName;
    }

    public Mode(String modeName, Runnable cb) {
        name = modeName;
        callback = cb;
    }

    public Mode addSubMode(Mode submode) {
        submodes.add(submode);
        return this;
    }

    public Mode addSubMode(Mode[] all) {
        for (Mode submode : all) {
            submodes.add(submode);
        }
        return this;
    }

    public Mode addSubMode(String newname) {
        Mode newMode = new Mode(newname);
        submodes.add(newMode);
        return newMode;
    }

    public Mode addSubMode(String newname, Runnable cb) {
        Mode newMode = new Mode(newname, cb);
        submodes.add(newMode);
        return newMode;
    }

    public void setCallback(Runnable cb) {
        callback = cb;
    }

    public String getName() {
        return name;
    }

    public void listModes() {
        System.out.println("\n 0. ↶ Back");
        for (int i = 0; i < submodes.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, submodes.get(i).getName());
        }
    }

    public void capture() {
        // if child exits we want to run again
        ProgramContext.pushBreadcrumb(name);
        System.out.print("\033[2J\033[H");

        // maybe optional bool for inside/outside loop?

        while (true) {
            System.out.print("\033[2J\033[H"); // Clear screen
            String bc = ProgramContext.getBreadcrumb();
            System.out.println(bc + " │\n" + "─".repeat(bc.length()+1) + "╯");
            if (callback != null)
                callback.run();
            
            if (submodes.size() == 0) return;
            
            
            //TODO: print current user info?
            
            listModes();
            String msg = ProgramContext.getStatusMessage();
            if (msg != null) {
                System.out.println("\n"+msg);
                ProgramContext.clearStatusMessage();
            } else {
                System.out.println("\n");
            }
            System.out.print(">> "); // prompt
            
            try {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue; // empty inp

                if(line.trim().equals("exit")) System.exit(0);

                int choice = Integer.parseInt(line);

                if (choice == 0) {
                    return;
                }

                if (choice > 0 && choice <= submodes.size()) {
                    submodes.get(choice - 1).capture(); // child
                    ProgramContext.popBreadcrumb();
                } else {
                    ProgramContext.setStatusMessage("Invalid selection.",
                        ProgramContext.Color.RED);
                }
            } catch (NumberFormatException e) {
                ProgramContext.setStatusMessage("Please enter a valid number.",
                    ProgramContext.Color.RED);
            } catch (Exception e) {
                ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(),
                    ProgramContext.Color.RED);
            }
        }
    }
}
