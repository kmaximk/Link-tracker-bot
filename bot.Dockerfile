FROM openjdk:21
WORKDIR output
COPY . output/bot.jar

EXPOSE 8090
