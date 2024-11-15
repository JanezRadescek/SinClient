//
// Created by janez on 14.11.2024.
//

#ifndef JSONHELPER_H
#define JSONHELPER_H


static std::vector<std::string> split_on_commas(const std::string &str) {
    if (str.empty() || str.front() != '{' || str.back() != '}') {
        throw std::invalid_argument("String must start and end with {}");
    }

    std::string modified_str = str.substr(1, str.size() - 2);
    std::vector<std::string> result;
    std::string current;
    int brace_count = 0;

    for (char ch: modified_str) {
        if (ch == '{') {
            brace_count++;
            current += ch;
            continue;
        }
        if (ch == '}') {
            brace_count--;
            current += ch;
            continue;
        }

        if (ch == ',' && brace_count == 0) {
            result.push_back(current);
            current.clear();
        } else {
            current += ch;
        }
    }

    if (!current.empty()) {
        result.push_back(current);
    }

    return result;
}

// Function to split string on colon and put into dictionary
static std::unordered_map<std::string, std::string> split_on_colon(const std::vector<std::string> &vec) {
    std::unordered_map<std::string, std::string> dict;
    for (const auto &item: vec) {
        size_t pos = item.find(':');
        if (pos != std::string::npos) {
            std::string key = item.substr(0, pos);
            std::string value = item.substr(pos + 1);
            dict[key] = value;
        }
    }
    return dict;
}

#endif //JSONHELPER_H
