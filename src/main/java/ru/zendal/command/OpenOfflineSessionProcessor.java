/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: TradingPlatform
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package ru.zendal.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.zendal.config.LanguageConfig;
import ru.zendal.session.TradeOffline;
import ru.zendal.session.TradeSessionManager;
import ru.zendal.session.exception.TradeSessionManagerException;

/**
 * Open offline trade Command processor
 * <p>
 * /trade open
 */
public class OpenOfflineSessionProcessor implements ArgsCommandProcessor {


    private final LanguageConfig language;
    private final TradeSessionManager manager;

    public OpenOfflineSessionProcessor(TradeSessionManager manager, LanguageConfig language) {
        this.manager = manager;
        this.language = language;

    }

    @Override
    public boolean process(Command command, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            language.getMessage("trade.offline.notPickId").sendMessage(player);
        } else {
            try {
                TradeOffline tradeOffline = manager.getTradeOfflineById(args[1]);
                player.openInventory(tradeOffline.getInventory());
            } catch (TradeSessionManagerException e) {
                language.getMessage("trade.offline.udefinedId").setCustomMessage(1, args[1]).
                        sendMessage(player);
            }
        }
        return true;
    }


    @Override
    public boolean isCanBeProcessed(CommandSender sender, String[] args) {
        return args.length > 0 && args[0].equalsIgnoreCase("open");

    }
}
