import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    final private String name;
    final private ArrayList<Menu> submenus = new ArrayList<>();
    private Runnable callback;
    private static final Scanner scanner = new Scanner(System.in);

    public Menu(String menuName) {
        name = menuName;
    }

    public Menu(String menuName, Runnable cb) {
        name = menuName;
        callback = cb;
    }

    public Menu addSubMenu(Menu sumMenu) {
        submenus.add(sumMenu);
        return this;
    }

    public Menu addSubMenu(Menu[] all) {
        for (Menu sumMenu : all) {
            submenus.add(sumMenu);
        }
        return this;
    }

    public Menu addSubMenu(String newname) {
        Menu newMenu = new Menu(newname);
        submenus.add(newMenu);
        return newMenu;
    }

    public Menu addSubMenu(String newname, Runnable cb) {
        Menu newMenu = new Menu(newname, cb);
        submenus.add(newMenu);
        return newMenu;
    }

    public void setCallback(Runnable cb) {
        callback = cb;
    }

    public String getName() {
        return name;
    }

    public void listMenus() {
        System.out.println("\n 0. ↶ Back");
        for (int i = 0; i < submenus.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, submenus.get(i).getName());
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
            
            if (submenus.size() == 0) return;
            
            
            //TODO: print current user info?
            
            listMenus();
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

                if (choice > 0 && choice <= submenus.size()) {
                    submenus.get(choice - 1).capture(); // child
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
