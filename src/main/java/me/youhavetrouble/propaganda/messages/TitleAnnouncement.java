package me.youhavetrouble.propaganda.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class TitleAnnouncement implements Announcement {

    private final Title title;

    private TitleAnnouncement(Component message, Title.Times times, Builder.TitleMode mode) {
        if (mode == Builder.TitleMode.TITLE) {
            this.title = Title.title(message, Component.empty(), times);
            return;
        }
        this.title = Title.title(Component.empty(), message, times);
    }

    @Override
    public void send(Audience audience) {
        audience.showTitle(title);
    }

    public static class Builder {

        private final Component message;
        private Duration fadeIn, stay, fadeOut;
        private TitleMode mode;

        public Builder(@NotNull Component message) {
            this.message = message;
        }

        public Builder mode(@NotNull TitleMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder fadeIn(@Nullable Duration fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        public Builder stay(@Nullable Duration stay) {
            this.stay = stay;
            return this;
        }

        public Builder fadeOut(@Nullable Duration fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        public TitleAnnouncement build() {
            Title.Times times = Title.Times
                    .times(
                            fadeIn == null ? Duration.ofSeconds(1) : fadeIn,
                            stay == null ? Duration.ofSeconds(5) : stay,
                            fadeOut == null ? Duration.ofSeconds(1) : fadeOut
                    );
            return new TitleAnnouncement(message, times, mode);
        }

        public enum TitleMode {
            TITLE,
            SUBTITLE,
        }

    }
}
