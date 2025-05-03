# Используем официальный образ JDK
FROM openjdk:17-jdk-slim

# Копируем jar-файл в контейнер
COPY target/*.jar app.jar


# Порт, который будет слушать приложение
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]
