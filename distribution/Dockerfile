
FROM openjdk:8-jre-alpine
MAINTAINER devil.yao

ARG VERSION
ENV VERSION $VERSION

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
        && echo "Asia/Shanghai" > /etc/timezone

WORKDIR /opt/service
ADD target/bt-job-server-${VERSION}.tar.gz ./
RUN chmod +x /opt/service/bt-job/bin/start.sh

EXPOSE 8999
EXPOSE 10010

ENTRYPOINT ["/opt/service/bt-job/bin/start.sh"]
CMD []
