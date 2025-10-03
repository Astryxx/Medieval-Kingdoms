package com.Astryxx.medievalkingdoms.world;

import java.util.List;
import java.util.Random;

/**
 * Utility class to generate random medieval kingdom names.
 */
public class KingdomNameGenerator {

    private static final List<String> KINGDOM_NAMES = List.of(
            "Aethelred",
            "Mercia",
            "Dunwich",
            "Aquitaine",
            "Novgorod",
            "Rhodos",
            "Belfast",
            "Ashford",
            "Caerleon",
            "Windsmoor"
    );

    private static final List<String> KINGDOM_TITLES = List.of(
            "Kingdom of ",
            "Duchy of ",
            "Lordship of ",
            "Barony of "
    );

    private static final Random RANDOM = new Random();

    public static String generateRandomName() {
        String title = KINGDOM_TITLES.get(RANDOM.nextInt(KINGDOM_TITLES.size()));
        String name = KINGDOM_NAMES.get(RANDOM.nextInt(KINGDOM_NAMES.size()));
        return title + name;
    }

    public static String pickRandomColor() {
        // Simple list of colors for Banners/Accents
        List<String> colors = List.of("Red", "Blue", "Gold", "Green", "White", "Black");
        return colors.get(RANDOM.nextInt(colors.size()));
    }
}