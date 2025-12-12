package com.dheam.saveCoords.utils;

import com.dheam.saveCoords.data.CoordStorage;

import java.util.UUID;

public class NameGenerator {

    private final CoordStorage storage;

    public NameGenerator(CoordStorage storage) {
        this.storage = storage;
    }

    public String generateName(UUID uuid, int startingIndex) {
        int index = startingIndex;
        String base;
        do {
            base = "coord_" + index;
            index++;
        } while (storage.existsName(uuid, base));
        return base;
    }
}
