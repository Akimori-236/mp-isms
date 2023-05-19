# ANGULAR
# FROM node:16 AS frontend

# WORKDIR /app
# COPY package.json package-lock.json /app/
# RUN npm install
# COPY . /app
# RUN cd ../client && ng build

# FIRST STAGE
FROM maven:3.8.3-openjdk-17 AS build
RUN mvn --version

ENV MONGO_URL=mongodb://localhost:27017
ENV MONGOHOST=mongodb
ENV MONGOPORT=27017
ENV MONGODB=musicstore
ENV MONGOUSER=root
ENV MONGOPASSWORD=1234
ENV SHA256_SECRET=ac3a3d8c5aa2aee08570d048d0c175c45d08b551b8560e5d4d6cc263cb56a7f5


COPY src /home/app/src
COPY pom.xml /home/app

# copy angular build files to springboot
# COPY --from=frontend /app/dist/client /home/app/src/main/resources/static

# generate a JAR file
RUN mvn -f /home/app/pom.xml clean package

##############
# SECOND STAGE
FROM openjdk:17-oracle

ENV MONGO_URL=mongodb://localhost:27017
ENV MONGOHOST=mongodb
ENV MONGOPORT=27017
ENV MONGODB=musicstore
ENV MONGOUSER=root
ENV MONGOPASSWORD=1234

# copy from build stage = first stage
COPY --from=build /home/app/target/server-0.0.1-SNAPSHOT.jar /usr/local/lib/server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/server.jar"]
