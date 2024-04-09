package me.youhavetrouble.propaganda.command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import me.youhavetrouble.propaganda.Propaganda;
import me.youhavetrouble.propaganda.messages.ChatAnnouncement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class AnnounceCommand  {

    public static BrigadierCommand createCommand(Propaganda plugin) {
        LiteralCommandNode<CommandSource> announceCommand = BrigadierCommand.literalArgumentBuilder("announce")
                .executes(context -> {
                    context.getSource().sendMessage(Component.text("Usage: /announce [options] <message>"));
                    return Command.SINGLE_SUCCESS;
                })
                .requires(source -> source.hasPermission("propaganda.command.announce"))
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                        .requires(source -> source.hasPermission("propaganda.command.announce.text"))
                        .executes(context -> {
                            String message = context.getArgument("message", String.class);
                            Component componentMessage = MiniMessage.miniMessage().deserialize(message);
                            ChatAnnouncement announcement = new ChatAnnouncement.Builder(componentMessage)
                                    .prefix(plugin.getConfig().prefix)
                                    .build();
                            announcement.send(plugin.getServer());
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
        return new BrigadierCommand(announceCommand);
    }

}
