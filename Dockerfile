# Use Eclipse Temurin JDK 17 base image
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/machinemonitorsystem-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
