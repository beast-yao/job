
FROM openjdk:8u201-jdk-alpine3.9
MAINTAINER devil.yao

ARG VERSION
ENV VERSION $VERSION

RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
        && echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
        && apk update upgrade \
        && apk add --no-cache ttf-dejavu procps unzip curl bash tzdata \
        && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
        && echo "Asia/Shanghai" > /etc/timezone

ADD target/bt-job-server-${VERSION}.tar.gz /app/service
RUN chmod a+x /app/service/bt-job/bin/startup.sh

EXPOSE 8999
EXPOSE 10010

ENTRYPOINT ["/bin/sh","/app/service/bt-job/bin/startup.sh"]
CMD []
