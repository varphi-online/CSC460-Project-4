# CSC460 Final Project

TAs:
To run the program, you need to seed the DB by connecting to aloe from the misc_files folder. Then run `@ Schema.sql`, then `@ seed.sql`.
Once finished seeding, add the nessecary classpath with:

`export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}`

and run `javac *.java && java Prog4 <username> <password>` to start the program

# Contributions:
Aidan Fuhrmann: Created menu system and project structure, implemented all Member callbacks and some Staff callbacks, transferred project to lectura from local development.

Aidan DiNunzio: Maintained the database diagram throughout the project, populated relations with sample data, and created the design PDF.

Jesse Oved: Made adjustments to database schema, designed API to prompt users for different kinds of input, implemented some of the program's CRUD functionality, designed the custom query.

# E-R Diagram
![E-R Diagram](misc_files/Final_ER.png)
