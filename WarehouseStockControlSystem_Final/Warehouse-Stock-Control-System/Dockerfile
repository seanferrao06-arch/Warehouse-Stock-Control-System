FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src
COPY schema.sql ./schema.sql

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=/opt/java/openjdk/bin:$PATH
ENV LD_LIBRARY_PATH=/opt/java/openjdk/lib/server:/opt/java/openjdk/lib
ENV PORT=80

# Symlink java binary across system PATH
RUN ln -sf /opt/java/openjdk/bin/java /usr/bin/java && \
    ln -sf /opt/java/openjdk/bin/java /usr/local/bin/java && \
    ln -sf /opt/java/openjdk/bin/java /bin/java

COPY --from=builder /build/target/warehouse-stock-control-system-1.0.0.jar /app/app.jar

EXPOSE 80

# Optimized for sub-2-second serverless cold starts
CMD ["/opt/java/openjdk/bin/java", "-XX:TieredStopAtLevel=1", "-XX:+UseSerialGC", "-Xms128m", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=80", "-Dserver.address=0.0.0.0", "-jar", "/app/app.jar"]
