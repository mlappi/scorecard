FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.war /app/app.war
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar /app/app.war"]
