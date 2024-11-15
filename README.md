# Sin Client

## C++

The application can be built using:

```shell script
sudo apt-get update && \
sudo apt-get install -y cmake libboost-all-dev libasio-dev libwebsocketpp-dev && \
mkdir -p build && \
cmake -S . -B build && \
cmake --build build
```

The application is now runnable using:

```shell script
./build/SinClient
```

### Docker

The image can be built using:

```shell script
docker build -t sinclient .
```

To run the image:

```shell script
docker run --network host sinclient
```

## Java

The application can be packaged using:

```shell script
./mvnw package
```

The application is now runnable using:

```shell script
java -jar ./target/quarkus-app/quarkus-run.jar
```
