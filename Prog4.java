import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

@SuppressWarnings("UseSpecificCatch")
public class Prog4 {
    private static final Scanner scanner = new Scanner(System.in);
    public static Scanner getScanner(){return scanner;}
    public static void main(String[] args) throws SQLException {
        DB.init(args);
        UI ui = new UI();

        // DO NOT PROGRAM THE CALLBACKS INLINE, USE THEM TO RUN OTHER CLASSES
        // STATIC FUNCTIONS ex: new Menu("Customer Dashboard", ()->DBClass.loginUser())
        // THE EXAMPLES BELOW ARE FPR TESTING OR JUST HEADERS

        /**
         * We are just storing everything in a global mutable static context with ProgramContext,
         * so submenus don't have to keep passing context through callback args
         * 
         * pctx also has Status messages to show after an action is completed. You can use this for pretty
         * error/success messages within your callbacks (again ProgramContext is all statically global) such as:
         * 
         * ProgramContext.setStatusMessage("Reservation removed!", ProgramContext.Color.GREEN);
         */

        ui.setInitialMode(
            new Menu("Home", ()->{System.out.println("Welcome to El Jefe Cat Cafe!");}).addSubMenu(new Menu[] {
                new Menu("Customer Registration", ()->{registerMember();}),
                new Menu("Our Pets", ()->{listPets();}), 
                new Menu("Customer Dashboard", ()->login(false)).addSubMenu(new Menu[] {
                    new Menu("View Visit History", ()->{/**TODO: QUERY 2 GOES HERE (Complex History)*/}),
                    new Menu("Food Orders").addSubMenu(new Menu[] {
                        new Menu("New Order", ()->{/**TODO: Req 3 (Insert)*/}),
                        new Menu("Update Order", ()->{/**TODO: Req 3 (Update)*/}),
                        new Menu("Cancel Order", ()->{/**TODO: Req 3 (Delete - conditional)*/})
                    }),
                    new Menu("Manage Reservations").addSubMenu(new Menu[] {
                        new Menu("Book a Reservation", ()->{/**TODO: Req 4 (Insert)*/}),
                        new Menu("Cancel a Reservation", ()->{/**TODO: Req 4 (Delete)*/}),
                        new Menu("Update a Reservation").addSubMenu(new Menu[] {
                            new Menu("Reschedule", ()->{/**TODO: Req 4 (Update)*/}),
                            new Menu("Extend Duration", ()->{/**TODO: Req 4 (Update)*/})
                        }),
                    }),
                    new Menu("Manage Events").addSubMenu(new Menu[] {
                        new Menu("Show Registered Events", ()->{/**TODO: */}),
                        new Menu("Show All Events", ()->{/**TODO: QUERY 3 GOES HERE*/}),
                        new Menu("Register for Event", ()->{/**TODO: Req 7 (Insert)*/}),
                        new Menu("Withdraw From Event", ()->{/**TODO: Req 7 (Update/Delete)*/}),
                    }),
                    new Menu("Manage Adoptions").addSubMenu(new Menu[] {
                        new Menu("My Adoptions", ()->{/**TODO: */}),
                        new Menu("New Adoption Application", ()->{/**TODO: Req 6 (Insert)*/}),
                        new Menu("Withdraw Application", ()->{/**TODO: Req 6 (Update status to Withdrawn)*/}),
                    }),
                    new Menu("Profile").addSubMenu(new Menu[] {
                        new Menu("View Personal Details", ()->{getMemInfo();}),
                        new Menu("Change Membership Tier", ()->{/**TODO: Req 1 (Update)*/}),
                        new Menu("Update Contact Info", ()->{modifyMember();}),
                        new Menu("Delete Account", ()->{/**TODO: Req 1 (Delete - w/ logic checks)*/}),
                    }),
                }),
                new Menu("Staff Dashboard", ()->login(true)).addSubMenu(new Menu[] {
                    new Menu("Front Desk Operations").addSubMenu(new Menu[] {
                         new Menu("Check Customer In", ()->{checkCustIn();}),
                         new Menu("Check Customer Out", ()->{checkCustOut();}),
                         new Menu("Business Analytics", ()->{/**TODO: QUERY 4 (Custom Query)*/})
                    }),
                    new Menu("Pets").addSubMenu(new Menu[] {
                        new Menu("Add New Pet", ()->{/**TODO: Req 2 (Insert)*/}),
                        new Menu("Update Pet Info", ()->{/**TODO: Req 2 (Update)*/}),
                        new Menu("Remove A Pet", ()->{/**TODO: Req 2 (Delete - conditional)*/}),
                        new Menu("Pet Info").addSubMenu(new Menu[] {
                            new Menu("View Adoption Applications", ()->{/**TODO: QUERY 1 GOES HERE*/}),
                            new Menu("Health Info", ()->{/**TODO: */}),
                        }),
                    }),
                    new Menu("Veterinary").addSubMenu(new Menu[] {
                        new Menu("My Entries", ()->{/**TODO: */}), 
                        new Menu("Add New Health Entry", ()->{/**TODO: Req 5 (Insert)*/}),
                        new Menu("Edit Existing Entry", ()->{/**TODO: Req 5 (Update)*/}),
                        new Menu("Void/Correct Entry", ()->{/**TODO: Req 5 (Logically Delete)*/}),
                    }),
                    new Menu("Adoptions").addSubMenu(new Menu[] {
                        new Menu("Review Applications", ()->{/**TODO: Req 6 (Update - Approve/Reject)*/}),
                        new Menu("Finalize Adoption", ()->{/**TODO: Update pet status/archive*/}),
                    }),
                    new Menu("Events Management").addSubMenu(new Menu[] {
                        new Menu("View All Events", ()->{/**TODO: */}), 
                        new Menu("Create New Event", ()->{/**TODO: */}),
                        new Menu("Cancel Event", ()->{/**TODO: */}),
                    }),
                })
            })
        );

        /* TODO:  One additional non–trivial query of your own design, with these restrictions:
            - The question must use more than two relations.
            - It must be constructed using at least one piece of information gathered from the user.
        */
        ui.run();
        DB.db.close();
    }

    public static void login(Boolean staff){
        // No password auth.
        if(ProgramContext.getUserId() != null &&
            (ProgramContext.getType() == ProgramContext.UserType.STAFF && staff) ||
            (ProgramContext.getType() == ProgramContext.UserType.MEMBER && !staff)) return;
        System.out.print("Please enter your member id: ");
        String entered = scanner.nextLine().trim();
        try {
            Integer id = Integer.valueOf(entered);
            var s = DB.prepared(staff ? "SELECT 1 FROM Staff WHERE empId = ?" : "SELECT 1 FROM Member WHERE memberNum = ?");
            s.setInt(1, id);
            var rs = s.executeQuery();
            if (!rs.next()) {
                ProgramContext.setStatusMessage("Member not found!", ProgramContext.Color.RED);
                throw new RuntimeException("User does not exist");
            }
            ProgramContext.setUserId(id);
            ProgramContext.setType(staff ? ProgramContext.UserType.STAFF : ProgramContext.UserType.MEMBER);
            ProgramContext.setStatusMessage("Successfully Logged In!", ProgramContext.Color.GREEN);
        } catch (RuntimeException | SQLException e) {
            ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
            throw new RuntimeException("Login Failed: " + e.getMessage()); // so we dont descend
        }
    }

    public static void getMemInfo() {
        try {
            var p = DB.prepared("""
                        SELECT memberNum as "ID", name as "Name", tele_num as "Telephone #", email as "e-Mail", dob as "Birthday", membershipTier as "Tier"
                         FROM Member
                         WHERE memberNum = ?
                    """);
            p.setInt(1, ProgramContext.getUserId());
            System.out.println(DB.tabularize(p.executeQuery()));
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    private static String prompt(String label, String current) {
        System.out.printf("Enter %s %s: ", label, current.isBlank() ? "" : "("+current+")");
        var input = scanner.nextLine().trim();
        return input.isBlank() ? current : input;
    }

    public static void registerMember(){
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            var rs = DB.execute("SELECT count(*) FROM Member");
            rs.next();
            int newId = rs.getInt(1) + 1;
            var stmt = DB.prepared("INSERT INTO Member VALUES ( %d, ?, ?, ?, ?, ? )".formatted(newId));
            stmt.setString(1, prompt("Name", ""));
            stmt.setString(2, prompt("Phone", ""));
            stmt.setString(3, prompt("Email", ""));
            System.out.print("Please enter your birthday (MM-dd-yyyy): ");
            stmt.setDate(4, new Date(format.parse(scanner.nextLine().trim()).getTime()));
            System.out.print("What membership tier would you like?\nOptions: Bronze, Silver, Gold (blank for none)\n");
            var inp = scanner.nextLine().trim().toUpperCase();
            if (!inp.isEmpty() && !inp.matches("(BRONZE)|(SILVER)|(GOLD)")) 
                throw new RuntimeException("Selected tier does not exist.");
            stmt.setString(5, inp.isEmpty() ? "NOT CURRENTLY MEMBER" : inp);
            stmt.executeUpdate();
            ProgramContext.setStatusMessage("Successfully registered user with ID: %d!".formatted(newId), ProgramContext.Color.GREEN);
        } catch (RuntimeException | ParseException | SQLException e) {
            ProgramContext.genericError(e);
        }
    }


    public static void modifyMember() {
        try {
            int id = ProgramContext.getUserId();
            var sel = DB.prepared("SELECT name, tele_num, email FROM Member WHERE memberNum = ?");
            sel.setInt(1, id);
            var rs = sel.executeQuery();

            if (!rs.next())
                throw new SQLException("Member data not found.");

            var upd = DB.prepared("UPDATE Member SET name=?, tele_num=?, email=? WHERE memberNum=?");
            System.out.println("Press Enter to keep existing info.");
            upd.setString(1, prompt("Name", rs.getString(1)));
            upd.setString(2, prompt("Phone", rs.getString(2)));
            upd.setString(3, prompt("Email", rs.getString(3)));
            upd.setInt(4, id);

            upd.executeUpdate();
            ProgramContext.setStatusMessage("Contact info updated!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void checkCustIn(){
        try {
            var memNum = Integer.parseInt(prompt("Member #", ""));
            var sel = DB.prepared(
                "SELECT reservationId as \"ID\", reservationDate as \"Date\", timeSlot as \"Time\" " +
                "FROM Reservation WHERE memberNum=? AND checkedIn='NO'"
            );
            sel.setInt(1, memNum);
            System.out.println(DB.tabularize(sel.executeQuery()));
            var upd = DB.prepared("UPDATE Reservation SET checkedIn='YES' WHERE reservationId=? AND memberNum=?");
            upd.setInt(1, Integer.parseInt(prompt("Reservation ID", "")));
            upd.setInt(2, memNum);
            upd.executeUpdate();
            ProgramContext.setStatusMessage("Checked member in!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void checkCustOut() {
        try {
        var upd = DB.prepared("UPDATE Reservation SET checkedOut='YES' WHERE memberNum=?"); // only one res at a time
            upd.setInt(1, Integer.parseInt(prompt("Member #", "")));
            upd.executeUpdate();
            ProgramContext.setStatusMessage("Checked member out!", ProgramContext.Color.GREEN);
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }
    
    public static void listPets() {
        try {
            var s = DB.prepared(
                    """
                            SELECT Pet.petId as "ID", animalType as "Type", breed as "Breed", age as "Age", doa as "Birthday", adoptable as "Adoptable"
                            FROM Pet JOIN AdoptionApp USING (petId) WHERE status NOT IN ('PEN', 'APP');
                            """);
            System.out.println(DB.tabularize(s.executeQuery()));
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }
}