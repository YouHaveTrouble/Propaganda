package me.youhavetrouble.propaganda.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ActionbarAnnouncement implements Announcement {

    private final Component message;

    public ActionbarAnnouncement(Component message) {
        Component base = Component.empty();
        this.message = base.append(message);
    }

    @Override
    public void send(@NotNull Audience audience) {
        audience.sendActionBar(message);
    }

}
