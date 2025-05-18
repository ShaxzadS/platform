# Используем официальный образ JDK
FROM openjdk:17-jdk-slim

# Создаем рабочую директорию
WORKDIR /app

# Копируем jar-файл в контейнер (предполагается, что jar лежит рядом с Dockerfile)
COPY target/Grand-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 10000

# Активируем профиль prod, если у тебя есть application-prod.properties
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=10000

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
