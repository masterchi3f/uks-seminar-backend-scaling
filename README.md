# Horizontal scaling of a backend service

### Prerequisites

1. Run ``docker build . -t sine-wave:latest`` in the root to build the backend image.
2. In [nginx.conf](./nginx/nginx.conf) change the IPs of the servers to your own IPs
   (on single node scaling ```docker.host.internal``` will also work).
3. In [prometheus-config.yml](./monitoring/prometheus-config.yml) change the IPs of the Micrometer exporter (where the server instances are running) to your own IPs
   (on single node scaling ```docker.host.internal``` will also work).
4. In [Dockerfile](Dockerfile) set the Xmx (maximum RAM) and Xms (minimum, initial RAM) values you want to use the server with.

### Start scaling

Run the ``init.sh`` file.

It starts docker containers for a PostgreSQL and MongoDB.<br>
And afterwards pgAdmin and Mongo-Express for database administration.

Then it initializes Docker Swarm and scales the __sine-wave__ backend in four Docker containers.

Afterwards a Nginx container is started to load balance the backends.

Then it starts a Vegeta container in which the user may execute commands to load test the backends.

In the end it starts a three monitoring containers (Prometheus, Grafana, cAdvisor) in which the user may monitor the backends.

|Service|Address|
|:---|:---|
|pgAdmin |[http://localhost:5050](http://localhost:5050)|
|Mongo-Express |[http://localhost:8081](http://localhost:8081)|
|sine-wave backends |[http://localhost:8070](http://localhost:8070) until 8073|
|Load balanced Nginx proxy |[http://localhost:8080](http://localhost:8080)|

For PostgreSQL requests use: [http://localhost:8080/postgres/sine](http://localhost:8080/postgres/sine) <br>
For MongoDB requests use: [http://localhost:8080/mongo/sine](http://localhost:8080/mongo/sine)

|Method|Info|
|:---|:---|
|GET |Gets all entries.|
|POST |Needs JSON body ``{ x: <float> }``.<br>Inserts one sine value.&ast;|
|DELETE |Deletes all entries.|

### Execute a Vegeta command

Run ``docker exec -it vegeta /bin/sh`` to start the vegeta console.

__Example__ to send 249 successful and one error request (Error is with body ``{ x: null }``). This inserts 249 sine pairs in the MongoDB:

__IMPORTANT__ Set your Nginx IP for $IP$!

```bash
jq -ncM 'while(true; .+1) | {method: "POST", url: "http://$IP$:8080/mongo/sine", body: {x: .} | @base64, header: {"Content-Type": ["application/json"]}}' | \
  vegeta attack -rate=50/s -lazy -format=json -duration=5s | \
  tee results.bin | \
  vegeta report
```

See more at [Vegeta Github](https://github.com/tsenart/vegeta).

### Monitor Docker container

Prometheus is used to save properties of software metrics in a time series database.<br>
These metrics are scraped from exporter by Prometheus.<br>
cAdvisor is such an exporter and exports e.g. Docker container CPU, RAM and network traffic.<br>
Additionally the Ktor backends expose backend metrics such as received REST requests.<br>
An optional windows_exporter is also added to monitor system properties.

When visiting [Grafana](http://localhost:3000) login with user ``admin`` und password ``123``.

At the [Dashboards](http://localhost:3000/dashboards) site the user can select one of three dashboards which correspond to the above mentioned exporters.

|Function|Address|
|:---|:---|
|Dashboards | [http://localhost:3000/dashboards](http://localhost:3000/dashboards)|
|Docker monitoring dashboard |[http://localhost:3000/d/DfBn50vnk/docker-monitoring?orgId=1&refresh=10s](http://localhost:3000/d/DfBn50vnk/docker-monitoring?orgId=1&refresh=10s)|
|Ktor Monitoring dashboard |[http://localhost:3000/d/omBsPz1Wz/ktor-monitoring?orgId=1&refresh=10s](http://localhost:3000/d/omBsPz1Wz/ktor-monitoring?orgId=1&refresh=10s)|
|Windows Exporter dashboard |[http://localhost:3000/d/7UlnHoGZz/windows-exporter?orgId=1&refresh=10s](http://localhost:3000/d/7UlnHoGZz/windows-exporter?orgId=1&refresh=10s)|
|Prometheus targets |[http://localhost:9090/targets](http://localhost:9090/targets)|
|windows_exporter metrics |[http://localhost:9182/metrics](http://localhost:9182/metrics)|
|cAdvisor metrics |[http://localhost:8060/metrics](http://localhost:8060/metrics) and http://cAdvisor:8080/metrics in Docker|
|sine-wave metrics |[http://localhost:8070/metrics](http://localhost:8070/metrics) until 8073|

### Change scaled instances at runtime

Follow the instructions in the ``change.sh`` and then run it.

The Ktor backends may take a little time to calm down and start running,
so it is no coincidence when the CPU is first a little higher in the cAdvisor dashboard.

### Stop and prune everything that is running

Run the ``kill.sh`` file.

For stopping Docker swarm mode see the details in ``kill.sh``.
