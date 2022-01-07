# Docker Volume Exporter

This is a Go based prometheus metrics exporter that collects and exports docker volume sizes. 
Those metrics are exported as Gauge `docker_volume_metric` with label `volume_name` set
to the corresponding docker volume. The metrics are collected and updated every 5 seconds.

The metrics are available for scraping at `http://localhost:2112/metrics`. You should see output
as follows:

```
# HELP docker_volume_metric 
# TYPE docker_volume_metric gauge
docker_volume_metric{volume_name="name"} 9.5906937e+07
```

You can use this exporter with Docker but mind that this exporter requires access to the Docker
Engine to gather information about volume sizes. Thus, you need to mount the Docker Socket
inside the container. For example, you might use the following commands to run a containerized exporter:

```shell
$ docker build -t docker-volume-exporter:latest .
$ docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -p "2112:2112" docker-volume-exporter:latest
```

## Customizing

The exported has a simple command line interface which allows for basic customization:

````shell
$ docker-volume-exporter -help
Usage of docker-volume-exporter:
  -interval int
        the metric collect interval in seconds (default 5)
  -metricname string
        name of the exported metric (default "dock_volume_metric")
  -port int
        the port on which the http server listens (default 2112)
````

Using docker, you can apply the configuration via the run command, e.g:

```shell
$ docker run --rm -v /var/run/docker.sock:/var/run/docker.sock docker-volume-exporter:latest -interval 10 -port 1111
```

## Building locally

This is a standard Go project and all standard Go tooling applies:

* `docker build main.go` to create an executable
* `docker run main.go` to run the collector immediately
