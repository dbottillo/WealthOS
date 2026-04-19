# Stage 1: Build the application
FROM openjdk:21-slim AS build
WORKDIR /home/gradle/src
COPY . .
RUN ./gradlew :server:installDist --no-daemon

# Stage 2: Runtime image
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /home/gradle/src/server/build/install/server /app/
EXPOSE 8080
CMD ["./bin/server"]
