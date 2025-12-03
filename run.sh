#export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}

#dev
make -f Makefile Prog4.class
java -cp .:misc_files/h2-2.4.240.jar Prog4