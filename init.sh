### PostreSQL and MongoDB
docker-compose up -d

### Scaled backend service sine-wave
docker build . -t sine-wave:latest
docker swarm init
docker stack deploy -c docker-swarm.yml swarm

### Load balancer nginx
cd nginx && docker-compose up -d && cd ..

### Load testing tool vegeta
cd vegeta && docker-compose up -d && cd ..

### To remove swarm service:
# docker service rm $(docker service ls -q)
### To leave swarm mode:
# docker swarm leave --force

sleep 5
