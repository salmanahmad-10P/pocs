FROM fedora:33

ENV JAVA_HOME /etc/alternatives/java_sdk

ENV M2_MIRROR_URL http:\/\/nexus.user1-tools-erd:8081\/content\/groups\/public
#ENV M2_MIRROR_URL http:\/\/localhost:8085\/repository\/maven-rht'

# Set location of local npm cache to a directory with write permissions when working in OpenShift
ENV npm_config_cache=/build/.npm-global

# Executes a command in a new layer and creates a new image
RUN dnf install -y httpie mycli python3-pip which iproute iputils telnet net-tools lsof bind-utils nmap-ncat curl wget git swaks podman skopeo speedtest-cli jq tidy xmlstarlet postgresql community-mysql iptables procps-ng util-linux siege java-latest-openjdk-devel findutils npm && dnf clean all

ADD loop.sh /usr/bin/loop.sh

COPY home_configs/.m2/settings.xml /settings.xml

ENV MVN_VERSION=3.6.3
ENV MAVEN_HOME=/opt/apache-maven-$MVN_VERSION

RUN wget http://mirror.metrocast.net/apache/maven/maven-3/$MVN_VERSION/binaries/apache-maven-$MVN_VERSION-bin.tar.gz -O /tmp/apache-maven-$MVN_VERSION-bin.tar.gz \
    && tar -zxvf /tmp/apache-maven-$MVN_VERSION-bin.tar.gz -C /opt \
    && ln -s $MAVEN_HOME/bin/mvn /usr/local/bin/mvn

EXPOSE 8080 9000 5005

# Sets the directory and file permissions to allow users in the root group to access them in the built image
# Because the container user is always a member of the root group, the container user can read and write these files
RUN mkdir /build && chgrp -R 0 /build && \
    chmod -R g=u /build
VOLUME /build

# ENTRYPOINT command and parameters are not ignored when Docker container runs with command line parameters.
# Exec form of ENTRYPOINT allows for setting of commands and parameters and then use either form of CMD to set additional params that are more likely to be changed.
ENTRYPOINT ["/usr/bin/loop.sh", "60"]
