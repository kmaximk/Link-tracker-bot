FROM openjdk:21
WORKDIR output
COPY target/bot.jar output/bot.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "output/bot.jar"]
