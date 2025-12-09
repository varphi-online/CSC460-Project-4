Prog4.class : Prog4.java Menu.class UI.class DB.class ProgramContext.class Prompt.class
	javac Prog4.java

Menu.class UI.class : Menu.java UI.java
	javac Menu.java UI.java

DB.class : DB.java
	javac -cp .:misc_files/h2-2.4.240.jar DB.java

ProgramContext.class : ProgramContext.java
	javac ProgramContext.java

Prompt.class : Prompt.java
	javac Prompt.java

.PHONY: clean
clean:
	rm -rf *.class
	rm -rf *.db