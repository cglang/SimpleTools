package org.simpleTools.NetherPortalTeleport;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

public class SignUtils {
    /**
     * 获取告示牌上的文本内容
     * */
    public static String getSignLine(Sign sign, Side side, int index) {
        var textComponent = (TextComponent) sign.getSide(side).line(index);
        return textComponent.content();
    }
}
