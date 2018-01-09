echo -en "\nentering the following container: $1\n"
docker_pid=$(sudo docker inspect --format '{{ .State.Pid }}' $1)
sudo nsenter -m -u -n -i -p -t $docker_pid /bin/bash
