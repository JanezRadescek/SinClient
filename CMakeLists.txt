cmake_minimum_required(VERSION 3.28)
project(SinClient)

set(CMAKE_CXX_STANDARD 17)

add_executable(SinClient
    src/main/cpp/main.cpp
        src/main/cpp/sin/SinService.h
        src/main/cpp/sin/SinService.cpp
        src/main/cpp/dtos/MessageType.h
        src/main/cpp/dtos/JsonHelper.h
        src/main/cpp/dtos/Msg.h
        src/main/cpp/sin/SocketClient.cpp)
