FROM ubuntu:latest

WORKDIR /app

RUN apt-get update && \
    apt-get install -y cmake libboost-all-dev libasio-dev build-essential libwebsocketpp-dev

COPY . /app

RUN mkdir -p build && \
    cmake -S . -B build && \
    cmake --build build

ENTRYPOINT ["./build/SinClient"]
CMD ["ws://localhost:8080/ws/sin"]