import java.sql.*;
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
            new Menu("Home", ()->{
            System.out.println("Welcome to El Jefe Cat Cafe!");
            try{var rs = DB.prepared("SELECT TO_CHAR(CURRENT_DATE, 'DY, MON DD, YYYY')"+
                " || ' @ ' || TO_CHAR(CURRENT_TIMESTAMP, 'HH:MI AM') as d FROM DUAL").executeQuery();
                rs.next();
                System.out.println(rs.getString(1));
            }catch(Exception e){}
            System.out.println("\nType \"exit\" any time to cancel an action or exit the program.");
        }).addSubMenu(new Menu[] {
                new Menu("Customer Registration", ()->registerMember()),
                new Menu("Our Pets", ()->listPets()), 
                new Menu("Customer Dashboard", ()->login(false)).addSubMenu(new Menu[] {
                    new Menu("View Visit History", ()->viewVisitHistory()),
                    new Menu("Food Orders").addSubMenu(new Menu[] {
                        new Menu("View Orders", ()-> listMyOrders() ),
                        new Menu("New Order", ()->placeOrder()),
                        new Menu("Update Order", ()->updateOrder()),
                        new Menu("Cancel Order", ()->cancelOrder())
                    }),
                    new Menu("Manage Reservations").addSubMenu(new Menu[] {
                        new Menu("View my Reservations", ()->listReservations()),
                        new Menu("Book a Reservation", ()->bookReservation()),
                        new Menu("Cancel a Reservation", ()->cancelReservation()),
                        new Menu("Update a Reservation").addSubMenu(new Menu[] {
                            new Menu("Reschedule", ()->rescheduleReservation()),
                            new Menu("Extend Duration", ()->extendReservation())
                        }),
                    }),
                    new Menu("Manage Events").addSubMenu(new Menu[] {
                        new Menu("Show Registered Events", ()->listMyEvents()),
                        new Menu("Show All Events", ()->listAllEvents()),
                        new Menu("Register for Event", ()->registerForEvent()),
                        new Menu("Withdraw From Event", ()->withdrawFromEvent()),
                    }),
                    new Menu("Manage Adoptions").addSubMenu(new Menu[] {
                        new Menu("My Adoptions", ()->listMyAdoptions(false)),
                        new Menu("New Adoption Application", ()->newAdoptionApp()),
                        new Menu("Withdraw Application", ()->withdrawAdoptApp()),
                    }),
                    new Menu("Profile").addSubMenu(new Menu[] {
                        new Menu("View Personal Details", ()->getMemInfo()),
                        new Menu("Change Membership Tier", ()->changeMembershipTier()),
                        new Menu("Update Contact Info", ()->modifyMember()),
                        new Menu("Delete Account", ()->deleteMember()),
                    }),
                }),
                new Menu("Staff Dashboard", ()->login(true)).addSubMenu(new Menu[] {
                    new Menu("Front Desk Operations").addSubMenu(new Menu[] {
                        new Menu("Check Customer In", ()->checkCustIn()),
                        new Menu("Check Customer Out", ()->checkCustOut()),
                        new Menu("Business Analytics", ()->{/**TODO: QUERY 4 (Custom Query)*/})
                    }),
                    new Menu("Pets").addSubMenu(new Menu[] {
                        new Menu("Add New Pet", ()->addPet()),
                        new Menu("Update Pet Info", ()->updatePet()),
                        new Menu("Remove A Pet", ()->deletePet()),
                        new Menu("Pet Info").addSubMenu(new Menu[] {
                            new Menu("View Adoption Applications", ()->viewAdoptionAppsForPet()),
                            new Menu("Health Info", ()->viewHealthInfoForPet()),
                        }),
                    }),
                    new Menu("Veterinary").addSubMenu(new Menu[] {
                        new Menu("My Entries", ()->listMyEntries()), 
                        new Menu("Add New Health Entry", ()->newHealthEntry()),
                        new Menu("Edit Existing Entry", ()->updateEntry(false)),
                        new Menu("Void Entry", ()->updateEntry(true)),
                    }),
                    new Menu("Adoptions").addSubMenu(new Menu[] {
                        new Menu("Review Applications", ()->showAdoptApps()).addSubMenu(new Menu[] {
                            new Menu("Approve", ()->approveAdoptApp()),
                            new Menu("Reject", ()->rejectAdoptApp()),
                        }),
                        new Menu("Finalize Adoption", ()->finalizeAdoptApp()),
                    }),
                    new Menu("Events Management").addSubMenu(new Menu[] {
                        new Menu("View All Events", ()->listAllEvents()), 
                        new Menu("Create New Event", ()->newEvent()),
                        new Menu("Cancel Event", ()->cancelEvent()),
                        new Menu("Delete Event", ()->deleteEvent()),
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
    
    public static void login(Boolean staff) {
        // No password auth.
        if (ProgramContext.getUserId() != null &&
                (ProgramContext.getType() == ProgramContext.UserType.STAFF && staff) ||
                (ProgramContext.getType() == ProgramContext.UserType.MEMBER && !staff))
            return;
        var id = Prompt.integer("Please enter your member id: ", null);
        try {
            var s = DB.prepared(
                    staff ? "SELECT 1 FROM Staff WHERE empId = ?" : "SELECT 1 FROM Member WHERE memberNum = ?");
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

    public static void viewVisitHistory() {
        try {
            System.out.println("--- Visit History ---");
            // https://modern-sql.com/feature/listagg#:~:text=Syntax,this%20requirement%20is%20not%20fulfilled.
            DB.printQuery("""
                        SELECT
                        TO_CHAR(r.reservationDate, 'DY, MON DD, YYYY') as "Date",
                        r.roomId as "Room",

                        COALESCE(
                            (SELECT LISTAGG(i.itemName || ' (x' || oi.quantity || ')', ', ')
                                    WITHIN GROUP (ORDER BY i.itemName)
                             FROM FoodOrder fo
                             JOIN OrderItem oi ON fo.orderId = oi.orderId
                             JOIN Item i ON oi.itemId = i.itemId
                             WHERE fo.reservationId = r.reservationId),
                        'No Food Ordered') as "Food Orders",

                        TO_CHAR(COALESCE(
                            (SELECT SUM(fo.totalPrice)
                             FROM FoodOrder fo
                             WHERE fo.reservationId = r.reservationId),
                        0), '$990.00') as "Total Spent",

                        COALESCE(
                            (SELECT mh.membershipTier
                             FROM MemberHistory mh
                             WHERE mh.memberNum = r.memberNum
                               AND r.reservationDate >= mh.startDate
                               AND (mh.endDate IS NULL OR r.reservationDate <= mh.endDate)
                             FETCH FIRST 1 ROWS ONLY),
                            'UNKNOWN') as "Tier Status"

                    FROM Reservation r
                    WHERE r.memberNum = ?
                    ORDER BY r.reservationDate DESC
                    """, ProgramContext.getUserId());
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    public static void getMemInfo() {
        try {
            DB.printQuery("""
                    SELECT memberNum as "ID", name as "Name", tele_num as "Telephone #",
                           email as "e-Mail", TO_CHAR(dob, 'MON DD') as "Birthday", membershipTier as "Tier"
                    FROM Member WHERE memberNum = ?
                    """, ProgramContext.getUserId());
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    public static void changeMembershipTier() {
        try {
            var id = ProgramContext.getUserId();

            var inp = Prompt.choice("What membership tier you would like?", new String[] { "Bronze", "Silver", "Gold", null });
            if (inp == null) {
                inp = "NOT CURRENTLY MEMBER";
            }
            inp = inp.toUpperCase();
            DB.executeUpdate("UPDATE Member SET membershipTier=? WHERE memberNum=?", inp, id);
            if (DB.exists("SELECT startDate FROM MemberHistory WHERE memberNum=? AND endDate = NULL", id)) {
                DB.executeUpdate("UPDATE MemberHistory SET endDate=CURRENT_DATE WHERE memberNum=? AND startDate=?", id,
                        DB.executeQuery("SELECT startDate FROM MemberHistory WHERE memberNum=? AND endDate = NULL", id)
                                .getInt(1));
            }
            DB.executeUpdate(" INSERT INTO MemberHistory VALUES (?,  CURRENT_DATE, NULL, ?) ", id, inp);
            ProgramContext.setStatusMessage("Successfully changed membership tier to: %s!".formatted(inp),
                    ProgramContext.Color.GREEN);
        } catch (RuntimeException | SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    public static void deleteMember() {
        try {
            int id = ProgramContext.getUserId();
            if (DB.exists("""
                    SELECT 1 FROM Member m
                    WHERE m.memberNum = ?
                    AND (
                        EXISTS (
                            SELECT 1 FROM Reservation r 
                            WHERE r.memberNum = m.memberNum 
                            AND r.checkedIn = 'YES' AND r.checkedOut = 'NO'
                        )
                        OR EXISTS (
                            SELECT 1 FROM AdoptionApp a 
                            WHERE a.memberNum = m.memberNum 
                            AND a.status = 'PEN'
                        )
                        OR EXISTS (
                            SELECT 1 FROM FoodOrder f 
                            WHERE f.memberNum = m.memberNum 
                            AND f.paymentStatus = FALSE
                        )
                    )
                    """, id)) {
                throw new RuntimeException(
                        "You have either an active adoption application, reservation, or unpaid food order.");
            }
            System.out.println("Are you sure you want to delete your account?\nALL ASSOCIATED RECORDS WILL BE DELETED\nType \"yes\" to proceed.");
            switch (scanner.nextLine().trim().toLowerCase()) {
                case "yes" -> {
                }
                default -> throw new RuntimeException("Cancelled account deletion");
            }
            DB.executeUpdate("""
                    DELETE FROM Member WHERE memberNum=?
                    """, id); // cascade handles ref records
            System.exit(0);
        } catch (RuntimeException | SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    public static void registerMember() {
        try {
            var rs = DB.execute("SELECT count(*) FROM Member");
            rs.next();
            var stmt = DB.prepared("INSERT INTO Member VALUES ( member_seq.NEXTVAL, ?, ?, ?, ?, ? )", true);
            stmt.setString(1, Prompt.string("Name", ""));
            stmt.setString(2, Prompt.string("Phone", ""));
            stmt.setString(3, Prompt.string("Email", ""));
            stmt.setDate(4, Prompt.date("Your Birthday", "MM-dd-yyyy", null));
            var inp = Prompt.choice("What membership tier you would like?", new String[] { "Bronze", "Silver", "Gold", null });
            if (inp == null) {
                inp = "NOT CURRENTLY MEMBER";
            }
            inp = inp.toUpperCase();
            // if (inp != null && !inp.matches("(BRONZE)|(SILVER)|(GOLD)"))
            //     throw new RuntimeException("Selected tier does not exist.");
            stmt.setString(5, inp);
            stmt.executeUpdate();
            var keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                DB.executeUpdate(" INSERT INTO MemberHistory VALUES (?,  CURRENT_DATE, NULL, ?) ", id, inp);
                ProgramContext.successMessage("Successfully registered user with ID: %d!".formatted(id));
            } else {
                ProgramContext.failureMessage("Failed to retrieve Member ID!");
            }

        } catch (Exception e) {
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
            upd.setString(1, Prompt.string("Name", rs.getString(1)));
            upd.setString(2, Prompt.string("Phone", rs.getString(2)));
            upd.setString(3, Prompt.string("Email", rs.getString(3)));
            upd.setInt(4, id);

            upd.executeUpdate();
            ProgramContext.successMessage("Contact info updated!");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void checkCustIn() {
        try {
            DB.printQuery("""
                SELECT m.memberNum, m.name
                FROM Member m
                JOIN Reservation r ON m.memberNum = r.memberNum
                WHERE r.checkedIn = 'NO'
                  AND r.reservationDate <= CURRENT_DATE
                  AND r.reservationDate = (
                      SELECT MAX(r2.reservationDate)
                      FROM Reservation r2
                      WHERE r2.memberNum = m.memberNum
                        AND r2.checkedIn = 'NO'
                        AND r2.reservationDate <= CURRENT_DATE
                )
                """);
            int memNum = Prompt.integer("Member #", null);
            DB.printQuery("SELECT reservationId as \"ID\", reservationDate as \"Date\", timeSlot as \"Time\" " +
                    "FROM Reservation WHERE memberNum=? AND checkedIn='NO'", memNum);
            DB.executeUpdate("UPDATE Reservation SET checkedIn='YES' WHERE reservationId=? AND memberNum=?",
                    Prompt.integer("Reservation ID", null), memNum);
            ProgramContext.successMessage("Checked member in!");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void checkCustOut() {
        try {
            DB.printQuery("""
                SELECT m.memberNum, m.name
                FROM Member m
                JOIN Reservation r ON m.memberNum = r.memberNum
                WHERE r.checkedIn = 'YES'
                  AND r.reservationDate <= CURRENT_DATE
                  AND r.checkedOut = 'NO'
                  AND r.reservationDate = (
                      SELECT MAX(r2.reservationDate)
                      FROM Reservation r2
                      WHERE r2.memberNum = m.memberNum
                        AND r2.checkedIn = 'YES'
                        AND r2.checkedOut = 'NO'
                        AND r2.reservationDate <= CURRENT_DATE
                )
                """);
            var memNum = Prompt.integer("Member #", null);
            DB.executeUpdate("UPDATE Reservation SET checkedOut='YES' WHERE memberNum=?", memNum);
            ProgramContext.setStatusMessage("Checked member out!", ProgramContext.Color.GREEN);
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    /* validates that an ID exists in the specified table. returns the ID for convenience. */
    public static Integer validateID(Integer id, String relation, String idName) throws SQLException {
        var rs = DB.executeQuery("SELECT 1 FROM %s WHERE %s = ?".formatted(relation, idName), id);
        if (!rs.next())
            throw new SQLException("ID not found.");
        return id;
    }

    public static void printPetInfo(Integer id) throws SQLException {
        DB.printQuery("""
                SELECT *
                FROM Pet
                WHERE petId = ?
                """, id);
    }

    public static void addPet() {
        try {
            var stmt = DB.prepared("INSERT INTO Pet VALUES (%d, ?, ?, ?, ?, ?, ? )".formatted(DB.uniqueId("Pet", "petId")), true);
            stmt.setString(1, Prompt.string("Animal Type", ""));
            stmt.setString(2, Prompt.stringNullable("Breed", null));
            stmt.setInt(3, Prompt.integer("Age", null));
            stmt.setDate(4, Prompt.date("Pet's Date Of Arrival", "MM-dd-yyyy", null));
            stmt.setBoolean(5, Prompt.bool("adoptable?", null));
            stmt.setString(6, Prompt.stringNullable("Name", null));
            stmt.executeUpdate();
            var keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                System.out.println("Added the following pet entry to databse:");
                printPetInfo(id);
                ProgramContext.successMessage("Added Pet with ID: %d!".formatted(id));
            } else {
                ProgramContext.failureMessage("Failed to retrieve Pet ID!");
            }
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    public static void updatePet() {
        try {
            listPets();
            int id = validateID(
                    Prompt.integer("ID of pet you wish to edit", null),
                    "Pet",
                    "petId");

            System.out.println("Current Pet Info:");
            printPetInfo(id);
            
            var rs = DB.executeQuery("""
                    SELECT animalType, breed, age, doa, adoptable, name
                    FROM Pet
                    WHERE petId = ?""",
                    id);
            rs.next();
            DB.executeUpdate("UPDATE Pet SET animalType=?, breed=?, age=?, doa=?, adoptable=?, name=? WHERE petId=?", 
                    Prompt.string("animalType", rs.getString(1)), 
                    Prompt.string("Breed", rs.getString(2)),
                    Prompt.integer("Age", rs.getInt(3)),
                    Prompt.date("DOA", "MM-dd-YYYY", rs.getDate(4)),
                    Prompt.bool("Adoptable?", rs.getBoolean(5)),
                    Prompt.string("Name", rs.getString(6)),
                    id
                );
            ProgramContext.successMessage("Pet information updated!");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }
    
    public static void deletePet() {
        try {
            listPets();
            int id = validateID(
                    Prompt.integer("ID of Pet you wish to delete", null),
                    "Pet",
                    "petId");
            System.out.println("Pet Info:");
            printPetInfo(id);
            Boolean confirmed = Prompt.bool("Are you sure you want to delete this pet with ID %d?\nALL ASSOCIATED RECORDS WILL BE DELETED\n".formatted(id), false);
            if (confirmed) {
                DB.executeUpdate("DELETE FROM Pet WHERE petId = ?", id);
                ProgramContext.successMessage("Deleted Pet with ID %d".formatted(id));
            }
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void viewAdoptionAppsForPet() {
        try {
            listPets();
            int id = validateID(
                    Prompt.integer("ID of Pet you want to view applications for", null),
                    "Pet",
                    "petId");
            System.out.println("Pet Info:");
            printPetInfo(id);
            try {
                DB.printQuery("""
                        SELECT  a.appId as "ID",
                                m.name as "Name",
                                a.appDate as "Application Date",
                                a.status as "Status",
                                s.name as "Adoption Coordinator"
                        FROM AdoptionApp a
                        JOIN Member m ON (a.memberNum = m.memberNum)
                        JOIN STAFF s  ON (a.empId = s.empId)
                        WHERE a.petId = ?
                        """,
                        id);
            } catch (SQLException e) {
                ProgramContext.warningMessage("No Adoption Application data available for pet.");
            }
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void viewHealthInfoForPet() {
        try {
            listPets();
            var id = validateID(
                    Prompt.integer("ID of Pet you want to view health records of", null),
                    "Pet",
                    "petId");
            System.out.println("Pet Info:");
            printPetInfo(id);
            try {
                DB.printQuery("""
                        SELECT  r.recId       as "ID",
                                r.revNum      as "Revision Number",
                                r.revAction   as "Revision Action",
                                r.revDate     as "Revision Date",
                                s.name        as "Staff Member",
                                r.recType     as "Record Type",
                                r.description as "Description",
                                r.nextDue     as "Next Due",
                                r.status      as "Status"
                        FROM HealthRecord r
                        JOIN STAFF s ON (r.empId = s.empId)
                        WHERE r.petId = ?
                        """,
                        id);
            } catch (Exception e) {
                ProgramContext.warningMessage("No Health data available for pet.");
            }
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void listPets() {
        try {
            DB.printQuery("""
                    SELECT p.petId as "ID", animalType as "Type", p.name as "Name", p.breed as "Breed",
                           p.age as "Age", TO_CHAR(p.doa, 'MON DD') as "DOA", p.adoptable as "Adoptable"
                    FROM Pet p LEFT OUTER JOIN AdoptionApp a on (p.petId = a.petId AND a.status NOT IN ('PEN', 'APP'))
                    ORDER BY p.animalType ASC, p.name ASC
                    """);
        } catch (SQLException e) {
            ProgramContext.genericError(e);
        }
    }

    public static void listReservations() {
        try {
            DB.printQuery("""
                        SELECT
                        reservationId as "ID",
                        TO_CHAR(reservationDate, 'DY, MON DD, YYYY HH:MI AM') as "Date/Time",
                        CASE
                            WHEN EXTRACT(HOUR FROM timeSlot) > 0 THEN
                                EXTRACT(HOUR FROM timeSlot) || ' hrs ' || EXTRACT(MINUTE FROM timeSlot) || ' mins'
                            ELSE
                                EXTRACT(MINUTE FROM timeSlot) || ' mins'
                        END as "Booking Length",
                        roomId as "Room"
                    FROM Reservation
                    WHERE memberNum = ?""",
                    ProgramContext.getUserId());
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void bookReservation() {
        try {
            String resDate = Prompt.timestamp("Reservation Date", "MM-dd-yyyy HH:mm", null).toString();
            String timeSlot = Prompt.time("Time Slot Duration", "HH:mm").toString();
            var rs = DB.execute("SELECT count(*) FROM Reservation");
            rs.next();
            int resId = DB.uniqueId("Reservation", "reservationId");
            DB.executeUpdate("INSERT INTO Reservation VALUES (?, ?, ?, ?, CAST(? AS INTERVAL HOUR TO MINUTE), ?, ?)",
                    resId,
                    ProgramContext.getUserId(),
                    Prompt.integer("Room ID", null),
                    resDate,
                    timeSlot,
                    "NO",
                    "NO");

            ProgramContext.successMessage("Reservation booked successfully! ID: " + resId);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void cancelReservation() {
        try {
            listReservations();
            DB.executeUpdate("DELETE FROM Reservation WHERE reservationId = ? AND memberNum = ?",
                    Prompt.integer("Reservation ID to cancel", null), ProgramContext.getUserId());
            ProgramContext.setStatusMessage("Reservation cancelled successfully!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void rescheduleReservation() {
        try {
            listReservations();
            var resId = Prompt.integer("Reservation ID to reschedule: ", null);
            var newDate = Prompt.timestamp("New Reservation Date", "MM-dd-yyyy HH:mm:ss", null);
            DB.executeUpdate("UPDATE Reservation SET reservationDate = ? WHERE reservationId = ? AND memberNum = ?",
                    newDate, resId, ProgramContext.getUserId());
            ProgramContext.successMessage("Reservation rescheduled successfully!");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void extendReservation() {
        try {
            listReservations();
            var resId = Prompt.integer("Reservation ID to extend: ", null);
            var newDuration = Prompt.time("New Duration", "HH:mm");
            DB.executeUpdate(
                    "UPDATE Reservation SET timeSlot = CAST(? AS INTERVAL HOUR TO MINUTE) WHERE reservationId = ? AND memberNum = ?",
                    newDuration.toString(), resId, ProgramContext.getUserId());
            ProgramContext.setStatusMessage("Reservation extended successfully!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    // Events Member
    public static void listAllEvents() {
        try {
            DB.printQuery("""
                        SELECT 
                            e.eventId as "ID",
                            e.description as "Name",
                            TO_CHAR(e.eventDate, 'DY, MON DD, YYYY HH:MI AM') as "Date/Time",
                            e.roomId as "Room #",
                            COUNT(CASE WHEN b.status = 'CAN' THEN b.bookingId END) as "Attendees",
                            e.maxCapacity as "Max Cap.",
                            (e.maxCapacity - COUNT(CASE WHEN b.status = 'CAN' THEN b.bookingId END)) as "Spots Left",
                            s.name as "Coordinator"
                        FROM Event e
                        LEFT JOIN Booking b ON e.eventId = b.eventId
                        LEFT JOIN Staff s ON s.empId = e.coordinator
                        WHERE e.canceled = FALSE
                        GROUP BY e.eventId, e.eventDate, e.description, e.roomId, e.maxCapacity
                        HAVING (e.maxCapacity - COUNT(CASE WHEN b.status = 'CAN' THEN b.bookingId END)) > 0
                    """);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void listMyEvents() {
        try {
            DB.printQuery("""
                        SELECT e.eventId as "ID",
                            TO_CHAR(e.eventDate, 'DY, MON DD, YYYY HH:MI AM') as "Date/Time",
                            e.description,
                            e.roomId
                        FROM (Event e
                        JOIN Booking b USING (eventId))
                        WHERE b.member = ? AND b.status NOT IN ('CAN')
                        GROUP BY e.eventId, e.eventDate, e.description, e.roomId, e.maxCapacity
                    """, ProgramContext.getUserId());
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void registerForEvent() {
        try {
            listAllEvents();
            var evtId = Prompt.integer("Event ID", null);
            if (evtId == null)
                return;
            int bookId = DB.uniqueId("Booking", "bookingId");

            DB.executeUpdate("INSERT INTO Booking VALUES (?, ?, ?, 'REG', 0, 0)",
                    bookId, evtId, ProgramContext.getUserId());
            ProgramContext.setStatusMessage("Registered for event!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void withdrawFromEvent() {
        try {
            listMyEvents();
            var bookId = Prompt.integer("Booking ID to withdraw", null);
            if (bookId == null)
                return;
            DB.executeUpdate("UPDATE Booking SET status = 'CAN' WHERE bookingId = ? AND member = ?",
                    bookId, ProgramContext.getUserId());
            ProgramContext.setStatusMessage("Withdrawn from event.", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    private static void listFoodItems() throws SQLException {
        DB.printQuery("SELECT itemId as \"ID\", itemName as \"Item\", '$' || price as \"Price\" FROM Item");
    }

    private static void listMyOrders() {
        try {
            DB.printQuery("""
                        SELECT orderId as "Order ID",
                               TO_CHAR(orderTime, 'MM-DD-YYYY HH:MI AM') as "Time",
                               '$' || totalPrice as "Total",
                               CASE WHEN paymentStatus THEN 'PAID' ELSE 'UNPAID' END as "Status"
                        FROM FoodOrder
                        WHERE memberNum = ?
                        ORDER BY orderTime DESC
                    """, ProgramContext.getUserId());
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    private static void recalculateOrderTotal(int orderId) throws SQLException {
        var rs = DB.prepared("""
                    SELECT COALESCE(SUM(i.price * oi.quantity), 0)
                    FROM OrderItem oi
                    JOIN Item i ON oi.itemId = i.itemId
                    WHERE oi.orderId = ?
                """);
        rs.setInt(1, orderId);
        var res = rs.executeQuery();
        res.next();
        double newTotal = res.getDouble(1);

        DB.executeUpdate("UPDATE FoodOrder SET totalPrice = ? WHERE orderId = ?", newTotal, orderId);
    }

    public static void placeOrder() {
        try {
            listReservations();
            Integer resId = Prompt.integer("Reservation ID for this order", null);
            if (resId == null)
                return;

            int newOrderId = DB.uniqueId("FoodOrder", "orderId");

            DB.executeUpdate("INSERT INTO FoodOrder VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0.00, ?)",
                    newOrderId, ProgramContext.getUserId(), resId, false);

            System.out.println("Starting Order #" + newOrderId + ". Enter items below.");
            listFoodItems();

            while (true) {
                int itemId = Prompt.integer("Item ID to add (or 0 to finish)", null);
                if (itemId == 0)
                    break;

                int qty = Prompt.integer("Quantity", 1);

                try {
                    DB.executeUpdate("INSERT INTO OrderItem VALUES (?, ?, ?)", newOrderId, itemId, qty);
                    System.out.println("Item added.");
                } catch (SQLException e) {
                    System.out.print("Error adding item (might already exist in order): " + e.getMessage());
                }
            }

            recalculateOrderTotal(newOrderId);
            ProgramContext.setStatusMessage("Order placed successfully! Order ID: " + newOrderId,
                    ProgramContext.Color.GREEN);

        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void updateOrder() {
        try {
            listMyOrders();
            Integer orderId = Prompt.integer("Order ID to update", null);
            if (orderId == null)
                return;

            System.out.println("--- Current Items in Order #" + orderId + " ---");
            DB.printQuery("""
                        SELECT i.itemName as "Item", oi.quantity as "Qty",
                               '$' || (i.price * oi.quantity) as "Subtotal", i.itemId as "ID"
                        FROM OrderItem oi
                        JOIN Item i USING (itemId)
                        WHERE oi.orderId = ?
                    """, orderId);

            listFoodItems();
            int itemId = Prompt.integer("Enter Item ID to add/update (or 0 to cancel):", null);
            if (itemId == 0)
                return;

            int newQty = Prompt.integer("New Quantity (0 to remove)", 1);

            if (newQty > 0) {
                var check = DB.prepared("SELECT count(*) FROM OrderItem WHERE orderId=? AND itemId=?");
                check.setInt(1, orderId);
                check.setInt(2, itemId);
                var rs = check.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    DB.executeUpdate("UPDATE OrderItem SET quantity = ? WHERE orderId = ? AND itemId = ?",
                            newQty, orderId, itemId);
                } else {
                    DB.executeUpdate("INSERT INTO OrderItem VALUES (?, ?, ?)", orderId, itemId, newQty);
                }
                System.out.println("Item updated.");
            } else {
                DB.executeUpdate("DELETE FROM OrderItem WHERE orderId = ? AND itemId = ?", orderId, itemId);
                System.out.println("Item removed.");
            }

            recalculateOrderTotal(orderId);
            ProgramContext.setStatusMessage("Order updated successfully!", ProgramContext.Color.GREEN);

        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void cancelOrder() {
        try {
            listMyOrders();
            Integer orderId = Prompt.integer("Order ID to cancel", null);
            if (orderId == null)
                return;

            DB.executeUpdate("DELETE FROM OrderItem WHERE orderId = ?", orderId); // cascade
            DB.executeUpdate("DELETE FROM FoodOrder WHERE orderId = ? AND memberNum = ?", orderId,
                    ProgramContext.getUserId());

            ProgramContext.setStatusMessage("Order cancelled.", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void listMyAdoptions(Boolean justApps){
        try {
            System.out.println("-- All Applications --");
            try{
            DB.printQuery("""
                        SELECT a.appId as "ID",
                            p.name as "Name",
                            p.age as "Age",
                            p.breed as  "Breed",
                            p.animalType as "Species",
                            TO_CHAR(a.appDate, 'DY, MON DD, YYYY HH:MI AM') as "Date/Time",
                            COALESCE(s.name, 'Pending Review') as "Coordinator",
                            a.status as "Status"
                        FROM AdoptionApp a
                        JOIN Pet p ON a.petId = p.petId
                        LEFT JOIN Staff s ON a.empId = s.empId
                        WHERE a.memberNum = ? AND a.status NOT IN ('APP'%s)
                        ORDER BY a.appDate DESC
                    """.formatted(justApps ? ",'WIT'":""), ProgramContext.getUserId());
            } catch (Exception e){
                System.out.println("none.");
            }
            if(justApps) return;
            System.out.println("-- All Adoptions --");
            try{
            DB.printQuery("""
                        SELECT p.adoptId as "ID",
                            p.name as "Name",
                            TO_CHAR(p.adoptDate, 'DY, MON DD, YYYY HH:MI AM') as "Adopt Date",
                            p.fee as "Fee ($)",
                            p.followUpSchedule as "Follow Up Schedule"
                        FROM (AdoptionApp a JOIN Pet p USING (petId)) JOIN Adoption p USING (appId)
                        WHERE a.memberNum = ?
                        ORDER BY p.adoptDate DESC
                    """, ProgramContext.getUserId());
            } catch (Exception e){
                System.out.println("none.");
            }
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void withdrawAdoptApp(){
        try{
        listMyAdoptions(true);
        DB.executeUpdate("""
                UPDATE AdoptionAPP a SET status='WIT' WHERE a.appId=? AND a.memberNum=?
                """, Prompt.integer("Application ID", null), ProgramContext.getUserId());
            ProgramContext.setStatusMessage("Adoption withdrwan!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void newAdoptionApp(){
        try{
            DB.printQuery("""
                    SELECT p.petId as "ID", animalType as "Type", p.name as "Name", p.breed as "Breed",
                           p.age as "Age", TO_CHAR(p.doa, 'MON DD') as "Birthday"
                    FROM Pet p LEFT OUTER JOIN AdoptionApp a on (p.petId = a.petId AND a.status NOT IN ('PEN', 'APP'))
                    WHERE p.adoptable
                    ORDER BY p.animalType ASC, p.name ASC
            """);
            var pid = Prompt.integer("Pet ID you wish to adopt", null);
            var stmt = DB.prepared("""
                    SELECT * FROM AdoptionApp 
                    WHERE petId=? AND memberNum=? AND status IN ('PEN','APP')
                    """);
            stmt.setObject(1, pid);
            stmt.setObject(2, ProgramContext.getUserId());
            var rs = stmt.executeQuery();
            if(rs.isBeforeFirst()){
                ProgramContext.setStatusMessage("You already have a pending application for that pet, or it does not exist!", ProgramContext.Color.RED);
                return;
            }
            int appId = DB.uniqueId("AdoptionApp", "appId");
            DB.executeUpdate("""
                    INSERT INTO AdoptionApp VALUES (%d, ?, NULL, ?, CURRENT_DATE, 'PEN')
                    """.formatted(appId), ProgramContext.getUserId(), pid);
            ProgramContext.setStatusMessage("Created a new application!", ProgramContext.Color.GREEN);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void newEvent() {
        try {
            System.out.println("-- Rooms -- ");
            DB.printQuery("SELECT roomId as \"ID\", maxCapacity as\"Max Cap.\" FROM Room");
            DB.executeUpdate("INSERT INTO Event VALUES(%d,?,?,CAST(? AS INTERVAL HOUR TO MINUTE),?,?,?, FALSE)"
                .formatted(DB.uniqueId("Event", "eventId")),
                    ProgramContext.getUserId(),
                    Prompt.date("Event Date", "MM-dd-yyyy H:m", null),
                    Prompt.time("Event Duration", "H:m").toString(),
                    Prompt.integer("Room ID (from above)", null),
                    Prompt.string("Event Description", null),
                    Prompt.integer("Max Capacity (Cannot exceed chosen Room's Max capacity)", null)
                );
            ProgramContext.successMessage("Created new event!");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void cancelEvent(){
        try {
            listAllEvents();
            var eid = Prompt.integer("Event Id", null);
            DB.executeUpdate("UPDATE Event SET canceled=TRUE WHERE eventId=?", eid);
            DB.executeUpdate("UPDATE Booking SET status='CAN' WHERE eventId=?", eid);
            ProgramContext.successMessage("Successfully canceled event.");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void deleteEvent(){
        try {
            DB.printQuery("""
                    SELECT eventId as "ID", description as "Name" FROM
                    Event WHERE canceled=TRUE AND eventDate > (CURRENT_DATE + 14)
                    """); // "well in advance is 14 days i think"
            DB.executeUpdate("DELETE FROM Event WHERE eventId=? AND canceled", Prompt.integer("Event ID", null));
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void showAdoptApps(){
        try {
            DB.printQuery("""
                SELECT a.appId as "ID", m.name as "Member Name", COALESCE(e.name, 'None') as "Coordinator",
                p.name as "Pet Name", TO_CHAR(a.appDate, 'MM-dd-yyyy') as "App Date", a.status as "Status" 
                FROM AdoptionApp a LEFT OUTER JOIN Member m USING (memberNum)
                LEFT OUTER JOIN Staff e USING (empId)
                LEFT OUTER JOIN Pet p USING (petId)
                WHERE a.status NOT IN ('REJ', 'WIT', 'APP')
                """);
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }
    public static void approveAdoptApp(){
        try {
            showAdoptApps();
            DB.executeUpdate("UPDATE AdoptionApp SET status='APP' WHERE appId=?",Prompt.integer("Application ID", null));
            ProgramContext.successMessage("Approved Application");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }
    public static void rejectAdoptApp(){
        try {
            showAdoptApps();
            DB.executeUpdate("UPDATE AdoptionApp SET status='REJ' WHERE appId=?",Prompt.integer("Application ID", null));
            ProgramContext.successMessage("Rejected Application");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }
    public static void finalizeAdoptApp(){
        try {
            DB.printQuery("""
                SELECT a.appId as "ID", m.name as "Member Name", COALESCE(e.name, 'None') as "Coordinator",
                p.name as "Pet Name", TO_CHAR(a.appDate, 'MM-dd-yyyy') as "App Date"
                FROM AdoptionApp a LEFT OUTER JOIN Member m USING (memberNum)
                LEFT OUTER JOIN Staff e USING (empId)
                LEFT OUTER JOIN Pet p USING (petId)
                LEFT OUTER JOIN Adoption n using (appId)
                WHERE a.status IN ('APP') AND n.adoptId IS NULL
                """);
            DB.executeUpdate("INSERT INTO Adoption VALUES (%d, ?, ?, ?, ?)"
                .formatted(DB.uniqueId("Adoption", "adoptId")),
                Prompt.integer("Application ID", null),
                Prompt.date("Adoption Date", "MM-dd-yyyy", null),
                Prompt.integer("Adoption Fee ($)", null),
                Prompt.stringNullable("follow up schedule", null));
            ProgramContext.successMessage("Finalized Adoption");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void newHealthEntry(){
        try {
            listPets();
            DB.executeUpdate("INSERT INTO HealthRecord VALUES (%d, 1, 'insert', CURRENT_DATE, ?, ?, ?, ?, ?, ?)"
                .formatted(DB.uniqueId("HealthRecord", "recId")),
                Prompt.integer("Pet ID", null),
                ProgramContext.getUserId(),
                Prompt.choice("Record Type", new String[]{"Vet","Chk","Sch","Grm","Bhn"}).toUpperCase(),
                Prompt.stringNullable("Record Description", null),
                Prompt.date("Next Due Date", "MM-dd-yyyy", null),
                Prompt.stringNullable("Record Status", null)
            );
            ProgramContext.successMessage("Created new Health Entry");
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void listMyEntries(){
        try{
            DB.printQuery("""
            SELECT r.recId as "ID", r.revNum as "Rev. #", r.revAction as "Rev. Action",
            r.revDate as "Last Modified", p.name as "Pet Name", r.recType as "Record Type",
            COALESCE(r.description, 'None') as "Description", TO_CHAR(r.nextDue, 'DY, MON, YYYY') as "Next Due",
            COALESCE(r.status, 'None') as "Status" FROM
            HealthRecord r LEFT OUTER JOIN Pet p USING (petId)
            WHERE r.empId=?""", ProgramContext.getUserId());
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    public static void updateEntry(Boolean delete){
        try {
            DB.printQuery("""
            SELECT r.recId as "ID", r.revNum as "Rev. #", r.revAction as "Rev. Action",
            TO_CHAR(r.revDate, 'MON-DD-YYYY') as "Last Modified", p.name as "Pet Name", 
            r.recType as "Record Type", COALESCE(r.description, 'None') as "Description", 
            TO_CHAR(r.nextDue, 'MON-DD-YYYY') as "Next Due", COALESCE(r.status, 'None') as "Status" 
            FROM HealthRecord r LEFT OUTER JOIN Pet p ON r.petId = p.petId
            WHERE (r.recId, r.revNum) IN ( SELECT recId, MAX(revNum) FROM HealthRecord GROUP BY recId)
            ORDER BY r.recId ASC""");
            var recNum = Prompt.integer("Record ID", null);
            var rs = DB.executeQuery("""
            SELECT r.revNum, r.petId, r.description, r.nextDue, r.status
            FROM HealthRecord r 
            WHERE r.recId = ? 
            ORDER BY r.revNum DESC FETCH FIRST 1 ROWS ONLY""", recNum);
            if(rs.next()){
                DB.executeUpdate("INSERT INTO HealthRecord VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    recNum,
                    rs.getInt(1) + 1, // increment rev auto
                    delete ? "delete" : "update",
                    new Date(System.currentTimeMillis()),
                    delete ? rs.getInt(2) : Prompt.integer("Pet ID", rs.getInt(2)),
                    ProgramContext.getUserId(),
                    Prompt.choice("Record Type", new String[]{"Vet","Chk","Sch","Grm","Bhn"}).toUpperCase(),
                    delete ? rs.getString(3) : Prompt.stringNullable("Record Description", rs.getString(3)),
                    delete ? rs.getDate(4) : Prompt.date("Next Due Date", "MM-dd-yyyy", rs.getDate(4)),
                    delete ? rs.getString(5) : Prompt.stringNullable("Record Status", rs.getString(5))
                );
                ProgramContext.successMessage("Updated Health Entry!");
            } else {throw new RuntimeException("ID did not match a pet in the database.");}
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }
}