package net.internetTelephone.program.common.enter;

import android.graphics.drawable.Drawable;

import net.internetTelephone.program.MyApp;

/**
 * Created by chaochen on 14-11-12.
 */
public class DrawableTool {
    public static void zoomDrwable(Drawable drawable, boolean isMonkey) {
        int width = isMonkey ? MyApp.sEmojiMonkey : MyApp.sEmojiNormal;
        drawable.setBounds(0, 0, width, width);
    }
}
