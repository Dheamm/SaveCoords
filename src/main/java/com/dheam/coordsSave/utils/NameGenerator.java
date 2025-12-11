package com.dheam.coordsSave.utils;

import com.dheam.coordsSave.data.CoordStorage;

import java.util.UUID;

public class NameGenerator {

    private final CoordStorage storage;

    public NameGenerator(CoordStorage storage) {
        this.storage = storage;
    }

    public String generateName(UUID uuid) {
        int index = storage.countCoords(uuid) + 1;
        String base;
        do {
            base = "coord_" + index;
            index++;
        } while (storage.existsName(uuid, base));
        return base;
    }
}
