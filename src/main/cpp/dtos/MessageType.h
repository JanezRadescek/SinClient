//
// Created by janez on 11.11.2024.
//

#ifndef MESSAGETYPE_H
#define MESSAGETYPE_H
#include <stdexcept>
#include <string>


enum class MessageType {
    NEW_TASK,
    PARTIAL,
    RESULT,
    CANCEL,
    CANCELED,
    ERROR,
    ID
};


inline std::string toString(MessageType type) {
    switch (type) {
        case MessageType::NEW_TASK:
            return "NEW_TASK";
        case MessageType::PARTIAL:
            return "PARTIAL";
        case MessageType::RESULT:
            return "RESULT";
        case MessageType::CANCEL:
            return "CANCEL";
        case MessageType::CANCELED:
            return "CANCELED";
        case MessageType::ERROR:
            return "ERROR";
        case MessageType::ID:
            return "ID";
        default:
            throw std::invalid_argument("Unknown MessageType");
    }
}

inline MessageType typeFromString(const std::string &str) {
    if (str == "NEW_TASK") {
        return MessageType::NEW_TASK;
    }
    if (str == "PARTIAL") {
        return MessageType::PARTIAL;
    }
    if (str == "RESULT") {
        return MessageType::RESULT;
    }
    if (str == "CANCEL") {
        return MessageType::CANCEL;
    }
    if (str == "CANCELED") {
        return MessageType::CANCELED;
    }
    if (str == "ERROR") {
        return MessageType::ERROR;
    }
    if (str == "ID") {
        return MessageType::ID;
    }
    throw std::invalid_argument("Unknown MessageType string: " + str);
}


#endif //MESSAGETYPE_H
