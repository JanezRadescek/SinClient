# Use an official Ubuntu as a parent image
FROM ubuntu:latest

# Set the working directory
WORKDIR /app

# Install dependencies
RUN apt-get update && \
    apt-get install -y cmake libboost-all-dev libasio-dev build-essential libwebsocketpp-dev

# Copy the current directory contents into the container at /app
COPY . /app

# Create the build directory and run CMake and make
RUN mkdir -p build && \
    cmake -S . -B build && \
    cmake --build build

# Run the application
CMD ["./build/SinClient"]