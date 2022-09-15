package net.swofty.dungeons.gui.guiitem;

import net.swofty.dungeons.gui.GUI;

public interface GUIChatQueryItem extends GUIClickableItem {
    GUI onQueryFinish(String query);

    default boolean acceptRightClick() {
        return true;
    }
}
