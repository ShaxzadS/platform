# Используем официальный образ JDK
FROM openjdk:17-jdk-slim

# Копируем jar-файл в контейнер
COPY app.jar app.jar



# Порт, который будет слушать приложение
EXPOSE 10000


ENV SPRING_PROFILES_ACTIVE=prod

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
