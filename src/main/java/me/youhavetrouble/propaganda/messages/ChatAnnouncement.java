package me.youhavetrouble.propaganda.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatAnnouncement implements Announcement {

    private final Component message;

    private ChatAnnouncement(Component prefix, Component message) {
        Component base = Component.empty();
        if (prefix != null) {
            base = base.append(prefix);
        }
        this.message = base.append(message);
    }

    @Override
    public void send(@NotNull Audience audience) {
        audience.sendMessage(message);
    }

    public static class Builder {

        private final Component message;
        private Component prefix;

        public Builder(@NotNull Component message) {
            this.message = message;
        }

        public Builder prefix(@Nullable Component prefix) {
            this.prefix = prefix;
            return this;
        }

        public ChatAnnouncement build() {
            return new ChatAnnouncement(prefix, message);
        }

    }
}
