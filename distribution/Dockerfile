
FROM openjdk:8u201-jdk-alpine3.9
MAINTAINER devil.yao

ARG VERSION
ENV VERSION $VERSION

#RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
#        && echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
#        && apk update upgrade \
#        && apk add --no-cache ttf-dejavu procps unzip curl bash tzdata \
#        && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
#        && echo "Asia/Shanghai" > /etc/timezone

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
            && echo "Asia/Shanghai" > /etc/timezone

ADD target/bt-job-server-${VERSION}.tar.gz /app/service
RUN chmod a+x /app/service/bt-job/bin/startup.sh

EXPOSE 8999
EXPOSE 10010

HEALTHCHECK --interval=10s --timeout=3s  --retries=3\
              CMD curl -fs http://127.0.0.1:8778/job/actuator/health || exit 1
ENTRYPOINT ["/bin/sh","/app/service/bt-job/bin/startup.sh"]
CMD ["tail -f /app/service/bt-job/logs/jobs.log"]
