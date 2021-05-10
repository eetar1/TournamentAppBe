FROM openjdk:11-jre-slim

ADD --chown=root:root build/libs/tournament-1.0.jar /app/

WORKDIR /app/

CMD ["java", "-jar", "/app/tournament-1.0.jar", "com/tournament/tournament/TournamentApplication.java"]