import java.util.ArrayList;

/*
 * Class Name: Menu
 *
 * Purpose:
 *      This class represents a console-based menu system. It supports nesting menus (submenus),
 *      executing callbacks (actions) when a menu is selected, and handling user input loop
 *      navigation via the capture() method. It relies on external classes ProgramContext,
 *      Prog4, and DB for state management and input handling.
 *
 * Public Class Constants and Variables:
 *      None.
 *
 * Constructors:
 *      Menu(String menuName)
 *      Menu(String menuName, Runnable cb)
 *
 * Implemented Class and Instance Methods:
 *      addSubMenu(Menu sumMenu)
 *      addSubMenu(Menu[] all)
 *      addSubMenu(String newname)
 *      addSubMenu(String newname, Runnable cb)
 *      setCallback(Runnable cb)
 *      getName()
 *      listMenus()
 *      capture()
 */
public class Menu {
    final private String name;                      // The display name of the menu
    final private ArrayList<Menu> submenus = new ArrayList<>(); // List containing child Menu objects
    private Runnable callback;                      // Functional interface to execute logic when this menu is active

    /*
     * Method Name: Menu (Constructor)
     * Purpose: Initializes a new Menu object with a specific name.
     * Pre-conditions: menuName should not be null.
     * Post-conditions: A new Menu object is created with an empty submenu list.
     * Return Value: None.
     * Parameters:
     *     menuName (Into) - The string title of the menu to be displayed.
     */
    public Menu(String menuName) {
        name = menuName;
    }

    /*
     * Method Name: Menu (Constructor)
     * Purpose: Initializes a new Menu object with a name and an executable callback.
     * Pre-conditions: menuName and cb should not be null.
     * Post-conditions: A new Menu object is created with a defined callback.
     * Return Value: None.
     * Parameters:
     *     menuName (Into) - The string title of the menu.
     *     cb (Into) - The Runnable code block to execute when this menu is accessed.
     */
    public Menu(String menuName, Runnable cb) {
        name = menuName;
        callback = cb;
    }

    /*
     * Method Name: addSubMenu
     * Purpose: Adds an existing Menu object to the list of submenus for this menu.
     * Pre-conditions: sumMenu must be a valid Menu object.
     * Post-conditions: The sumMenu is added to the submenus list.
     * Return Value: Menu (This instance, to allow for method chaining).
     * Parameters:
     *     sumMenu (Into) - The existing Menu object to add as a child.
     */
    public Menu addSubMenu(Menu sumMenu) {
        submenus.add(sumMenu);
        return this;
    }

    /*
     * Method Name: addSubMenu
     * Purpose: Adds an array of Menu objects to the submenus list.
     * Pre-conditions: all must be a valid array of Menu objects.
     * Post-conditions: All elements of the array are added to the submenus list.
     * Return Value: Menu (This instance, to allow for method chaining).
     * Parameters:
     *     all (Into) - An array of Menu objects to add.
     */
    public Menu addSubMenu(Menu[] all) {
        for (Menu sumMenu : all) {
            submenus.add(sumMenu);
        }
        return this;
    }

    /*
     * Method Name: addSubMenu
     * Purpose: Creates a new Menu object by name and adds it as a submenu.
     * Pre-conditions: newname should not be null.
     * Post-conditions: A new Menu is created and added to the list.
     * Return Value: Menu (The newly created child Menu object).
     * Parameters:
     *     newname (Into) - The name of the new menu to create.
     */
    public Menu addSubMenu(String newname) {
        Menu newMenu = new Menu(newname);
        submenus.add(newMenu);
        return newMenu;
    }

    /*
     * Method Name: addSubMenu
     * Purpose: Creates a new Menu object with a callback and adds it as a submenu.
     * Pre-conditions: newname and cb should not be null.
     * Post-conditions: A new Menu is created with the callback and added to the list.
     * Return Value: Menu (The newly created child Menu object).
     * Parameters:
     *     newname (Into) - The name of the new menu to create.
     *     cb (Into) - The Runnable action for the new menu.
     */
    public Menu addSubMenu(String newname, Runnable cb) {
        Menu newMenu = new Menu(newname, cb);
        submenus.add(newMenu);
        return newMenu;
    }

    /*
     * Method Names: setCallback, getName
     * Purpose: Accessor and Mutator methods for Menu fields.
     * Pre-conditions: setCallback requires a valid Runnable; getName requires the object to be initialized.
     * Post-conditions: setCallback updates the callback field; getName leaves state unchanged.
     * Return Value: getName returns the String name; setCallback returns void.
     * Parameters:
     *     cb (Into) - The new Runnable to set as the callback.
     */
    public void setCallback(Runnable cb) {
        callback = cb;
    }

    public String getName() {
        return name;
    }

    /*
     * Method Name: listMenus
     * Purpose: Prints the list of available submenus to the console, formatted with indices.
     * Pre-conditions: submenus list should be initialized.
     * Post-conditions: Text is written to standard output; state remains unchanged.
     * Return Value: None.
     * Parameters: None.
     */
    public void listMenus() {
        System.out.println("\n 0. ↶ Back");
        for (int i = 0; i < submenus.size(); i++) {
            System.out.printf("%2d. %s%s%n", i + 1, submenus.get(i).getName(),
                    submenus.get(i).submenus.isEmpty() ? "" : " ▷");
        }
    }

    /*
     * Method Name: capture
     * Purpose: Handles the main interaction loop for this menu. Manages breadcrumbs,
     *          clears the screen, executes callbacks, lists submenus, and processes user input.
     * Pre-conditions: ProgramContext and Prog4 must be correctly initialized.
     * Post-conditions: Updates ProgramContext breadcrumbs and status messages.
     *                  May exit the program or modify database state via callbacks.
     * Return Value: None.
     * Parameters: None.
     */
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