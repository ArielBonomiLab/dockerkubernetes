ARG MSVC_NAME=msvc-usuarios

FROM openjdk:17-jdk-alpine as builder

ARG MSVC_NAME
WORKDIR /app/$MSVC_NAME

COPY ./pom.xml /app
COPY ./$MSVC_NAME/.mvn ./.mvn
COPY ./$MSVC_NAME/mvnw .
COPY ./$MSVC_NAME/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
#RUN ./mvnw dependency:go-offline
COPY ./$MSVC_NAME/src ./src

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app
RUN mkdir ./logs

ARG MSVC_NAME
ARG TARGET_FOLDER=/app/$MSVC_NAME/target
COPY --from=builder $TARGET_FOLDER/msvc-usuarios-0.0.1-SNAPSHOT.jar .

ENV PORT 8001
EXPOSE $PORT

CMD ["java", "-jar", "msvc-usuarios-0.0.1-SNAPSHOT.jar"] 