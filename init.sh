### PostreSQL and MongoDB
docker-compose up -d

### Scaled backend service sine-wave
docker swarm init
docker stack deploy -c docker-swarm.yml swarm

### Load balancer Nginx
cd nginx && docker-compose up -d && cd ..

### Load testing tool Vegeta
cd vegeta && docker-compose up -d && cd ..

# Download and install windows_exporter (has to run on local machine)
# https://github.com/prometheus-community/windows_exporter/releases/download/v0.17.1/windows_exporter-0.17.1-amd64.msi
### Monitoring with Prometheus, Grafana, cAdvisor and windows_exporter
cd monitoring && docker-compose up -d && cd ..

sleep 5
