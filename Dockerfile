# ================================================================================================
# @FileName : Dockerfile
# @Program : D.W Kang
# @Date : 2024-10-01
# @Description  : docker 이미지 생성 파일
# ================================================================================================

# gradle로 jar 파일 생성
FROM openjdk:21-jdk-slim AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# 컨테이너로 jar 파일 이동
FROM openjdk:21-jdk-slim
COPY --from=builder build/libs/*.jar hey-backend.jar

# profile 설정
ARG PROFILE
ARG PORT
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

# 컨테이너에서 jar 파일 실행
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "/hey-backend.jar"]
