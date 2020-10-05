# Activity-Journal

To run postgresqlConn.java program on local machine:
* Need to have PostgreSQL installed on local machine
* Current username and password is not universale. You will need to change username and password for database to your username and password
    * Found on lines `23` and `24` of postgresqlConn.java
* You will need to have a database set up to be called 'db'
* Compile program with class path : 
>`javac -classpath ".:./postgresql-42.2.12.jar:" postgresqlConn.java`
* Run program: 
>`java -classpath ".:./postgresql-42.2.12.jar:" postgresqlConn`
