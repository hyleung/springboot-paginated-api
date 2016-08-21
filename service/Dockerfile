FROM java:openjdk-8-alpine

MAINTAINER hy.leung@gmail.com <H.Y. Leung>

COPY build/libs/api.jar /opt/app/
WORKDIR /opt/app
CMD ["java", "-jar", "api.jar"]


