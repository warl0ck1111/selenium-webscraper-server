FROM openjdk:17
COPY target/selenium-webscraper-server.jar selenium-webscraper-server.jar
ENTRYPOINT ["java","-jar","/selenium-webscraper-server.jar"]