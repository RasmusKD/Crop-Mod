package com.rasmus.cropmod.client;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

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

    // Timestamps of recent harvests for per-hour calculation (rolling window)
    private final LinkedList<Long> recentHarvests = new LinkedList<>();
    private final Map<Block, LinkedList<Long>> recentHarvestsByCrop = new HashMap<>();

    // Session start time - null until first harvest
    private Long sessionStartTime = null;

    // One hour in milliseconds
    private static final long ONE_HOUR_MS = 60 * 60 * 1000;

    private HarvestStatistics() {
        // Initialize crop-specific lists
        for (Block crop : getSupportedCrops()) {
            recentHarvestsByCrop.put(crop, new LinkedList<>());
        }
    }

    public static HarvestStatistics getInstance() {
        return INSTANCE;
    }

    /**
     * Record a successful harvest of a crop.
     */
    public void recordHarvest(Block cropBlock) {
        long now = System.currentTimeMillis();

        // Start session timer on first harvest
        if (sessionStartTime == null) {
            sessionStartTime = now;
        }

        // Update session count
        sessionCounts.merge(cropBlock, 1, (oldVal, newVal) -> oldVal + newVal);

        // Add to recent harvests for per-hour tracking
        recentHarvests.add(now);

        // Add to crop-specific list
        LinkedList<Long> cropList = recentHarvestsByCrop.get(cropBlock);
        if (cropList != null) {
            cropList.add(now);
        }

        // Cleanup old entries (older than 1 hour)
        cleanupOldEntries(now);
    }

    /**
     * Remove harvest timestamps older than 1 hour.
     */
    private void cleanupOldEntries(long now) {
        long cutoff = now - ONE_HOUR_MS;

        while (!recentHarvests.isEmpty() && recentHarvests.peek() < cutoff) {
            recentHarvests.poll();
        }

        for (LinkedList<Long> list : recentHarvestsByCrop.values()) {
            while (!list.isEmpty() && list.peek() < cutoff) {
                list.poll();
            }
        }
    }

    /**
     * Get total crops harvested this session.
     */
    public int getTotalSessionCount() {
        return sessionCounts.values().stream().mapToInt(Integer::intValue).sum();
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
        cleanupOldEntries(System.currentTimeMillis());
        return recentHarvests.size();
    }

    /**
     * Get harvests per hour for a specific crop.
     */
    public int getHarvestsPerHour(Block cropBlock) {
        cleanupOldEntries(System.currentTimeMillis());
        LinkedList<Long> cropList = recentHarvestsByCrop.get(cropBlock);
        return cropList != null ? cropList.size() : 0;
    }

    /**
     * Get harvests per minute (based on actual session time).
     * Rate is capped at actual count harvested.
     */
    public int getHarvestsPerMinute() {
        if (sessionStartTime == null)
            return 0;

        long elapsedMs = System.currentTimeMillis() - sessionStartTime;
        int total = getTotalSessionCount();
        if (total == 0 || elapsedMs < 1000)
            return 0;

        // Calculate rate: (count / elapsed_minutes)
        double minutes = elapsedMs / 60000.0;
        int rate = (int) Math.round(total / minutes);

        // Cap at actual count - can't show more than you've harvested
        return Math.min(rate, total);
    }

    /**
     * Get harvests per minute for a specific crop.
     * Rate is capped at actual count harvested.
     */
    public int getHarvestsPerMinute(Block cropBlock) {
        if (sessionStartTime == null)
            return 0;

        long elapsedMs = System.currentTimeMillis() - sessionStartTime;
        int count = getSessionCount(cropBlock);
        if (count == 0 || elapsedMs < 1000)
            return 0;

        double minutes = elapsedMs / 60000.0;
        int rate = (int) Math.round(count / minutes);

        // Cap at actual count
        return Math.min(rate, count);
    }

    /**
     * Get session duration in minutes.
     */
    public long getSessionDurationMinutes() {
        if (sessionStartTime == null)
            return 0;
        return (System.currentTimeMillis() - sessionStartTime) / (60 * 1000);
    }

    /**
     * Get a formatted string showing session stats.
     */
    public String getSessionStatsString() {
        int total = getTotalSessionCount();
        long minutes = getSessionDurationMinutes();

        if (minutes < 1) {
            return String.format("§a%d §fcrops", total);
        }

        return String.format("§a%d §fcrops §7(%.1f/min)", total, (double) total / minutes);
    }

    /**
     * Get a formatted string showing per-hour stats.
     */
    public String getPerHourStatsString() {
        int perHour = getHarvestsPerHour();
        return String.format("§a%d §f/hr", perHour);
    }

    /**
     * Reset all statistics.
     */
    public void reset() {
        sessionCounts.clear();
        recentHarvests.clear();
        for (LinkedList<Long> list : recentHarvestsByCrop.values()) {
            list.clear();
        }
        sessionStartTime = System.currentTimeMillis();
    }

    /**
     * Get map of session counts by crop for detailed display.
     */
    public Map<Block, Integer> getSessionCountsByCrop() {
        return new HashMap<>(sessionCounts);
    }

    private static Block[] getSupportedCrops() {
        return new Block[] {
                Blocks.WHEAT,
                Blocks.CARROTS,
                Blocks.POTATOES,
                Blocks.BEETROOTS,
                Blocks.NETHER_WART,
                Blocks.COCOA,
                Blocks.PUMPKIN,
                Blocks.MELON,
                Blocks.SWEET_BERRY_BUSH,
                Blocks.SUGAR_CANE,
                Blocks.BAMBOO,
                Blocks.KELP,
                Blocks.KELP_PLANT,
                Blocks.CACTUS,
                Blocks.CHORUS_PLANT,
                Blocks.CHORUS_FLOWER,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                Blocks.TORCHFLOWER,
                Blocks.TORCHFLOWER_CROP,
                Blocks.PITCHER_PLANT,
                Blocks.PITCHER_CROP
        };
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
