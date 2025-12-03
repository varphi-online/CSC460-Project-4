public class Prog4 {
    public static void main(String[] args) {
        UI ui = new UI();

        ui.setInitialMode(
            new Menu("Home", ()->{System.out.println("Welcome to El Jefe Cat Cafe!");}).addSubMenu(new Menu[] {
                new Menu("Customer Login", ()->{/**TODO: Login and set progctx*/ System.out.println("hi");}).addSubMenu(new Menu[] {
                    new Menu("Food Orders").addSubMenu(new Menu[] {
                        new Menu("New Order", ()->{/**TODO: food and bev sep modes?*/}),
                        new Menu("Update Order", ()->{/**TODO: */}),
                        new Menu("Delete Order (Mistake!)", ()->{/**TODO: */})
                    }),
                    new Menu("Manage Reservations").addSubMenu(new Menu[] {
                        new Menu("Book a Reservation", ()->{/**TODO: */}),
                        new Menu("Remove a Reservation", ()->{/**TODO: following is an example*/ 
                            ProgramContext.setStatusMessage("Reservation removed!",ProgramContext.Color.GREEN);}),
                        new Menu("Update a Reservation").addSubMenu(new Menu[] {
                            new Menu("Reschedule", ()->{/**TODO: */}),
                            new Menu("Extend Duration", ()->{/**TODO: */})
                        }),
                        new Menu("Check In", ()->{/**TODO: */}),
                        new Menu("Check Out", ()->{/**TODO: */})
                    }),
                    new Menu("Manage Events").addSubMenu(new Menu[] {
                        new Menu("Show Registered Events", ()->{/**TODO: */}),
                        new Menu("Register for Event", ()->{/**TODO: */}),
                    }),
                    new Menu("Edit Profile").addSubMenu(new Menu[] {
                        new Menu("Change Membership Tier.", ()->{/**TODO: */}),
                        new Menu("Change Phone #", ()->{/**TODO: */}),
                        new Menu("Change e-Mail", ()->{/**TODO: */}),
                        new Menu("Book a Emergency Contact", ()->{/**TODO: */}),
                    }),
                }),
                new Menu("Customer Registration", ()->{/**TODO:*/}),
                new Menu("Staff Dashboard")
            })
        );

        // System.out.println("Welcome to El Jefe Cat Cafe!");
        ui.run();
    }
}