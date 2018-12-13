package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
// TODO: Find a better name for this command - what is its meaning?
// TODO: Add Javadoc documentation describing the function of this command
public class GetContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

}
