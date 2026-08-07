# 軽量JRE 25 アルパインイメージ
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# ビルド済みJARファイルをコピー
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
