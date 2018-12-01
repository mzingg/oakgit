package com.diconium.oak.jdbc.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Getter
@Setter
public class InsertIntoTableCommand implements Command {

    private String containerName = StringUtils.EMPTY;


}
