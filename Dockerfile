FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/PokedexRoute-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]