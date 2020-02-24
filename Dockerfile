FROM adoptopenjdk:8-jdk-hotspot
COPY Californium.properties build/libs/coap-dfu-server-1.0.jar build/install/coap-dfu-server/lib/*.jar /home/root/java/
WORKDIR /home/root/java
ENTRYPOINT ["java", "-cp", "./californium-core-2.1.0-RC1.jar:./coap-dfu-server-1.0.jar:./element-connector-2.1.0-RC1.jar:./json-20190722.jar:./slf4j-api-1.7.25.jar", "com.zenatix.Main", "main"]
