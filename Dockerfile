FROM openjdk:8

RUN mkdir /src

Add . /src

WORKDIR /src/service
