FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD kn-service.jar /app.jar
ENV TZ=Asia/Shanghai
RUN echo "http://mirrors.aliyun.com/alpine/v3.12/main/" > /etc/apk/repositories && apk add --no-cache procps curl bash tzdata && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone
RUN set -xe && apk --no-cache add ttf-dejavu fontconfig
ENTRYPOINT java -XX:-OmitStackTraceInFastThrow -XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap $JAVA_OPTS -jar -Dorg.jboss.netty.epollBugWorkaround=true -Duser.timezone=Asia/Shanghai /app.jar