FROM openjdk:21
WORKDIR output
COPY target/scrapper.jar output/scrapper.jar

EXPOSE 9191

ENTRYPOINT ["java", "-jar", "output/scrapper.jar"]
