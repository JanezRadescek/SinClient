#include <iostream>
#include <string>
#include "sin/SinService.h"

int main(int argc, char* argv[])
{
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " <uri>" << std::endl;
        return 1;
    }

    std::string uri = argv[1];
    std::cout << "Starting WebSocket client with URI: " << uri << std::endl;
    run_client(uri);
    return 0;
}