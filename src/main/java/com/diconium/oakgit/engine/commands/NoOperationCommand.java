package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;


/**
 * This Class {@link NoOperationCommand} get called if there is no SQL Command.
 *
 */

@Getter
@Setter
@RequiredArgsConstructor
public class NoOperationCommand implements Command {

    @NonNull
    private final String sqlCommand;

    @NonNull
    final
    private Map<Integer, Object> placeholderData;

    @Override
    public String toString() {
        return "------- NoOperationCommand{" +
                "sqlCommand='" + sqlCommand + '\'' +
                ", placeholderData=" + placeholderData +
                '}';
    }
}
