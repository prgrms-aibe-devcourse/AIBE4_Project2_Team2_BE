# Stage 1: Build (빌드 단계)
FROM azul/zulu-openjdk:17-latest AS build
WORKDIR /app

# 1. Gradle 캐싱을 위해 의존성 파일들만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

# 2. 소스 코드 복사 및 빌드 (테스트 제외)
COPY src src
RUN ./gradlew clean build -x test

# Stage 2: Runtime (실행 단계)
FROM azul/zulu-openjdk:17-latest
WORKDIR /app

# 3. 빌드 스테이지에서 실행 가능한 JAR 파일만 콕 집어서 복사
# (*-SNAPSHOT.jar로 지정하여 plain.jar가 복사되는 것을 방지합니다)
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# 4. 포트 및 프로필 설정
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

# 5. 실행
ENTRYPOINT ["java", "-jar", "app.jar"]