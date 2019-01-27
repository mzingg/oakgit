package com.diconium.oakgit.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Container {

	@NonNull
	@Getter
	private final String name;

	private final Map<String, ContainerEntry> entries = new HashMap<>();

	public Container setEntry(@NonNull ContainerEntry entry) {
		entries.put(entry.getID(), entry);
		return this;
	}

}
