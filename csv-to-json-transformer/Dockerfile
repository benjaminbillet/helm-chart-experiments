FROM openjdk:11-jre-slim

WORKDIR /app

RUN groupadd --gid 1001 app && useradd --uid 1001 app --gid 1001

COPY --chown=app:app target/*.jar /app
COPY --chown=app:app docker/ /app

RUN chmod 755 entrypoint.sh

USER 1001:1001

EXPOSE 8443
ENTRYPOINT [ "./entrypoint.sh" ]
