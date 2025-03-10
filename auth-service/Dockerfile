FROM openjdk:17-alpine
WORKDIR /app
COPY build/libs/auth-service-0.0.1-SNAPSHOT.jar /app
EXPOSE 8099
CMD ["java", "-jar", "auth-service-0.0.1-SNAPSHOT.jar"]