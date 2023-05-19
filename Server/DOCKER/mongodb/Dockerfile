FROM ubuntu:latest

RUN apt-get update
RUN apt-get install wget gnupg2 -y

RUN wget -qO - https://pgp.mongodb.com/server-6.0.asc | apt-key add -
RUN echo "deb [ arch=amd64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/6.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-6.0.list

RUN apt-get update
RUN apt-get install -y mongodb-org

RUN rm -rf /var/lib/apt/lists/*

RUN mkdir -p /data/db

WORKDIR /data

EXPOSE 27017:27017

CMD ["/usr/bin/mongod", "--dbpath", "/data/db", "--bind_ip_all"]