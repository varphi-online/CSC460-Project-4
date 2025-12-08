import java.util.ArrayList;

public class Menu {
    final private String name;
    final private ArrayList<Menu> submenus = new ArrayList<>();
    private Runnable callback;

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
            System.out.printf("%2d. %s%s%n", i + 1, submenus.get(i).getName(),
                    submenus.get(i).submenus.isEmpty() ? "" : " ▷");
        }
    }

    public void capture() {
        // if child exits we want to run again
        ProgramContext.pushBreadcrumb(name);
        System.out.print("\033[2J\033[H");

        if (submenus.isEmpty()) { // "leaf" no submenus so just call callback
            System.out.print("\033[2J\033[H"); // Clear
            String bc = ProgramContext.getBreadcrumb();
            System.out.println(bc + " │\n" + "─".repeat(bc.length() + 1) + "╯");
            try{
                if (callback != null) {
                    callback.run();
                }
                System.out.print("\n(Press Enter to continue...)");
                Prog4.getScanner().nextLine();
            } catch (Exception e){
                return;
            }
            return; // Return to prev
        }

        while (true) {
            System.out.print("\033[2J\033[H"); // Clear screen
            String bc = ProgramContext.getBreadcrumb();
            System.out.println(bc + " │\n" + "─".repeat(bc.length() + 1) + "╯");
            if (callback != null) // use for headers and stuff
                callback.run();

            if (submenus.size() == 0)
                return;

            // TODO: print current user info?

            listMenus();
            String msg = ProgramContext.getStatusMessage();
            if (msg != null) {
                System.out.println("\n" + msg);
                ProgramContext.clearStatusMessage();
            } else {
                System.out.println("\n");
            }
            System.out.print(">> "); // prompt

            try {
                String line = Prog4.getScanner().nextLine();
                if (line.trim().isEmpty())
                    continue; // empty inp

                if (line.trim().equals("exit"))
                    try {
                        DB.db.close();
                    } catch (Exception e) {} finally {
                        System.exit(0);
                    }

                int choice = Integer.parseInt(line);

                if (choice == 0) {
                    return;
                }

                if (choice > 0 && choice <= submenus.size()) {
                    try {
                        submenus.get(choice - 1).capture(); // child
                        ProgramContext.popBreadcrumb();
                    } catch (Exception e) {
                        ProgramContext.popBreadcrumb();
                        continue;
                    }
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
