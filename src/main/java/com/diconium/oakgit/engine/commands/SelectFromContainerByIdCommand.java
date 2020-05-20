package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class SelectFromContainerByIdCommand implements Command {

	private String containerName = "";

	private String id = "";
    private List<String> resultFieldList = Collections.emptyList();

    public <T extends ContainerEntry<T>> CommandResult buildResult(@NonNull T foundEntry) {
		return new SingleEntityCommandResult<T>(this, foundEntry);
	}

}
