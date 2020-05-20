package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;

import java.util.List;

public interface MultipleEntitiesCommandResultProvider {

    String getContainerName();

    <T extends ContainerEntry<T>> CommandResult buildResult(List<T> foundEntries);

}
