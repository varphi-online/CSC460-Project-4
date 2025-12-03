public class Prog4 {
    public static void main(String[] args) {
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
                new Menu("Customer Registration", ()->{/**TODO: Req 1 (Insert)*/}),
                new Menu("Our Pets", ()->{/**TODO: Public pet list*/}), 
                
                new Menu("Customer Dashboard", ()->{/**TODO: Login*/}).addSubMenu(new Menu[] {
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
                        new Menu("View Personal Details", ()->{/**TODO: Simple SELECT*/}),
                        new Menu("Change Membership Tier", ()->{/**TODO: Req 1 (Update)*/}),
                        new Menu("Update Contact Info", ()->{/**TODO: Req 1 (Update)*/}),
                        new Menu("Delete Account", ()->{/**TODO: Req 1 (Delete - w/ logic checks)*/}),
                    }),
                }),
                new Menu("Staff Dashboard", ()->{/**TODO: Staff login*/}).addSubMenu(new Menu[] {
                    new Menu("Front Desk Operations").addSubMenu(new Menu[] {
                         new Menu("Check Customer In", ()->{/**TODO: Req 4 (Update status)*/}),
                         new Menu("Check Customer Out", ()->{/**TODO: Req 4 (Update status)*/}),
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
    }
}