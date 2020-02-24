FROM gradle:jdk10 as builder
COPY --chown=gradle:gradle . /home/gradle/dfu-srv
WORKDIR /home/gradle/dfu-srv
RUN gradle --no-daemon distTar

FROM adoptopenjdk:8-jdk-hotspot
COPY --from=builder /home/gradle/dfu-srv/build/distributions/dfu-srv*.tar /home/root/java/
WORKDIR /home/root/java
RUN tar -xvf dfu-srv*.tar
WORKDIR /home/root/java/dfu-srv-1.0
COPY --from=builder /home/gradle/dfu-srv/Californium.properties .
CMD bin/dfu-srv
