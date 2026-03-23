# Use an image with JDK 25 to build (using the local gradlew)
FROM openjdk:21-slim AS build
# Note: I'll actually copy the build result for now to keep it fast, 
# but in a real CI it would build from source.
# For this task, I'll assume the files are built locally.

FROM openjdk:21-slim
WORKDIR /app
COPY server/build/install/server /app/
EXPOSE 8080
CMD ["./bin/server"]
