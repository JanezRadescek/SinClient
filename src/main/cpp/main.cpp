//
// Created by janez on 15.11.2024.
//

#include <iostream>

#include "sin/SinService.h"

int main()
{
    std::cout << "Starting WebSocket client..." << std::endl;
    run_client("ws://localhost:8080/ws/sin");
    return 0;
}
