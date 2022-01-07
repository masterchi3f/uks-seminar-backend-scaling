FROM golang:1.17 as builder

WORKDIR /app
COPY ./ /app

RUN go build -tags netgo -o dockerVolumeExporter

FROM busybox:1.33

COPY --from=builder /app/dockerVolumeExporter /bin/dockerVolumeExporter

EXPOSE 2112

ENTRYPOINT ["dockerVolumeExporter"]
