FROM openjdk:8-jdk-alpine
VOLUME /tmp

COPY target/*.jar design8or.jar
ENTRYPOINT ["java","-jar","/design8or.jar"]