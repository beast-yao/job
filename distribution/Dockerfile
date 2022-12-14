
FROM openjdk:8u201-jdk-alpine3.9
LABEL MAINTAINER="devil.yao"

ARG VERSION
ENV VERSION $VERSION

#RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
#        && echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
#        && apk update upgrade \
#        && apk add --no-cache ttf-dejavu procps unzip curl bash tzdata \
#        && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
#        && echo "Asia/Shanghai" > /etc/timezone
ADD target/bt-job-server-${VERSION}.tar.gz /app/service

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
            && echo "Asia/Shanghai" > /etc/timezone
#            && chmod a+x /app/service/bt-job-server/bin/startup.sh

EXPOSE 18778
EXPOSE 10010

HEALTHCHECK --interval=10s --timeout=3s  --retries=3\
              CMD curl -fs http://127.0.0.1:18778/job/actuator/health || exit 1
#ENTRYPOINT ["/bin/sh","/app/service/bt-job-server/bin/startup.sh"]
ENTRYPOINT ["/bin/sh","/app/service/bt-job-server/target/bt-job-server.jar","run"]
