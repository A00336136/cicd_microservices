FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/lifestyle-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
