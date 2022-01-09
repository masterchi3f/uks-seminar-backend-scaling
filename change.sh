# Scaled backends: Change the replica amount and port range in docker-swarm.yml
# Load balancing: Apply these changes to the servers in nginx/nginx.conf
# Monitoring: Apply these changes to the scrape endpoints in monitoring/prometheus-config.yml
# Then run this file:
docker service rm "$(docker service ls -q)"
docker stack deploy -c docker-swarm.yml swarm
cd nginx && docker-compose restart && cd ..
docker exec -it nginx //bin/sh -c 'nginx -t && nginx -s reload'
cd monitoring && docker-compose restart prometheus && cd ..

sleep 5
