package me.youhavetrouble.propaganda.messages;

import net.kyori.adventure.audience.Audience;

public interface SendableAnnouncement {

    void send(Audience audience);

}
