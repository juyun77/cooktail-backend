# 기본 이미지 설정
FROM openjdk:17 as builder

# 환경 변수 설정: 언어와 시간대
ENV LC_ALL=C.UTF-8
ENV TZ=Asia/Seoul

# 시간대 설정
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 작업 디렉터리 설정
WORKDIR /app

# 현재 디렉터리의 모든 파일을 /app에 복사
COPY . .

# gradlew 실행 가능하게 변경
RUN chmod u+x gradlew

# 프로젝트 빌드
RUN ./gradlew build

# 빌드된 jar 파일을 실행하기 위해 빌드 결과물을 다른 스테이지로 복사
FROM openjdk:17

WORKDIR /app

# builder 스테이지에서 빌드된 jar 파일을 현재 스테이지로 복사
COPY --from=builder /app/build/libs/backend-0.0.1-SNAPSHOT.jar .

# 컨테이너 시작 시 실행될 명령
CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]





#FROM openjdk:17 as builder
#
#
## Language
#ENV LC_ALL=C.UTF-8
#
## timezone
#ENV TZ=Asia/Seoul
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#
#WORKDIR app
#
#COPY ./build/libs/backend-0.0.1-SNAPSHOT.jar .
#CMD java -jar backend-0.0.1-SNAPSHOT.jar
