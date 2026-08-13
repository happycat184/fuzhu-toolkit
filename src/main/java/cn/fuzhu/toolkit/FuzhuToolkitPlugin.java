package cn.fuzhu.toolkit;

import cn.fuzhu.toolkit.module.*;
import cn.fuzhu.toolkit.modules.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.*;

public final class FuzhuToolkitPlugin extends JavaPlugin implements CommandExecutor, TabCompleter {
    private final Map<String, ToolkitModule> modules = new LinkedHashMap<>();
    private File modsFile;
    private FileConfiguration mods;

    @Override public void onEnable() {
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File configs = new File(getDataFolder(), "configs"); if (!configs.exists()) configs.mkdirs();
        modsFile = new File(getDataFolder(), "mods.yml");
        if (!modsFile.exists()) saveResource("mods.yml", false);
        mods = YamlConfiguration.loadConfiguration(modsFile);
        registerModules();
        Objects.requireNonNull(getCommand("fuzhu")).setExecutor(this);
        Objects.requireNonNull(getCommand("fuzhu")).setTabCompleter(this);
        for (String command : List.of("bs","bb","spawn","sethome","home","warp","clearlag","playerinfo","feed","heal","weather","ptime","back","rules","motd","afk","near","tpa","tpaccept","tpdeny","rtp","invsee","ec","fly","gm","nick","repair","hat","anvil","craft","kit","vanish","chatcolor","broadcast","serverstats","ping","online","trash","condense","more","gtime","top","depth","itemname","itemlore","sit","glow","biome","chunkinfo","light","durability","itemid","deathloc","invsort","chatmute","privatecontainer","pv")) {
            PluginCommand c = getCommand(command); if (c != null) { c.setExecutor(this); c.setTabCompleter(this); }
        }
        modules.values().forEach(m -> { if (mods.getBoolean("modules." + m.id(), true)) { m.enable(); getLogger().info("Enabled module " + m.id()); } });
        msg(null, "&a腐竹工具箱已启动 &7| &f" + modules.size() + " 个模块");
    }
    @Override public void onDisable() { modules.values().forEach(ToolkitModule::disable); saveMods(); }
    private void registerModules() {
        add(new BlockSearchModule(this)); add(new BetterBossbarModule(this)); add(new WelcomeModule(this));
        add(new SpawnModule(this)); add(new HomeModule(this)); add(new WarpModule(this)); add(new ClearLagModule(this));
        add(new PlayerInfoModule(this)); add(new FeedHealModule(this)); add(new WeatherModule(this));
        add(new PersonalTimeModule(this)); add(new BackModule(this));
        add(new ExtraModules.RulesModule(this)); add(new ExtraModules.MotdModule(this)); add(new ExtraModules.AfkModule(this)); add(new ExtraModules.NearModule(this));
        add(new ExtraModules.TpaModule(this)); add(new ExtraModules.RtpModule(this)); add(new ExtraModules.InvseeModule(this)); add(new ExtraModules.EnderChestModule(this));
        add(new ExtraModules.FlyModule(this)); add(new ExtraModules.GameModeModule(this)); add(new ExtraModules.NickModule(this)); add(new ExtraModules.RepairModule(this));
        add(new ExtraModules.HatModule(this)); add(new ExtraModules.AnvilModule(this)); add(new ExtraModules.CraftModule(this)); add(new ExtraModules.KitModule(this));
        add(new ExtraModules.VanishModule(this)); add(new ExtraModules.ChatColorModule(this)); add(new ExtraModules.BroadcastModule(this)); add(new ExtraModules.ServerStatsModule(this));
        add(new AdditionalModules.Ping(this)); add(new AdditionalModules.Online(this)); add(new AdditionalModules.Trash(this)); add(new AdditionalModules.Condense(this));
        add(new AdditionalModules.More(this)); add(new AdditionalModules.GlobalTime(this)); add(new AdditionalModules.Top(this)); add(new AdditionalModules.Depth(this));
        add(new AdditionalModules.ItemName(this)); add(new AdditionalModules.ItemLore(this));
        add(new NovelModules.Sit(this)); add(new NovelModules.Glow(this)); add(new NovelModules.Biome(this)); add(new NovelModules.ChunkInfo(this));
        add(new NovelModules.Light(this)); add(new NovelModules.Durability(this)); add(new NovelModules.ItemId(this)); add(new NovelModules.DeathLoc(this));
        add(new NovelModules.InvSort(this)); add(new NovelModules.ChatMute(this)); add(new PrivateContainerModule(this));
    }
    private void add(ToolkitModule module) { modules.put(module.id().toLowerCase(Locale.ROOT), module); }
    public ToolkitModule module(String id) { return modules.get(id.toLowerCase(Locale.ROOT)); }
    private ToolkitModule commandModule(String command) {
        return switch (command.toLowerCase(Locale.ROOT)) {
            case "bs" -> module("BlockSearch"); case "bb" -> module("BetterBossbar"); case "spawn" -> module("Spawn");
            case "sethome", "home" -> module("Home"); case "warp" -> module("Warp"); case "clearlag" -> module("ClearLag");
            case "playerinfo" -> module("PlayerInfo"); case "feed", "heal" -> module("FeedHeal"); case "weather" -> module("Weather");
            case "ptime" -> module("PersonalTime"); case "back" -> module("Back");
            case "rules" -> module("Rules"); case "motd" -> module("Motd"); case "afk" -> module("Afk"); case "near" -> module("Near");
            case "tpa", "tpaccept", "tpdeny" -> module("Tpa"); case "rtp" -> module("Rtp"); case "invsee" -> module("Invsee");
            case "ec" -> module("EnderChest"); case "fly" -> module("Fly"); case "gm" -> module("GameMode"); case "nick" -> module("Nick");
            case "repair" -> module("Repair"); case "hat" -> module("Hat"); case "anvil" -> module("Anvil"); case "craft" -> module("Craft");
            case "kit" -> module("Kit"); case "vanish" -> module("Vanish"); case "chatcolor" -> module("ChatColor");
            case "broadcast" -> module("Broadcast"); case "serverstats" -> module("ServerStats");
            case "ping" -> module("Ping"); case "online" -> module("Online"); case "trash" -> module("Trash"); case "condense" -> module("Condense");
            case "more" -> module("More"); case "gtime" -> module("GlobalTime"); case "top" -> module("Top"); case "depth" -> module("Depth");
            case "itemname" -> module("ItemName"); case "itemlore" -> module("ItemLore");
            case "sit" -> module("Sit"); case "glow" -> module("Glow"); case "biome" -> module("Biome"); case "chunkinfo" -> module("ChunkInfo");
            case "light" -> module("Light"); case "durability" -> module("Durability"); case "itemid" -> module("ItemId"); case "deathloc" -> module("DeathLoc");
            case "invsort" -> module("InvSort"); case "chatmute" -> module("ChatMute"); case "privatecontainer", "pv" -> module("PrivateContainer"); default -> null;
        };
    }
    public Collection<ToolkitModule> modules() { return modules.values(); }
    public String color(String s) { return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s); }
    public void msg(CommandSender sender, String s) { if (sender != null) sender.sendMessage(color(getConfig().getString("prefix", "&8[&b腐竹工具箱&8] &7") + s)); }
    public void broadcast(String s) { getServer().broadcastMessage(color(getConfig().getString("prefix", "&8[&b腐竹工具箱&8] &7") + s)); }
    public void saveMods() { if (mods != null) try { mods.save(modsFile); } catch (Exception e) { getLogger().warning(e.getMessage()); } }
    /** 模块权限：op（默认）或 all。控制台不受限制。 */
    public boolean canUseModule(CommandSender sender, ToolkitModule module) {
        if (!(sender instanceof org.bukkit.entity.Player player)) return true;
        return mods.getString("access." + module.id(), "op").equalsIgnoreCase("all") || player.isOp();
    }
    public FileConfiguration moduleConfig(String id) {
        File f = new File(new File(getDataFolder(), "configs"), id + ".yml");
        FileConfiguration result = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) try { result.save(f); } catch (Exception e) { getLogger().warning("无法创建模块配置 " + f.getName()); }
        return result;
    }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fuzhu")) return rootCommand(sender, args);
        ToolkitModule m = commandModule(command.getName());
        if (m == null || !(m instanceof AbstractModule a) || !a.isEnabled()) { msg(sender, "&c该模块未启用。"); return true; }
        if (!canUseModule(sender, m)) { msg(sender, "&c该模块仅限 OP 使用。管理员可用 /fuzhu setnop " + m.id() + " 开放给普通玩家。"); return true; }
        return m.handle(sender, label, args);
    }
    private boolean rootCommand(CommandSender s, String[] a) {
        if (a.length == 0 || a[0].equalsIgnoreCase("list")) { msg(s, "&b模块列表："); modules.values().forEach(m -> msg(s, (m instanceof AbstractModule x && x.isEnabled() ? "&a✔ " : "&c✘ ") + "&f" + m.id() + " &7- " + m.description())); return true; }
        if (a[0].equalsIgnoreCase("help")) {
            if (a.length < 2) { msg(s, "&e用法: /fuzhu help <模块名>"); return true; }
            ToolkitModule target = module(a[1]);
            if (target == null) { msg(s, "&c未知模块。使用 /fuzhu list 查看模块名。"); return true; }
            msg(s, "&b模块帮助 &f" + target.id());
            msg(s, "&7说明: &f" + target.description());
            String usage = moduleUsage(target.id());
            if (usage != null) msg(s, "&7用法: &f" + usage);
            msg(s, "&7状态: " + (target instanceof AbstractModule x && x.isEnabled() ? "&a已启用" : "&c已禁用") + " &7| 访问: " + (mods.getString("access." + target.id(), "op").equalsIgnoreCase("all") ? "&a所有玩家" : "&e仅 OP"));
            return true;
        }
        if (!s.hasPermission("fuzhu.admin")) { msg(s, "&c你没有权限。"); return true; }
        if (a[0].equalsIgnoreCase("reload")) { reloadConfig(); mods = YamlConfiguration.loadConfiguration(modsFile); modules.values().forEach(m -> { boolean on = mods.getBoolean("modules." + m.id(), true); if (m instanceof AbstractModule x) { if (on && !x.isEnabled()) x.enable(); if (!on && x.isEnabled()) x.disable(); } }); msg(s, "&a配置已重载。"); return true; }
        if (a[0].equalsIgnoreCase("setop") || a[0].equalsIgnoreCase("setnop")) {
            if (a.length < 2) { msg(s, "&e用法: /fuzhu setop|setnop <模块名>"); return true; }
            ToolkitModule target = module(a[1]);
            if (target == null) { msg(s, "&c未知模块。"); return true; }
            boolean opOnly = a[0].equalsIgnoreCase("setop");
            mods.set("access." + target.id(), opOnly ? "op" : "all"); saveMods();
            msg(s, (opOnly ? "&a已设置为仅 OP 可用：" : "&a已开放给普通玩家：") + "&f" + target.id()); return true;
        }
        if (a.length < 2 || !(a[0].equalsIgnoreCase("enable") || a[0].equalsIgnoreCase("disable"))) { msg(s, "&e用法: /fuzhu enable|disable|setop|setnop <模块名>"); return true; }
        ToolkitModule m = module(a[1]); if (!(m instanceof AbstractModule x)) { msg(s, "&c未知模块。"); return true; }
        boolean on = a[0].equalsIgnoreCase("enable"); if (on) x.enable(); else x.disable(); mods.set("modules." + m.id(), on); saveMods(); msg(s, (on ? "&a已启用 " : "&c已禁用 ") + "&f" + m.id()); return true;
    }
    private String moduleUsage(String id) {
        return switch (id.toLowerCase(Locale.ROOT)) {
            case "blocksearch" -> "/bs <方块名> [半径]";
            case "betterbossbar" -> "/bb <文本> | /bb start|stop|progress|color";
            case "spawn" -> "/spawn";
            case "home" -> "/sethome | /home";
            case "warp" -> "/warp <名称> | /warp set|delete <名称>";
            case "clearlag" -> "/clearlag";
            case "playerinfo" -> "/playerinfo [玩家]";
            case "feedheal" -> "/feed | /heal";
            case "weather" -> "/weather sun|rain|storm";
            case "personaltime" -> "/ptime day|night";
            case "back" -> "/back";
            case "rules" -> "/rules";
            case "motd" -> "/motd";
            case "afk" -> "/afk";
            case "near" -> "/near [半径]";
            case "tpa" -> "/tpa <玩家> | /tpaccept | /tpdeny";
            case "rtp" -> "/rtp [半径]";
            case "invsee" -> "/invsee <玩家>";
            case "enderchest" -> "/ec";
            case "fly" -> "/fly";
            case "gamemode" -> "/gm <模式>";
            case "nick" -> "/nick <昵称>";
            case "repair" -> "/repair";
            case "hat" -> "/hat";
            case "anvil" -> "/anvil";
            case "craft" -> "/craft";
            case "kit" -> "/kit starter";
            case "vanish" -> "/vanish";
            case "chatcolor" -> "/chatcolor <颜色>|reset";
            case "broadcast" -> "/broadcast <文本>";
            case "serverstats" -> "/serverstats";
            case "ping" -> "/ping";
            case "online" -> "/online";
            case "trash" -> "/trash";
            case "condense" -> "/condense";
            case "more" -> "/more";
            case "globaltime" -> "/gtime day|night|noon";
            case "top" -> "/top";
            case "depth" -> "/depth";
            case "itemname" -> "/itemname <名称>";
            case "itemlore" -> "/itemlore <说明>";
            case "sit" -> "/sit";
            case "glow" -> "/glow";
            case "biome" -> "/biome";
            case "chunkinfo" -> "/chunkinfo";
            case "light" -> "/light";
            case "durability" -> "/durability";
            case "itemid" -> "/itemid";
            case "deathloc" -> "/deathloc";
            case "invsort" -> "/invsort";
            case "chatmute" -> "/chatmute";
            case "privatecontainer" -> "/pv create|open|give|removeperm|list";
            default -> null;
        };
    }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if (c.getName().equalsIgnoreCase("fuzhu")) { if (a.length == 1) return List.of("list","help","enable","disable","setop","setnop","reload").stream().filter(x -> x.startsWith(a[0].toLowerCase())).toList(); if (a.length == 2 && (a[0].equalsIgnoreCase("help") || a[0].equalsIgnoreCase("enable") || a[0].equalsIgnoreCase("disable") || a[0].equalsIgnoreCase("setop") || a[0].equalsIgnoreCase("setnop"))) return modules.values().stream().map(ToolkitModule::id).toList(); }
        return List.of();
    }
}
