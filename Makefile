Prog4.class : Prog4.java Menu.class UI.class DB.class ProgramContext.class Prompt.class
	javac -g Prog4.java

Menu.class UI.class : Menu.java UI.java
	javac -g Menu.java UI.java

DB.class : DB.java
	javac -g -cp .:misc_files/h2-2.4.240.jar DB.java

ProgramContext.class : ProgramContext.java
	javac g ProgramContext.java

Prompt.class : Prompt.java
	javac -g Prompt.java

.PHONY: clean
clean:
	rm -rf *.class
	rm -rf *.db