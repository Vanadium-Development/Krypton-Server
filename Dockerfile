FROM gradle:alpine

WORKDIR /app

COPY ./build/libs/server-*.jar server.jar

ENTRYPOINT ["java", "-jar", "server.jar", "-Dspring.profiles.active=prod"]