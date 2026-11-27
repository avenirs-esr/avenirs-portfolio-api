# syntax=docker/dockerfile:1

# ---- Build : compile le jar avec le wrapper Maven du repo
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /src

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY libs libs
COPY src src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -ntp -DskipTests package \
 && cp "$(find target -maxdepth 1 -name 'avenirs-portfolio-api-*.jar' ! -name '*-plain.jar' | head -n 1)" app.jar \
 && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# ---- Exécution : JRE seul, couches Spring Boot pour profiter du cache Docker
FROM eclipse-temurin:25-jre-alpine
WORKDIR /workspace/app

COPY --from=build /src/extracted/dependencies/ ./
COPY --from=build /src/extracted/spring-boot-loader/ ./
COPY --from=build /src/extracted/snapshot-dependencies/ ./
COPY --from=build /src/extracted/application/ ./

RUN mkdir -p target/storage logs

# La mémoire de la JVM suit la limite du conteneur au lieu d'un -Xmx fixe
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

EXPOSE 10000
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
