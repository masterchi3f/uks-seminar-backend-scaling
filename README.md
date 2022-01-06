# Horizontal scaling of a backend service

Run the ``init.sh`` file.

It starts docker containers for a PostgreSQL and MongoDB.<br>
And afterwards pgAdmin and Mongo-Express for database administration.

Then it initializes Docker Swarm and scales the __sine-wave__ backend in four Docker containers.

Afterwards a Nginx container is started to load balance the backends.

In the end it starts a Vegeta container in which the user may execute commands to load test the backends.

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
|GET |Gets all entries|
|POST |Needs JSON body ``{ x: <int> }``.<br>Inserts one sine value.&ast;|
|DELETE |Deletes all entries.|

&ast;PostgreSQL uses the x value as primary key (can't insert two sine values with same x value).

### Execute a Vegeta command

Run ``docker exec -it vegeta /bin/sh`` to start the vegeta console.

__Example__ to send 249 successful and one error request (Error is with body ``{ x: null }``). This inserts 249 sine pairs in the MongoDB:

```bash
jq -ncM 'while(true; .+1) | {method: "POST", url: "http://host.docker.internal:8080/mongo/sine", body: {x: .} | @base64, header: {"Content-Type": ["application/json"]}}' | \
  vegeta attack -rate=50/s -lazy -format=json -duration=5s | \
  tee results.bin | \
  vegeta report
```

