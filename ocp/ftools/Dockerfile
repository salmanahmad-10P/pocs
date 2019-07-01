FROM fedora:30

# Build:
#   sudo podman build --rm -t fedoratools:2.5 .
#   sudo podman tag fedoratools:2.5 quay.io/rhtgptetraining/fedoratools:2.5

# Docker push (on F30):
#   Make sure the following is the *first* entry in the docker daemon (by updating /etc/sysconfig/docker):  --add-registry docker.io
#   sudo podman login quay.io -u rhtgptetraining
#   sudo podman push quay.io/rhtgptetraining/fedoratools:2.5

# View details of the image in the remote docker registry
# skopeo inspect docker://quay.io/rhtgptetraining/fedoratools:2.5

# Execution: docker
#   sudo podman run --rm fedoratools:2.5

# Execution: OpenShift
#   oc run ftools --image=quay.io/rhtgptetraining/fedoratools:2.5           :   creates a DC
#   oc create -f ftools-deploy.yml                                          :   creates a deployment

# Execution: Istio enabled
#   istioctl kube-inject -f ftools-deploy.yml > $HOME/lab/ftools-istio.yml
#   oc create -f $HOME/lab/ftools-istio.yml

# Executes a command in a new layer and creates a new image
RUN dnf install -y iproute iputils telnet net-tools lsof bind-utils nmap-ncat curl wget git swaks podman skopeo speedtest-cli jq tidy xmlstarlet && dnf clean all

ADD loop.sh /usr/bin/loop.sh

# ENTRYPOINT command and parameters are not ignored when Docker container runs with command line parameters.
# Exec form of ENTRYPOINT allows for setting of commands and parameters and then use either form of CMD to set additional params that are more likely to be changed.
ENTRYPOINT ["/usr/bin/loop.sh", "5"]
