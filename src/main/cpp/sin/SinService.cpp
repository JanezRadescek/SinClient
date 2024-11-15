//
// Created by janez on 15.11.2024.
//

#include <iostream>
#include <optional>
#include <boost/uuid/uuid_generators.hpp>
#include <boost/uuid/uuid_io.hpp>

#include "SocketClient.h"

std::string client_id = "";

void get_id(SocketClient &client) {
    while (true) {
        std::optional<Msg> msg = client.getMsg();
        if (msg) {
            if (msg->type == MessageType::ID) {
                client_id = msg->id;
                return;
            }
        } else {
            std::this_thread::sleep_for(std::chrono::milliseconds(100));
        }
    }
}

void get_active(SocketClient &client) {
    std::this_thread::sleep_for(std::chrono::milliseconds(100));
}

Msg get_new_msg() {
    double x = static_cast<double>(std::rand()) / RAND_MAX * 10;
    int n = std::rand() % 20 + 5;

    auto id = client_id + "::" + to_string(boost::uuids::random_generator()());
    return Msg(id, MessageType::NEW_TASK, Task(x, 0, 0, 0, n), "");
}

void wait_for_result(SocketClient & client, const std::string & id) {
    while (true) {
        std::optional<Msg> msg = client.getMsg();
        if (msg) {
            if (msg->id == id) {
                if (msg->type == MessageType::RESULT) {
                    // sin(x, N) = result
                    std::cout << "sin(" << msg->task.input << ", " << msg->task.required_steps << ") = " << msg->task.output << std::endl;
                    return;
                }
                if (msg->type == MessageType::ERROR) {
                    std::cerr << "Error: " << msg->error << std::endl;
                    return;
                }
            }
        }
    }
}

void calculate_sin(SocketClient &client) {
    Msg msg = get_new_msg();
    client.send(msg);
    wait_for_result(client, msg.id);
}

void run_client(const std::string &uri) {
    std::srand(std::time(nullptr));

    SocketClient client(uri);

    get_id(client);

    get_active(client);

    while (true) {
        calculate_sin(client);
    }
}
