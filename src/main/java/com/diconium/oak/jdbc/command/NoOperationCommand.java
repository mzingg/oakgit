package com.diconium.oak.jdbc.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class NoOperationCommand implements Command {

    private String containerName = StringUtils.EMPTY;


}
