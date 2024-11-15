//
// Created by janez on 11.11.2024.
//

#ifndef MSG_H
#define MSG_H
#include <string>

#include "MessageType.h"
#include "Task.h"
#include "JsonHelper.h"

class Msg {
public:
    std::string id;
    MessageType type;
    Task task;
    std::string error;

    Msg(const std::string &id, MessageType type, const Task &task, const std::string &error)
        : id(id), type(type), task(task), error(error) {
    }

    std::string toString() const {
        return "{id:" + id
               + ",type:" + ::toString(type)
               + ",task:" + task.toString()
               + ",error:" + error + "}";
    }

    static Msg fromString(const std::string &str) {
        auto fields = split_on_colon(split_on_commas(str));

        return Msg(fields["id"],
                   ::typeFromString(fields["type"]),
                   Task::fromString(fields["task"]),
                   fields["error"]);
    }
};

#endif //MSG_H
