package me.youhavetrouble.propaganda.command;


import com.velocitypowered.api.command.CommandSource;
import me.youhavetrouble.propaganda.Propaganda;
import me.youhavetrouble.propaganda.messages.ActionbarAnnouncement;
import me.youhavetrouble.propaganda.messages.AnnouncementType;
import me.youhavetrouble.propaganda.messages.ChatAnnouncement;
import me.youhavetrouble.propaganda.messages.TitleAnnouncement;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import java.util.*;

public class AnnounceCommand {

    public static void createCommand(Propaganda plugin) {
        CommandManager<CommandSource> commandManager = plugin.getCommandManager();
        Command<CommandSource> command = commandManager
                .commandBuilder("announce")
                .commandDescription(Description.of("Send an announcement"))
                .permission("propaganda.announce")
                .flag(CommandUtil.ANNOUNCEMENT_TYPE_FLAG)
                .flag(CommandUtil.SERVER_FLAG)
                .required("message", StringParser.quotedStringParser())
                .handler(context -> {
                    String message = context.get("message");
                    Component messageComponent = MiniMessage.miniMessage().deserialize(message);
                    Component noNewlineMessageComponent = MiniMessage.miniMessage().deserialize(message.replace("<newline>", ""));

                    Audience audience = CommandUtil.getAudienceFromContext(plugin.getServer(), context);

                    if (audience == null) {
                        context.sender().sendMessage(Component.text("No valid server specified"));
                        return;
                    }

                    Set<AnnouncementType> types = CommandUtil.getTypesFromContext(context);

                    if (types.contains(AnnouncementType.CHAT)) {
                        new ChatAnnouncement.Builder(messageComponent)
                                .prefix(plugin.getConfig().prefix)
                                .build()
                                .send(audience);
                    }
                    if (types.contains(AnnouncementType.TITLE)) {
                        new TitleAnnouncement.Builder(noNewlineMessageComponent)
                                .mode(TitleAnnouncement.Builder.TitleMode.SUBTITLE)
                                .build()
                                .send(audience);
                    }
                    if (types.contains(AnnouncementType.ACTIONBAR)) {
                        new ActionbarAnnouncement(noNewlineMessageComponent)
                                .send(audience);
                    }

                })
                .build();
        commandManager.command(command);
    }

}
