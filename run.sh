#Linux auto run

mvn clean
mvn package
java -jar target/reggie_take_out-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
