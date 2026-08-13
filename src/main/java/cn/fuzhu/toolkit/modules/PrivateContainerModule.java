package cn.fuzhu.toolkit.modules;

import cn.fuzhu.toolkit.FuzhuToolkitPlugin;
import cn.fuzhu.toolkit.module.AbstractModule;
import org.bukkit.Bukkit;
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

/** Shared token-based vaults. Owners can grant/revoke access to other players. */
public final class PrivateContainerModule extends AbstractModule {
    private final File storageFile;
    private final YamlConfiguration storage;
    private Listener listener;
    private int rows;

    public PrivateContainerModule(FuzhuToolkitPlugin plugin) {
        super(plugin);
        config = plugin.moduleConfig(id());
        storageFile = new File(plugin.getDataFolder(), "configs/PrivateContainer.yml");
        storage = YamlConfiguration.loadConfiguration(storageFile);
    }

    @Override public String id() { return "PrivateContainer"; }
    @Override public String description() { return "Token共享仓库：创建、授权并多人共同存取"; }

    @Override public void enable() {
        super.enable();
        rows = Math.max(1, Math.min(6, plugin.getConfig().getInt("private-container.rows", 6)));
        listener = new Listener() {
            @EventHandler public void close(InventoryCloseEvent event) {
                if (event.getInventory().getHolder() instanceof VaultHolder holder) saveVault(holder);
            }
            @EventHandler public void quit(PlayerQuitEvent event) {
                Inventory open = event.getPlayer().getOpenInventory().getTopInventory();
                if (open.getHolder() instanceof VaultHolder holder) saveVault(holder);
            }
        };
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory open = player.getOpenInventory().getTopInventory();
            if (open.getHolder() instanceof VaultHolder holder) saveVault(holder);
        }
        if (listener != null) HandlerList.unregisterAll(listener);
        saveStorage();
        super.disable();
    }

    @Override public boolean handle(CommandSender sender, String label, String[] args) {
        Player player = ModuleUtil.player(plugin, sender);
        if (player == null) return true;
        if (args.length == 0) {
            plugin.msg(sender, "&e/pv create | /pv open <Token> | /pv give <Token> <玩家名> | /pv removeperm <Token> <玩家名> | /pv list");
            return true;
        }
        return switch (args[0].toLowerCase()) {
            case "create" -> create(player);
            case "open" -> openCommand(player, args);
            case "give" -> permissionCommand(player, args, true);
            case "removeperm" -> permissionCommand(player, args, false);
            case "list" -> list(player);
            default -> openByToken(player, args[0]);
        };
    }

    private boolean create(Player player) {
        String ownerPath = "owners." + player.getUniqueId();
        List<String> owned = new ArrayList<>(storage.getStringList(ownerPath));
        if (owned.size() >= 3) { plugin.msg(player, "&c每位玩家最多创建 3 个共享仓库。"); return true; }
        String token;
        do { token = String.valueOf(1000 + java.util.concurrent.ThreadLocalRandom.current().nextInt(9000)); }
        while (storage.contains("vaults." + token));
        owned.add(token);
        storage.set(ownerPath, owned);
        storage.set("vaults." + token + ".owner", player.getUniqueId().toString());
        storage.set("vaults." + token + ".permissions", new ArrayList<String>());
        storage.set("vaults." + token + ".items", new ArrayList<Map<String, Object>>());
        saveStorage();
        plugin.msg(player, "&a共享仓库创建成功，Token: &f" + token);
        plugin.msg(player, "&7使用 &f/pv open " + token + " &7打开，可用 &f/pv give " + token + " <玩家名> &7授权。");
        return true;
    }

    private boolean openCommand(Player player, String[] args) {
        if (args.length < 2) { plugin.msg(player, "&e用法: /pv open <Token>"); return true; }
        return openByToken(player, args[1]);
    }

    private boolean openByToken(Player player, String token) {
        if (!token.matches("\\d{4}") || !storage.contains("vaults." + token)) { plugin.msg(player, "&c仓库 Token 不存在，必须是四位数字。"); return true; }
        String uuid = player.getUniqueId().toString();
        String owner = storage.getString("vaults." + token + ".owner", "");
        List<String> permissions = storage.getStringList("vaults." + token + ".permissions");
        if (!uuid.equals(owner) && !permissions.contains(uuid)) { plugin.msg(player, "&c你没有这个仓库的访问权限。"); return true; }
        VaultHolder holder = new VaultHolder(token);
        holder.inventory = Bukkit.createInventory(holder, rows * 9, plugin.color("&8共享仓库 &7[" + token + "]"));
        loadVault(holder);
        player.openInventory(holder.inventory);
        return true;
    }

    private boolean permissionCommand(Player owner, String[] args, boolean grant) {
        if (args.length < 3) { plugin.msg(owner, "&e用法: /pv " + (grant ? "give" : "removeperm") + " <Token> <玩家名>"); return true; }
        String token = args[1];
        String ownerUuid = storage.getString("vaults." + token + ".owner", "");
        if (!owner.getUniqueId().toString().equals(ownerUuid)) { plugin.msg(owner, "&c只有仓库创建者可以管理权限。"); return true; }
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) { plugin.msg(owner, "&c目标玩家必须在线。"); return true; }
        List<String> permissions = new ArrayList<>(storage.getStringList("vaults." + token + ".permissions"));
        String targetUuid = target.getUniqueId().toString();
        if (grant && !permissions.contains(targetUuid)) permissions.add(targetUuid);
        if (!grant) permissions.remove(targetUuid);
        storage.set("vaults." + token + ".permissions", permissions);
        saveStorage();
        plugin.msg(owner, grant ? "&a已授权 &f" + target.getName() + " &a访问仓库。" : "&a已移除 &f" + target.getName() + " &a的访问权限。");
        return true;
    }

    private boolean list(Player player) {
        List<String> owned = storage.getStringList("owners." + player.getUniqueId());
        plugin.msg(player, owned.isEmpty() ? "&e你还没有创建共享仓库。" : "&b你创建的仓库: &f" + String.join(", ", owned));
        return true;
    }

    private void loadVault(VaultHolder holder) {
        for (Map<?, ?> entry : storage.getMapList("vaults." + holder.token + ".items")) {
            if (!(entry.get("slot") instanceof Number) || !(entry.get("item") instanceof Map<?, ?>)) continue;
            int slot = ((Number) entry.get("slot")).intValue();
            if (slot < 0 || slot >= holder.inventory.getSize()) continue;
            try {
                @SuppressWarnings("unchecked") Map<String, Object> serialized = (Map<String, Object>) entry.get("item");
                holder.inventory.setItem(slot, ItemStack.deserialize(serialized));
            } catch (Exception ignored) { plugin.getLogger().warning("跳过损坏的仓库物品: " + holder.token); }
        }
    }

    private void saveVault(VaultHolder holder) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (int slot = 0; slot < holder.inventory.getSize(); slot++) {
            ItemStack item = holder.inventory.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;
            Map<String, Object> entry = new HashMap<>(); entry.put("slot", slot); entry.put("item", item.serialize()); items.add(entry);
        }
        storage.set("vaults." + holder.token + ".items", items);
        saveStorage();
    }

    private void saveStorage() { try { storage.save(storageFile); } catch (IOException e) { plugin.getLogger().warning("无法保存共享仓库: " + e.getMessage()); } }

    private static final class VaultHolder implements InventoryHolder {
        private final String token; private Inventory inventory;
        private VaultHolder(String token) { this.token = token; }
        @Override public Inventory getInventory() { return inventory; }
    }
}
