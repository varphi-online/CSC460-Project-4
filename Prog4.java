public class Prog4 {
    public static void main(String[] args) {
        UI ui = new UI();

        ui.setInitialMode(
            new Mode("Home", ()->{System.out.println("Welcome to El Jefe Cat Cafe!");}).addSubMode(new Mode[] {
                new Mode("Customer Login", ()->{/**TODO: Login and set progctx*/ System.out.println("hi");}).addSubMode(new Mode[] {
                    new Mode("Food Orders").addSubMode(new Mode[] {
                        new Mode("New Order", ()->{/**TODO: food and bev sep modes?*/}),
                        new Mode("Update Order", ()->{/**TODO: */}),
                        new Mode("Delete Order (Mistake!)", ()->{/**TODO: */})
                    }),
                    new Mode("Manage Reservations").addSubMode(new Mode[] {
                        new Mode("Book a Reservation", ()->{/**TODO: */}),
                        new Mode("Remove a Reservation", ()->{/**TODO: following is an example*/ 
                            ProgramContext.setStatusMessage("Reservation removed!",ProgramContext.Color.GREEN);}),
                        new Mode("Update a Reservation").addSubMode(new Mode[] {
                            new Mode("Reschedule", ()->{/**TODO: */}),
                            new Mode("Extend Duration", ()->{/**TODO: */})
                        }),
                        new Mode("Check In", ()->{/**TODO: */}),
                        new Mode("Check Out", ()->{/**TODO: */})
                    }),
                    new Mode("Manage Events").addSubMode(new Mode[] {
                        new Mode("Show Registered Events", ()->{/**TODO: */}),
                        new Mode("Register for Event", ()->{/**TODO: */}),
                    }),
                    new Mode("Edit Profile").addSubMode(new Mode[] {
                        new Mode("Change Membership Tier.", ()->{/**TODO: */}),
                        new Mode("Change Phone #", ()->{/**TODO: */}),
                        new Mode("Change e-Mail", ()->{/**TODO: */}),
                        new Mode("Book a Emergency Contact", ()->{/**TODO: */}),
                    }),
                }),
                new Mode("Customer Registration", ()->{/**TODO:*/}),
                new Mode("Staff Dashboard")
            })
        );

        // System.out.println("Welcome to El Jefe Cat Cafe!");
        ui.run();
    }
}