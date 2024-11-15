#ifndef SOCKETCLIENT_H
#define SOCKETCLIENT_H

#include <string>
#include <queue>
#include <mutex>
#include <condition_variable>
#include <optional>
#include <websocketpp/config/asio_no_tls_client.hpp>
#include <websocketpp/client.hpp>
#include "../dtos/Msg.h"

class SocketClient {
public:
    SocketClient(const std::string &uri);
    ~SocketClient();

    std::optional<Msg> getMsg();
    void send(const Msg & msg);

private:
    void onMessage(websocketpp::connection_hdl hdl, websocketpp::client<websocketpp::config::asio_client>::message_ptr msg);
    void connect();

    std::string uri;
    std::queue<Msg> messageQueue;
    std::mutex queueMutex;
    std::condition_variable queueCondVar;
    websocketpp::client<websocketpp::config::asio_client> client;
    websocketpp::connection_hdl client_hdl;
    std::thread io_thread;
};

#endif // SOCKETCLIENT_H