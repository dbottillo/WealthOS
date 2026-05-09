# Stage 1: Build the Wasm distribution
FROM eclipse-temurin:25-jdk AS build
WORKDIR /home/gradle/src
COPY . .
RUN ./gradlew :composeApp:wasmJsBrowserDistribution --no-daemon

# Stage 2: Serve with Nginx
FROM nginx:alpine
# Copy the built production executable
COPY --from=build /home/gradle/src/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
# Copy missing skiko runtime files (needed for Wasm to run)
COPY --from=build /home/gradle/src/composeApp/build/compose/skiko-for-web-runtime/* /usr/share/nginx/html/
COPY web/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
