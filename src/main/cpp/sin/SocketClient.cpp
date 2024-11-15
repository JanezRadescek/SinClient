#include "SocketClient.h"
#include <websocketpp/client.hpp>
#include <websocketpp/config/asio_no_tls_client.hpp>
#include <iostream>
#include <optional>

SocketClient::SocketClient(const std::string &uri) : uri(uri) {
    client.init_asio();
    client.clear_access_channels(websocketpp::log::alevel::frame_header);
    client.clear_access_channels(websocketpp::log::alevel::frame_payload);
    connect();
}

void SocketClient::connect() {
    websocketpp::lib::error_code ec;

    // Create connection
    auto con = client.get_connection(uri, ec);
    if (ec) {
        throw std::runtime_error("Could not create connection, because: " + ec.message());
    }

    con->set_message_handler(bind(&SocketClient::onMessage, this, std::placeholders::_1, std::placeholders::_2));

    // Set handlers for connection lifecycle events
    con->set_open_handler([this](websocketpp::connection_hdl hdl) {
        client_hdl = hdl;
        std::cout << "Connection opened successfully." << std::endl;
    });

    con->set_fail_handler([this](websocketpp::connection_hdl hdl) {
        std::cerr << "Connection failed." << std::endl;
    });

    con->set_close_handler([this](websocketpp::connection_hdl hdl) {
        std::cout << "Connection closed." << std::endl;
    });

    // Start the connection
    client.connect(con);

    // Run the ASIO event loop in a separate thread
    io_thread = std::thread([this]() {
        try {
            client.run();
        } catch (const std::exception &e) {
            std::cerr << "ASIO event loop error: " << e.what() << std::endl;
        }
    });
}

void SocketClient::onMessage(websocketpp::connection_hdl hdl, websocketpp::client<websocketpp::config::asio_client>::message_ptr msg) {
    std::lock_guard lock(queueMutex);
    try {
        std::cout << "Received message: " << msg->get_payload() << std::endl;

        // Parse message and push to queue
        Msg message = Msg::fromString(msg->get_payload());
        messageQueue.push(message);

        // Notify waiting threads
        queueCondVar.notify_one();
    } catch (const std::exception &e) {
        std::cerr << "Error parsing message: " << e.what() << std::endl;
    }
}

std::optional<Msg> SocketClient::getMsg() {
    std::unique_lock lock(queueMutex);

    // Wait for a message to arrive
    queueCondVar.wait(lock, [this] { return !messageQueue.empty(); });

    // Retrieve the message from the queue
    Msg msg = messageQueue.front();
    messageQueue.pop();
    return msg;
}

void SocketClient::send(const Msg& msg) {
    std::string message = msg.toString();
    std::cout << "Sending message: " << message << std::endl;

    websocketpp::lib::error_code ec;
    if (!client_hdl.lock()) {
        std::cerr << "Error: Connection handle is invalid or not established." << std::endl;
        return;
    }

    // Send the message using the active connection handle
    client.send(client_hdl, message, websocketpp::frame::opcode::text, ec);
    if (ec) {
        std::cerr << "Error sending message: " << ec.message() << std::endl;
    }
}

SocketClient::~SocketClient() {
    client.stop();
    if (io_thread.joinable()) {
        io_thread.join();
    }
}
