FROM alpine:3.4

RUN apk update && apk add \
    curl \
    git \
    vim \
    jq

RUN curl -Lo vegeta.tar.gz "https://github.com/tsenart/vegeta/releases/latest/download/vegeta_12.8.4_linux_amd64.tar.gz" \
    && mkdir vegeta-temp \
    && tar xf vegeta.tar.gz -C vegeta-temp \
    && mv vegeta-temp/vegeta /usr/local/bin \
    && rm -rf vegeta.tar.gz && rm -rf vegeta-temp

# To run container and don't stop it after build
ENTRYPOINT ["tail"]
CMD ["-f", "/dev/null"]
