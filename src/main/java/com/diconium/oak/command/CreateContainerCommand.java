package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class CreateContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

}