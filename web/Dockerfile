# Stage 1: Build the Wasm distribution
FROM openjdk:21-slim AS build
WORKDIR /home/gradle/src
COPY . .
RUN ./gradlew :composeApp:wasmJsBrowserDistribution --no-daemon

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /home/gradle/src/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY web/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
