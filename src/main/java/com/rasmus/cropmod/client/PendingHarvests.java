package com.rasmus.cropmod.client;

import java.util.ArrayDeque;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

/**
 * destroyBlock runs inside the prediction window, before the packet even
 * leaves the client, so a count taken there means "the client thought this
 * was legal" - a server that rejects the break (protection plugins) would
 * still be counted. Each break parks here instead, and counts only if the
 * block is still gone a few ticks later; a revert restores the same crop
 * block and the entry is dropped. A neighbor replanting within the window
 * can hide a real harvest, which under-counts, never over-counts.
 */
public final class PendingHarvests {

    private record Pending(BlockPos pos, Block block, long deadline) {
    }

    private static final int CONFIRM_TICKS = 4;
    private static final ArrayDeque<Pending> QUEUE = new ArrayDeque<>();

    private PendingHarvests() {
    }

    public static void add(BlockPos pos, Block block, long gameTime) {
        QUEUE.add(new Pending(pos.immutable(), block, gameTime + CONFIRM_TICKS));
    }

    public static void tick(Minecraft client) {
        if (QUEUE.isEmpty()) {
            return;
        }
        if (client.level == null) {
            QUEUE.clear();
            return;
        }
        long now = client.level.getGameTime();
        while (!QUEUE.isEmpty() && QUEUE.peek().deadline <= now) {
            Pending pending = QUEUE.poll();
            if (!client.level.getBlockState(pending.pos).is(pending.block)) {
                HarvestStatistics.getInstance().recordHarvest(pending.block);
            }
        }
    }

    public static void clear() {
        QUEUE.clear();
    }
}
