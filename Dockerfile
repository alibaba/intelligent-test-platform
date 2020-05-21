FROM adoptopenjdk/maven-openjdk8:latest

ADD ./target/markov-demo-0.0.1-SNAPSHOT.jar /usr/local/markov-demo-0.0.1-SNAPSHOT.jar

# Add docker-compose-wait tool -------------------
ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/usr/local/markov-demo-0.0.1-SNAPSHOT.jar" ]