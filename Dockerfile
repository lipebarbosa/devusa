# Estágio 1: Build da aplicação Java
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Imagem leve de produção
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S devusa && adduser -S devusa -G devusa
USER devusa
COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} app.jar"]

