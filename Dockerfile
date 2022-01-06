FROM gradle:jdk11-alpine AS builder
WORKDIR /home/gradle/src

COPY . .
RUN gradle build --no-daemon

FROM openjdk:13-alpine
WORKDIR /usr/src/app

COPY --from=builder /home/gradle/src/build/libs/sine-wave-0.0.1-all.jar sine-wave.jar

EXPOSE 8080:8080
CMD java -jar sine-wave.jar -P:ktor.deployment.dbHost=host.docker.internal -P:ktor.deployment.host=0.0.0.0
