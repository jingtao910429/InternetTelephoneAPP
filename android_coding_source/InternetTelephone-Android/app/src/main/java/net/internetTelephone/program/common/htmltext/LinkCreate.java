package net.internetTelephone.program.common.htmltext;

import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.model.Maopao;

/**
 * Created by chenchao on 15/3/9.
 */
public class LinkCreate {

    public static String maopao(Maopao.MaopaoObject maopao) {
        return Global.HOST + maopao.path;
    }


}
