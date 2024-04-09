package me.youhavetrouble.propaganda;

import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PropagandaConfig {

    public final Component prefix;

    protected PropagandaConfig(Toml config) {
        Toml general = config.getTable("General");
        prefix = MiniMessage.miniMessage().deserialize(general.getString("prefix", "<bold>[</bold><rainbow>Propaganda</rainbow><bold>]</bold> "));
    }

}
