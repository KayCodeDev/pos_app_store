FROM eclipse-temurin:24-alpine AS build

RUN apk update && apk add maven

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-alpine

WORKDIR /app
COPY --from=build /build/target/appstore-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 9090

CMD ["java", "-jar", "app.jar"]

