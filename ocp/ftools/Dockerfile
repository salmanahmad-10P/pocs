FROM fedora:26

# Build:
#   docker build --rm -t fedoratools:1.0 .
#   docker tag fedoratools:1.0 docker.io/rhtgptetraining/fedoratools:1.0

# Execution: docker
#   docker run --rm fedoratools:1.0 

# Execution: OpenShift
#   oc run ftools --image=docker.io/rhtgptetraining/fedoratools:1.0

# Executes a command in a new layer and creates a new image
RUN dnf install -y net-tools lsof bind-utils nmap-ncat curl wget && yum clean all

ADD loop.sh /usr/bin/loop.sh

# ENTRYPOINT command and parameters are not ignored when Docker container runs with command line parameters.
# Exec form of ENTRYPOINT allows for setting of commands and parameters and then use either form of CMD to set additional params that are more likely to be changed.
ENTRYPOINT ["/usr/bin/loop.sh", "5"]
