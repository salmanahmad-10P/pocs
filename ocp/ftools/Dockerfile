FROM fedora:27

# Build:
#   docker build --rm -t fedoratools:2.4 .
#   docker tag fedoratools:2.4 docker.io/rhtgptetraining/fedoratools:2.4

# Docker push (on F27):
# Make sure the following is the *first* entry in the docker daemon (by updating /etc/sysconfig/docker):  --add-registry docker.io
# docker login docker.io -u rhtgptetraining
# docker push docker.io/rhtgptetraining/fedoratools:2.4

# View details of the image in the remote docker registry
# skopeo inspect docker://docker.io/rhtgptetraining/fedoratools:2.4

# Execution: docker
#   docker run --rm fedoratools:2.4

# Execution: OpenShift
#   oc run ftools --image=docker.io/rhtgptetraining/fedoratools:2.4

# Executes a command in a new layer and creates a new image
RUN dnf install -y iproute iputils telnet net-tools lsof bind-utils nmap-ncat curl wget git && dnf clean all

ADD loop.sh /usr/bin/loop.sh

# ENTRYPOINT command and parameters are not ignored when Docker container runs with command line parameters.
# Exec form of ENTRYPOINT allows for setting of commands and parameters and then use either form of CMD to set additional params that are more likely to be changed.
ENTRYPOINT ["/usr/bin/loop.sh", "5"]
