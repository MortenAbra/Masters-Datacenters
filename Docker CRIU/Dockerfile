FROM ubuntu:18.04

WORKDIR /app

RUN apt-get update && apt-get upgrade -y && apt install python3-pip -y

RUN apt-get update && apt-get install -y \
    libbsd0 \
    libc6 \
    libgnutls30 \
    libnet1 \
    libnl-3-200 \
    libprotobuf-c1 \
    python3 \
    python3-protobuf \
    iproute2 \
    pkg-config \
    nftables \
    nftables \
    libbsd-dev \
    nftables \
    git \
    && rm -rf /var/lib/apt/lists/*

RUN apt search manpages

COPY . /app

RUN chmod +x criu_install.sh

RUN sh criu_install.sh

#RUN tar -C /app/criu -xvf criu_tar.tar

CMD ["app.py"]

ENTRYPOINT [ "python3" ]