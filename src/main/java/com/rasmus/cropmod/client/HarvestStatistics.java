package com.rasmus.cropmod.client;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Tracks harvest statistics for the current session.
 */
public class HarvestStatistics {
    private static final HarvestStatistics INSTANCE = new HarvestStatistics();

    // Total counts per crop type for the session
    private final Map<Block, Integer> sessionCounts = new HashMap<>();
    private final Map<Block, Integer> unmodifiableSessionCounts = Collections.unmodifiableMap(sessionCounts);
    private int totalSessionCount = 0;

    // Timestamps of recent harvests for per-hour calculation (rolling window)
    private final LinkedList<Long> recentHarvests = new LinkedList<>();
    private final Map<Block, LinkedList<Long>> recentHarvestsByCrop = new HashMap<>();

    // Session start time - null until first harvest
    private Long sessionStartTime = null;

    // One hour in milliseconds
    private static final long ONE_HOUR_MS = 60 * 60 * 1000;

    /**
     * The one list of tracked crops; the break tracker and the HUD icon
     * table derive from it, so adding a crop is one edit, not three
     * (three separate lists used to drift silently).
     */
    public static final Block[] TRACKED_CROPS = {
            Blocks.WHEAT,
            Blocks.CARROTS,
            Blocks.POTATOES,
            Blocks.BEETROOTS,
            Blocks.NETHER_WART,
            Blocks.COCOA
    };

    private HarvestStatistics() {
    }

    /** Monotonic milliseconds: rate windows must not follow wall-clock steps. */
    private static long monotonicMs() {
        return System.nanoTime() / 1_000_000L;
    }

    public static HarvestStatistics getInstance() {
        return INSTANCE;
    }

    /**
     * Record a successful harvest of a crop.
     */
    public void recordHarvest(Block cropBlock) {
        long now = monotonicMs();

        // Start session timer on first harvest
        if (sessionStartTime == null) {
            sessionStartTime = now;
        }

        // Update session count
        sessionCounts.merge(cropBlock, 1, (oldVal, newVal) -> oldVal + newVal);
        totalSessionCount++;

        // Add to recent harvests for per-hour tracking
        recentHarvests.add(now);

        // Add to crop-specific list; created lazily so an added crop
        // degrades to "works" instead of "session count without a rate"
        recentHarvestsByCrop.computeIfAbsent(cropBlock, b -> new LinkedList<>()).add(now);

        // Cleanup old entries (older than 1 hour)
        cleanupOldEntries(now);
    }

    /**
     * Remove harvest timestamps older than 1 hour.
     */
    private void cleanupOldEntries(long now) {
        cleanupRecentList(recentHarvests, now);
        for (LinkedList<Long> list : recentHarvestsByCrop.values()) {
            cleanupRecentList(list, now);
        }
    }

    private void cleanupRecentList(LinkedList<Long> list, long now) {
        long cutoff = now - ONE_HOUR_MS;
        while (!list.isEmpty() && list.peek() < cutoff) {
            list.poll();
        }
    }

    /**
     * Get total crops harvested this session.
     */
    public int getTotalSessionCount() {
        return totalSessionCount;
    }

    /**
     * Get session count for a specific crop.
     */
    public int getSessionCount(Block cropBlock) {
        return sessionCounts.getOrDefault(cropBlock, 0);
    }

    /**
     * Get crops harvested in the last hour.
     */
    public int getHarvestsPerHour() {
        cleanupRecentList(recentHarvests, monotonicMs());
        return recentHarvests.size();
    }

    /**
     * Get harvests per hour for a specific crop.
     */
    public int getHarvestsPerHour(Block cropBlock) {
        LinkedList<Long> cropList = recentHarvestsByCrop.get(cropBlock);
        if (cropList == null) {
            return 0;
        }
        cleanupRecentList(cropList, monotonicMs());
        return cropList.size();
    }

    /**
     * Get harvests per minute (based on actual session time).
     * Rate is capped at actual count harvested.
     */
    public int getHarvestsPerMinute() {
        if (sessionStartTime == null)
            return 0;

        long elapsedMs = monotonicMs() - sessionStartTime;
        int total = getTotalSessionCount();
        if (total == 0 || elapsedMs < 1000)
            return 0;

        // Calculate rate: (count / elapsed_minutes)
        double minutes = elapsedMs / 60000.0;
        int rate = (int) Math.round(total / minutes);

        return rate;
    }

    /**
     * Get harvests per minute for a specific crop.
     * Rate is capped at actual count harvested.
     */
    public int getHarvestsPerMinute(Block cropBlock) {
        if (sessionStartTime == null)
            return 0;

        long elapsedMs = monotonicMs() - sessionStartTime;
        int count = getSessionCount(cropBlock);
        if (count == 0 || elapsedMs < 1000)
            return 0;

        double minutes = elapsedMs / 60000.0;
        int rate = (int) Math.round(count / minutes);

        return rate;
    }

    /**
     * Get session duration in minutes.
     */
    public long getSessionDurationMinutes() {
        if (sessionStartTime == null)
            return 0;
        return (monotonicMs() - sessionStartTime) / (60 * 1000);
    }

    /**
     * Reset all statistics.
     */
    public void reset() {
        sessionCounts.clear();
        totalSessionCount = 0;
        recentHarvests.clear();
        for (LinkedList<Long> list : recentHarvestsByCrop.values()) {
            list.clear();
        }
        sessionStartTime = monotonicMs();
    }

    /**
     * Get map of session counts by crop for detailed display.
     */
    public Map<Block, Integer> getSessionCountsByCrop() {
        return unmodifiableSessionCounts;
    }

    /**
     * Get a friendly name for a crop block.
     */
    public static String getCropName(Block block) {
        if (block == Blocks.WHEAT)
            return "Wheat";
        if (block == Blocks.CARROTS)
            return "Carrots";
        if (block == Blocks.POTATOES)
            return "Potatoes";
        if (block == Blocks.BEETROOTS)
            return "Beetroots";
        if (block == Blocks.NETHER_WART)
            return "Nether Wart";
        if (block == Blocks.COCOA)
            return "Cocoa";
        if (block == Blocks.PUMPKIN)
            return "Pumpkin";
        if (block == Blocks.MELON)
            return "Melon";
        if (block == Blocks.SWEET_BERRY_BUSH)
            return "Sweet Berries";
        if (block == Blocks.SUGAR_CANE)
            return "Sugar Cane";
        if (block == Blocks.BAMBOO)
            return "Bamboo";
        if (block == Blocks.KELP || block == Blocks.KELP_PLANT)
            return "Kelp";
        if (block == Blocks.CACTUS)
            return "Cactus";
        if (block == Blocks.CHORUS_PLANT || block == Blocks.CHORUS_FLOWER)
            return "Chorus";
        if (block == Blocks.CAVE_VINES || block == Blocks.CAVE_VINES_PLANT)
            return "Glow Berries";
        if (block == Blocks.TORCHFLOWER || block == Blocks.TORCHFLOWER_CROP)
            return "Torchflower";
        if (block == Blocks.PITCHER_PLANT || block == Blocks.PITCHER_CROP)
            return "Pitcher Plant";
        return "Unknown";
    }
}
