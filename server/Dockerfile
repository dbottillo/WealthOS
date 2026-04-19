# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /home/gradle/src
COPY . .
RUN ./gradlew :server:installDist --no-daemon

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/server/build/install/server /app/
EXPOSE 8080
CMD ["./bin/server"]
