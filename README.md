# ToDo Application

Put some cool description here

## Requirements

- for simple local run `docker` and `docker-compose` should be installed
- for development Java 21, Gradle should be installed

## Stack

I will put everything here someday...

## Build

- run only unit tests: `./gradlew test`
- run only integration tests: `./gradlew integrationTest -x test`
- build without tests: `./gradlew build -x test`
- build with unit tests: `./gradlew build`
- build with both unit and integration tests: `./gradlew build -Dtest.profile=integration`
- build test coverage report (first `test` and `integrationTest` should be executed: `./gradlew jacocoTestReport`
- build image locally: `docker build -t to-do-app .`

## Local run

- run dockerized full BE with infra: `docker-compose --profile backend up`
- run dockerized full BE with infra rebuilding the BE image: `docker-compose --profile backend up --build`
- run local infra without BE itself, it's assumed BE will be run in dev mode with IDE: `docker-compose up`