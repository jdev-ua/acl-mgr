FROM openjdk:17-jdk-alpine
LABEL maintainer="jdev.pp.ua@gmail.com"

ARG JAR_FILE=target/*.jar
ARG SRC_FILE=/src/test/resources/bnk-security-config-simple.xml

COPY ${JAR_FILE} app.jar

RUN mkdir /tmp/data
COPY ${SRC_FILE} /tmp/data

ENV TZ=Europe/Kiev

ENTRYPOINT ["java","-jar","/app.jar"]