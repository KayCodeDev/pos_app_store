# FROM openjdk:21-jdk-slim AS build

# RUN apt-get update && apt-get install -y maven

# WORKDIR /build

# COPY pom.xml .

# COPY src ./src/

# RUN mvn clean package -DskipTests

# FROM openjdk:21-jdk-slim

# WORKDIR /app

# COPY --from=build /build/target/itexstore-0.0.1-SNAPSHOT.jar /app/app.jar

# EXPOSE 9090
# EXPOSE 9091
# EXPOSE 9092

# CMD ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-alpine AS build

RUN apk update && apk add maven

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-alpine

WORKDIR /app
COPY --from=build /build/target/itexstore-0.0.1-SNAPSHOT.jar /app/app.jar

# RUN mkdir -p logs && \
#     addgroup --system appgroup && \
#     adduser --system appuser --ingroup appgroup && \
#     chown -R appuser:appgroup logs

# USER appuser

EXPOSE 9090
# EXPOSE 9091
# EXPOSE 9092

# ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

CMD ["java", "-jar", "app.jar"]

