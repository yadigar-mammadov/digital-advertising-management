FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

EXPOSE 8080

CMD ["mvn", "-Dspotless.check.skip=true", "spring-boot:run"]