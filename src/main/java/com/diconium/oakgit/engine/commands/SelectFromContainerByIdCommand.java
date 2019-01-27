package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.model.ContainerEntry;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.util.Optional;

@Getter
@Setter
public class SelectFromContainerByIdCommand implements Command {
	
	private String containerName = StringUtils.EMPTY;
	
	private String id = StringUtils.EMPTY;

	public CommandResult buildResult(Optional<ContainerEntry> foundEntry) {
		return new SelectFromContainerByIdCommandResult(this, foundEntry);
	}

	@Override
	public String toString() {
		return "SelectFromContainerByIdCommand{" +
				"containerName='" + containerName + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
