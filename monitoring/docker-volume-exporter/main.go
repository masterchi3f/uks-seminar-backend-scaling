package main

import (
	"context"
	"flag"
	"fmt"
	"github.com/docker/docker/client"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"log"
	"net/http"
	"os"
	"os/signal"
	"sync"
	"syscall"
	"time"
)

type ApplicationSettings struct {
	exporter  ExporterSettings
	collector CollectorSettings // Settings for the metric collector
}

// ExporterSettings collects all relevant configuration parameters to tweak the internal behaviour
// of the complete exporter application.
type ExporterSettings struct {
	port        int    // The port on which to start the HTTP server
	metricsPath string // The HTTP path on which to expose the metrics
}

// CollectorSettings is used to configure the metric collector's behaviour.
type CollectorSettings struct {
	interval   time.Duration // The interval for collecting volume information from docker
	metricName string        // Name of the metric used for exporting
}

// exitOnOsSignals listens for SIGTERM and SIGINT and cancels the context when signal received.
func exitOnOsSignals(cancelCtx context.CancelFunc) {
	sigs := make(chan os.Signal, 1)
	signal.Notify(sigs, syscall.SIGINT, syscall.SIGTERM)
	go func() {
		_ = <-sigs
		cancelCtx()
	}()
}

// getSettings uses Go's Flag module to allow for simple customizations of the exporter. Applies sensitive defaults
// such that supplying configuration is not a must.
func getSettings() ApplicationSettings {
	port := flag.Int("port", 2112, "the port on which the http server listens")
	interval := flag.Int("interval", 5, "the metric collect interval in seconds")
	metricName := flag.String("metricname", "dock_volume_metric", "name of the exported metric")

	flag.Parse()

	settings := ApplicationSettings{
		exporter: ExporterSettings{
			port:        *port,
			metricsPath: "/metrics",
		},
		collector: CollectorSettings{
			interval:   time.Duration(*interval) * time.Second,
			metricName: *metricName,
		},
	}

	return settings
}

// getDockerDiskUsage requests the disk usage from the docker engine and maps the usage in bytes
// to every individual volume found.
func getDockerDiskUsage(cli *client.Client, ctx context.Context) map[string]int64 {
	info, err := cli.DiskUsage(ctx)
	if err != nil {
		log.Fatalln(err)
	}
	usage := make(map[string]int64)
	for _, v := range info.Volumes {
		usage[v.Name] = v.UsageData.Size
	}
	return usage
}

// startCollectingDiskUsage collects the disk usage on the specified interval and exposes them to
// prometheus using a GaugeVector with the label volume_name. Every volume is exposed by using
// the volume name for the label.
func startCollectingDiskUsage(cli *client.Client, ctx context.Context, wg *sync.WaitGroup, settings CollectorSettings) {
	ticker := time.NewTicker(settings.interval)
	metric := promauto.NewGaugeVec(prometheus.GaugeOpts{
		Name: settings.metricName},
		[]string{"volume_name"})

	// the actual goroutine which collects metrics periodically and runs as long as the context is not cancelled
	go func() {
		defer wg.Done()
		for {
			select {
			case <-ctx.Done():
				return
			case <-ticker.C:
				usage := getDockerDiskUsage(cli, ctx)
				for volumeName, volumeSize := range usage {
					metric.WithLabelValues(volumeName).Set(float64(volumeSize))
				}
			}
		}
	}()
}

// startMetricsExporter starts an HTTP server and exposes the collected metrics as long as the server
// is not shutdown.
func startMetricsExporter(wg *sync.WaitGroup, settings ExporterSettings) *http.Server {
	srv := &http.Server{Addr: fmt.Sprintf(":%d", settings.port)}
	http.Handle(settings.metricsPath, promhttp.Handler())

	go func() {
		defer wg.Done()
		if err := srv.ListenAndServe(); err != http.ErrServerClosed {
			log.Fatalf("ListenAndServer(): %v", err)
		}
	}()

	return srv
}

// startCollectingAndExport runs the app logic by first starting the HTTP exported and then collecting
// the volume metrics.
func startCollectingAndExport(ctx context.Context, wg *sync.WaitGroup, settings ApplicationSettings) *http.Server {
	cli, err := client.NewClientWithOpts(client.FromEnv)
	if err != nil {
		log.Fatalf("cannot create docker client: %s", err.Error())
	}

	wg.Add(1)
	srv := startMetricsExporter(wg, settings.exporter)
	log.Printf("Exposing docker volume metrics on :%d%s", settings.exporter.port, settings.exporter.metricsPath)

	wg.Add(1)
	startCollectingDiskUsage(cli, ctx, wg, settings.collector)
	log.Printf("Started collecting volume metrics every %ds", settings.collector.interval/time.Second)

	return srv
}

func main() {
	rootCtx := context.Background()
	mainCtx, cancelMainCtx := context.WithCancel(rootCtx)
	waitGroup := &sync.WaitGroup{}

	// register OS signals as early as possible to allow for fast shutdown in any situation
	exitOnOsSignals(cancelMainCtx)

	settings := getSettings()
	srv := startCollectingAndExport(mainCtx, waitGroup, settings)

	// waiting here until an OS signal cancels the context
	_ = <-mainCtx.Done()

	log.Println("Shutdown initiated")

	if err := srv.Shutdown(context.TODO()); err != nil {
		panic(err)
	}
	// wait until all goroutines have signalled completion
	waitGroup.Wait()
}
