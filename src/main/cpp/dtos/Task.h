//
// Created by janez on 11.11.2024.
//

#ifndef TASK_H
#define TASK_H
#include <cstdint>

#include "JsonHelper.h"

class Task {
public:
    double input;
    double output;
    double term;
    int step;
    int required_steps;

    Task(double input, double output, double term, int step, int required_steps) : input(input), output(output),
        term(term), step(step), required_steps(required_steps) {
    }

    std::string toString() const {
        return "{input:" + std::to_string(input)
               + ",output:" + std::to_string(output)
               + ",term:" + std::to_string(term)
               + ",step:" + std::to_string(step)
               + ",required_steps:" + std::to_string(required_steps) + "}";
    }

    static Task fromString(const std::string &str) {
        auto fields = split_on_colon(split_on_commas(str));
        return Task(std::stod(fields["input"]),
                    std::stod(fields["output"]),
                    std::stod(fields["term"]),
                    std::stoi(fields["step"]),
                    std::stoi(fields["required_steps"]));
    }
};

#endif //TASK_H
