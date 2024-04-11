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
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;

import java.util.HashSet;
import java.util.Set;


public class AnnounceCommand {

    public static void createCommand(Propaganda plugin) {
        CommandManager<CommandSource> commandManager = plugin.getCommandManager();
        Command<CommandSource> command = commandManager
                .commandBuilder("announce")
                .flag(CommandFlag.builder("type")
                        .withPermission(Permission.permission("propaganda.announce.type"))
                        .withAliases("t")
                        .asRepeatable()
                        .withDescription(Description.of("The type of announcement to send"))
                        .withComponent(StringParser.stringComponent(StringParser.StringMode.SINGLE).name("type").build())
                )
                .required("message", StringParser.greedyFlagYieldingStringParser())
                .handler(context -> {
                    Audience audience = plugin.getServer();
                    String message = context.get("message");
                    Component messageComponent = MiniMessage.miniMessage().deserialize(message);
                    Component noNewlineMessageComponent = MiniMessage.miniMessage().deserialize(message.replace("<newline>", ""));

                    if (context.flags().isPresent("type")) {
                        Set<AnnouncementType> types = new HashSet<>();

                        context.flags().getAll("type").forEach(type -> {
                            try {
                                types.add(AnnouncementType.valueOf(type.toString().toUpperCase()));
                            } catch (IllegalArgumentException ignored) {}
                        });

                        if (types.isEmpty()) {
                            context.sender().sendMessage(Component.text("No valid announcement type specified"));
                            return;
                        }

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
                        return;
                    }

                    new ChatAnnouncement.Builder(messageComponent)
                            .prefix(plugin.getConfig().prefix)
                            .build()
                            .send(context.sender());

                })
                .build();
        commandManager.command(command);
    }

}
