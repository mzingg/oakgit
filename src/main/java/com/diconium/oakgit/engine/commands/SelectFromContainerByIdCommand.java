package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class SelectFromContainerByIdCommand implements Command {
	
	private String containerName = StringUtils.EMPTY;
	
	private String id = StringUtils.EMPTY;

	public <T extends ContainerEntry<T>> CommandResult buildResult(@NonNull ContainerEntry<T> foundEntry) {
		return new SelectFromContainerByIdCommandResult<T>(this, foundEntry);
	}

	@Override
	public String toString() {
		return "SelectFromContainerByIdCommand{" +
				"containerName='" + containerName + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
