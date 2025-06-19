package org.example;

import java.util.Random;

public class NameGenerator {

    // --- Word Lists ---
    // You can expand these lists considerably for more variety.
    private static final String[] ADJECTIVES = {
        "accurate", "adaptable", "adventurous", "agile", "alert", "ambitious",
        "analytical", "articulate", "awesome", "bold", "brave", "bright",
        "calm", "capable", "charming", "clever", "confident", "conscious",
        "creative", "curious", "dazzling", "dedicated", "determined", "digital",
        "dynamic", "eager", "efficient", "elegant", "energetic", "excellent",
        "fantastic", "fearless", "flexible", "focused", "friendly", "futuristic",
        "generous", "gentle", "gleaming", "graceful", "great", "harmonious",
        "innovative", "insightful", "inspired", "intelligent", "intuitive",
        "jovial", "keen", "kind", "lively", "logical", "loyal", "luminous",
        "magic", "magnetic", "marvelous", "modern", "mystical", "neat",
        "noble", "optimistic", "organized", "patient", "peaceful", "perfect",
        "playful", "pleasant", "positive", "powerful", "precise", "profound",
        "prominent", "radiant", "reliable", "resilient", "resourceful", "robust",
        "savvy", "sensitive", "sharp", "shining", "sincere", "smart",
        "smooth", "sparkling", "spectacular", "speedy", "spirited", "splendid",
        "steadfast", "stellar", "strategic", "stunning", "superb", "swift",
        "talented", "technical", "thoughtful", "thriving", "tidy", "tranquil",
        "transparent", "true", "trusty", "ultimate", "unique", "united",
        "upbeat", "valiant", "vibrant", "victorious", "vigilant", "virtuous",
        "vivid", "wise", "witty", "wonderful", "zesty"
    };

    private static final String[] NOUNS = {
        "algorithm", "anchor", "artifact", "beacon", "blueprint", "bridge",
        "byte", "catalyst", "chamber", "cipher", "circuit", "cloud",
        "cluster", "comet", "compass", "console", "core", "cosmos",
        "crystal", "cube", "cursor", "data", "delta", "dimension",
        "dragon", "echo", "eclipse", "element", "engine", "entity",
        "essence", "ether", "factor", "field", "filter", "fingerprint",
        "flame", "flash", "flow", "forge", "fragment", "galaxy",
        "genesis", "glacier", "horizon", "hub", "impulse", "index",
        "infra", "input", "junction", "key", "knight", "lab",
        "laser", "legend", "light", "logic", "matrix", "memory",
        "mercury", "method", "mirror", "module", "mosaic", "nexus",
        "node", "oasis", "orb", "origin", "output", "paradigm",
        "path", "phantom", "phoenix", "pillar", "pilot", "pioneer",
        "portal", "prism", "probe", "puzzle", "quantum", "radar",
        "radius", "reactor", "relay", "relic", "research", "reserve",
        "robot", "saga", "scanner", "schema", "scope", "sector",
        "sequence", "signal", "solution", "source", "spark", "spectrum",
        "sphere", "spiral", "star", "station", "stream", "structure",
        "synth", "system", "target", "template", "terminal", "thread",
        "threshold", "titan", "token", "tower", "trace", "trail",
        "transit", "transmission", "traveler", "trigger", "unity", "universe",
        "vector", "vertex", "vortex", "wave", "zenith", "zero"
    };

    // --- Alphanumeric Character Pool ---
    // Using lowercase letters and digits 0-9, matching the example format.
    private static final String ALPHANUMERIC_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    // --- Random String Length ---
    private static final int DEFAULT_RANDOM_STRING_LENGTH = 16; // Like in your example

    // --- Random Number Generator ---
    private final Random random = new Random(); // Reuse the Random instance

    /**
     * Selects a random element from a given String array.
     * @param array The array to select from.
     * @return A random String from the array.
     */
    private String selectRandom(String[] array) {
        if (array == null || array.length == 0) {
            return ""; // Or throw an exception, depending on desired behavior
        }
        int index = random.nextInt(array.length);
        return array[index];
    }

    /**
     * Generates a random alphanumeric string of a specified length.
     * Characters are selected from the ALPHANUMERIC_CHARS pool.
     * @param length The desired length of the random string.
     * @return A randomly generated alphanumeric string.
     */
    private String generateRandomString(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC_CHARS.length());
            sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * Generates a name in the format: [adjective]-[noun]-[random_alphanumeric_string].
     * Uses default random string length (DEFAULT_RANDOM_STRING_LENGTH).
     * @return A generated name string.
     */
    public String generateName() {
        return generateName(DEFAULT_RANDOM_STRING_LENGTH);
    }

    /**
     * Generates a name in the format: [adjective]-[noun]-[random_alphanumeric_string].
     * @param randomStringLength The desired length for the random alphanumeric part.
     * @return A generated name string.
     */
    public String generateName(int randomStringLength) {
        String adjective = selectRandom(ADJECTIVES);
        String noun = selectRandom(NOUNS);
        String randomPart = generateRandomString(randomStringLength);

        return adjective + "-" + noun + "-" + randomPart;
    }
}
