# Scorecard app

## How to run
mvn clean package

java -jar ./target/golfapp-0.0.1-SNAPSHOT.war

http://localhost:8080/

## Eclipse
install https://projectlombok.org/

import maven project

## PSQL
uncomment DatabaseConfig.java @Configuration and @ServletComponentScan

uncomment spring.datasource.* from application.properties

install local psql

uncomment org.postgresql dependency from pom.xml

comment org.hsqldb dependency from pom.xml





