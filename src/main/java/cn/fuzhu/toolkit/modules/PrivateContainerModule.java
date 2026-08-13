package cn.fuzhu.toolkit.modules;

import cn.fuzhu.toolkit.FuzhuToolkitPlugin;
import cn.fuzhu.toolkit.module.AbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Per-player, multi-page storage persisted as YAML ItemStack data. */
public final class PrivateContainerModule extends AbstractModule {
    private final File storageFile;
    private final YamlConfiguration storage;
    private Listener listener;
    private int pages;
    private int rows;

    public PrivateContainerModule(FuzhuToolkitPlugin plugin) {
        super(plugin);
        config = plugin.moduleConfig(id());
        storageFile = new File(plugin.getDataFolder(), "configs/PrivateContainer.yml");
        storage = YamlConfiguration.loadConfiguration(storageFile);
    }

    @Override public String id() { return "PrivateContainer"; }
    @Override public String description() { return "玩家专属多页私人存储空间"; }

    @Override public void enable() {
        super.enable();
        pages = Math.max(1, Math.min(20, plugin.getConfig().getInt("private-container.pages", 3)));
        rows = Math.max(1, Math.min(6, plugin.getConfig().getInt("private-container.rows", 6)));
        listener = new Listener() {
            @EventHandler public void close(InventoryCloseEvent event) {
                if (event.getInventory().getHolder() instanceof ContainerHolder holder) save(holder);
            }
            @EventHandler public void quit(PlayerQuitEvent event) {
                Inventory open = event.getPlayer().getOpenInventory().getTopInventory();
                if (open.getHolder() instanceof ContainerHolder holder) save(holder);
            }
        };
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory open = player.getOpenInventory().getTopInventory();
            if (open.getHolder() instanceof ContainerHolder holder) save(holder);
        }
        if (listener != null) HandlerList.unregisterAll(listener);
        saveStorage();
        super.disable();
    }

    @Override public boolean handle(CommandSender sender, String label, String[] args) {
        Player player = ModuleUtil.player(plugin, sender);
        if (player == null) return true;
        int page = 1;
        if (args.length > 0) {
            try { page = Integer.parseInt(args[0]); }
            catch (NumberFormatException ignored) { plugin.msg(sender, "&e用法: /pv [页码]"); return true; }
        }
        if (page < 1 || page > pages) {
            plugin.msg(sender, "&c页码范围为 1-" + pages + "。可在 config.yml 的 private-container.pages 修改。");
            return true;
        }
        open(player, page);
        return true;
    }

    private void open(Player player, int page) {
        ContainerHolder holder = new ContainerHolder(player.getUniqueId(), page);
        String rawTitle = plugin.getConfig().getString("private-container.title", "&8私人仓库 &7- 第%page%页");
        String title = plugin.color(rawTitle.replace("%page%", String.valueOf(page)));
        Inventory inventory = Bukkit.createInventory(holder, rows * 9, title);
        holder.inventory = inventory;
        load(holder);
        player.openInventory(inventory);
        plugin.msg(player, "&a已打开私人仓库 &f第 " + page + " &a页，共 &f" + pages + " &a页。");
    }

    private String path(ContainerHolder holder) {
        return "players." + holder.owner + ".pages." + holder.page;
    }

    private void load(ContainerHolder holder) {
        for (Map<?, ?> entry : storage.getMapList(path(holder))) {
            Object slotValue = entry.get("slot");
            Object itemValue = entry.get("item");
            if (!(slotValue instanceof Number) || !(itemValue instanceof Map<?, ?>)) continue;
            int slot = ((Number) slotValue).intValue();
            if (slot < 0 || slot >= holder.inventory.getSize()) continue;
            try {
                @SuppressWarnings("unchecked") Map<String, Object> serialized = (Map<String, Object>) itemValue;
                holder.inventory.setItem(slot, ItemStack.deserialize(serialized));
            } catch (Exception ignored) { plugin.getLogger().warning("跳过损坏的私人仓库物品: " + holder.owner + " page " + holder.page); }
        }
    }

    private void save(ContainerHolder holder) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (int slot = 0; slot < holder.inventory.getSize(); slot++) {
            ItemStack item = holder.inventory.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;
            Map<String, Object> entry = new HashMap<>();
            entry.put("slot", slot);
            entry.put("item", item.serialize());
            items.add(entry);
        }
        storage.set(path(holder), items);
        saveStorage();
    }

    private void saveStorage() {
        try { storage.save(storageFile); }
        catch (IOException e) { plugin.getLogger().warning("无法保存私人仓库: " + e.getMessage()); }
    }

    private static final class ContainerHolder implements InventoryHolder {
        private final UUID owner;
        private final int page;
        private Inventory inventory;
        private ContainerHolder(UUID owner, int page) { this.owner = owner; this.page = page; }
        @Override public Inventory getInventory() { return inventory; }
    }
}
