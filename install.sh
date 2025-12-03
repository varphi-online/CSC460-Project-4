rm *.db
javac -cp .:misc_files/h2-2.4.240.jar -d . DB.java misc_files/Install.java
java -cp .:misc_files/h2-2.4.240.jar Install