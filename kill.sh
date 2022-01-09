### Remove swarm service:
docker service rm "$(docker service ls -q)"
### Stop all container
docker stop $(docker ps -aq)
### Prune docker
docker container prune --force
docker volume prune --force
docker network prune --force
### To leave swarm mode:
# docker swarm leave --force

sleep 5
