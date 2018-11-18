package com.diconium.oak.jdbc.operation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SqlCommand {

    @NonNull
    @Getter
    private final String command;

    public Optional<String[]> matches(Pattern pattern) {
        Matcher matcher = pattern.matcher(getCommand());
        List<String> result = new ArrayList<>();
        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                result.add(matcher.group(i));
            }
        }

        return result.isEmpty() ? Optional.empty() : Optional.of(result.toArray(new String[0]));
    }

}
