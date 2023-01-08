FROM openjdk:18

ARG JAR_FILE=target/*.jar

ENV BOT_NAME=${BOT_NAME}
ENV BOT_TOKEN=${BOT_TOKEN}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.datasource.password=${DB_PASSWORD}", "-Dspring.datasource.username=${DB_USERNAME}", "-Dbot.username=${BOT_NAME}", "-Dbot.token=${BOT_TOKEN}", "-jar", "/app.jar"]
