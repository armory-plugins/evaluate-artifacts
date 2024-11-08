FROM armory/armory-alpine-base-jre:latest

MAINTAINER spin-klo-team@harness.io

RUN mkdir -p /opt/plugin

ADD build/distributions/evaluate-artifacts-* /opt/plugin/
ADD build-tools/install.sh /usr/local/bin/install.sh

RUN adduser -D spinnaker && addgroup spinnaker spinnaker

ENTRYPOINT ["sh", "/usr/local/bin/install.sh"]