/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: TradingPlatform
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package ru.zendal.event;

import com.google.inject.Inject;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ru.zendal.service.economy.EconomyProvider;
import ru.zendal.session.Session;
import ru.zendal.session.TradeOfflineSession;
import ru.zendal.session.inventory.PickBetInventoryManager;
import ru.zendal.session.inventory.holder.PickBetHolderInventory;

public class InventoryBetPickEvent implements Listener {

    private final EconomyProvider economyProvider;

    @Inject
    public InventoryBetPickEvent(EconomyProvider economyProvider) {
        this.economyProvider = economyProvider;
    }

    @EventHandler
    public void onPickAmount(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        if (inventory.getHolder() instanceof PickBetHolderInventory) {
            PickBetHolderInventory holderInventory = (PickBetHolderInventory) inventory.getHolder();
            event.setCancelled(true);
            if (event.getCurrentItem().getType() == Material.GOLD_NUGGET || event.getCurrentItem().getType() == Material.IRON_NUGGET) {
                this.processPickBet(holderInventory, player, event.getSlot());
            } else if (event.getCurrentItem().getType() == Material.BARRIER) {
                event.getWhoClicked().openInventory(holderInventory.getSession().getInventory());
            }
        }
    }

    /**
     * Process Pick Bet
     *
     * @param holder     Inventory holder
     * @param whoClicked who click
     * @param slot       index slot
     */
    private void processPickBet(PickBetHolderInventory holder, Player whoClicked, int slot) {
        Session session = holder.getSession();
        PickBetInventoryManager manager = holder.getInventoryManager();

        double bet = manager.getAmountBySlotIndex(slot);
        boolean isSeller = whoClicked == session.getSeller();

        bet += isSeller ? session.getBetSeller() : session.getBetBuyer();

        double balance = economyProvider.getBalance(whoClicked);

        //Is Creative mode
        if (holder.getSession() instanceof TradeOfflineSession && !isSeller) {
            balance = Double.MAX_VALUE;
        }

        if (bet >= 0) {
            if (balance <= bet) {
                if (isSeller) {
                    session.setBetSeller(balance);
                } else {
                    session.setBetBuyer(balance);
                }
            } else {
                if (isSeller) {
                    session.setBetSeller(bet);
                } else {
                    session.setBetBuyer(bet);
                }
            }
        } else {
            if (isSeller) {
                session.setBetSeller(0);
            } else {
                session.setBetBuyer(0);
            }
        }
        manager.updateInventory(whoClicked);
    }
}
