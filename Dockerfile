FROM ubuntu:bionic

ENV DEBIAN_FRONTEND=noninteractive

RUN    apt-get update            \
    && apt-get install --yes     \
        autogen                  \
        automake                 \
        autoconf                 \
        autotools-dev            \
        build-essential          \
        curl                     \
        gcc                      \
        git                      \
        libtool                  \
        openjdk-8-jdk            \
        shtool

ADD . /src
ADD https://dlcdn.apache.org/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.tar.gz /src

RUN cd /src && tar xvf apache-maven-3.8.3-bin.tar.gz
ENV PATH="/src/apache-maven-3.8.3/bin:${PATH}"

VOLUME /output

WORKDIR /src
ENTRYPOINT ["src/main/scripts/build-linux64.sh"]
