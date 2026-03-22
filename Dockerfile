# Build stage
FROM maven:3.9.14-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/car_stereo_wiki-0.0.1-SNAPSHOT.jar app.jar

# Environment Variables
ENV DB_URL="jdbc:mysql://localhost:3306/car_stereo_wiki"
ENV DB_USERNAME=""
ENV DB_PASSWORD=""
ENV GITHUB_CLIENT_ID="YOUR_GITHUB_CLIENT_ID"
ENV GITHUB_CLIENT_SECRET="YOUR_GITHUB_CLIENT_SECRET"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
