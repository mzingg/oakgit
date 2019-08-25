package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * This class offers a {@link Command} to create a container using the containerName
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CreateContainerCommand extends AbstractCommand<CreateContainerCommand> {

    @NonNull
    private String containerName = StringUtils.EMPTY;

}
