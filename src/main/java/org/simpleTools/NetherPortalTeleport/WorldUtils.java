package org.simpleTools.NetherPortalTeleport;

import java.util.UUID;

public class WorldUtils {
    public static long uuidToSeed(UUID playerId) {
        // 将UUID转化为一个long类型的种子
        long mostSigBits = playerId.getMostSignificantBits();
        long leastSigBits = playerId.getLeastSignificantBits();

        return mostSigBits ^ leastSigBits;  // 使用异或操作生成种子
    }
}
