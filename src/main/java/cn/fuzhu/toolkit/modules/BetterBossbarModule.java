package cn.fuzhu.toolkit.modules;

import cn.fuzhu.toolkit.FuzhuToolkitPlugin;
import cn.fuzhu.toolkit.module.AbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Animated, configurable global boss bar. */
public class BetterBossbarModule extends AbstractModule {
    private BossBar bar;
    private BukkitTask animationTask;
    private String title = "";
    private double step = 0.02;
    private boolean forward = true;
    private List<BarColor> cycleColors = new ArrayList<>();
    private int colorIndex;

    public BetterBossbarModule(FuzhuToolkitPlugin plugin) {
        super(plugin);
        config = plugin.moduleConfig(id());
    }

    @Override public String id() { return "BetterBossbar"; }
    @Override public String description() { return "Global boss bar with progress animation and color cycling"; }

    @Override public void enable() {
        super.enable();
        if (bar == null) {
            BarColor color = parseColor(plugin.getConfig().getString("bossbar.color", "BLUE"));
            BarStyle style = parseStyle(plugin.getConfig().getString("bossbar.style", "SOLID"));
            bar = Bukkit.createBossBar(title, color, style);
        }
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
        loadAnimationSettings();
        boolean auto = plugin.getConfig().getBoolean("bossbar.animation.enabled", false);
        if (auto) startAnimation();
    }

    @Override public void disable() {
        stopAnimation();
        if (bar != null) bar.removeAll();
        super.disable();
    }

    private void loadAnimationSettings() {
        step = Math.max(0.001, Math.min(1.0, plugin.getConfig().getDouble("bossbar.animation.step", 0.02)));
        cycleColors.clear();
        for (String value : plugin.getConfig().getStringList("bossbar.animation.colors")) {
            try { cycleColors.add(BarColor.valueOf(value.toUpperCase(Locale.ROOT))); } catch (IllegalArgumentException ignored) { }
        }
        if (cycleColors.isEmpty()) cycleColors.add(parseColor(plugin.getConfig().getString("bossbar.color", "BLUE")));
    }

    public void startAnimation() {
        if (bar == null) enable();
        stopAnimation();
        long period = Math.max(1, plugin.getConfig().getLong("bossbar.animation.period-ticks", 2));
        animationTask = new BukkitRunnable() {
            @Override public void run() {
                if (bar == null || !enabled) { cancel(); return; }
                double next = bar.getProgress() + (forward ? step : -step);
                if (next >= 1.0 || next <= 0.0) {
                    next = next >= 1.0 ? 0.0 : 1.0;
                    forward = !forward;
                    colorIndex = (colorIndex + 1) % cycleColors.size();
                    bar.setColor(cycleColors.get(colorIndex));
                }
                bar.setProgress(Math.max(0.0, Math.min(1.0, next)));
                Bukkit.getOnlinePlayers().forEach(BetterBossbarModule.this::addPlayer);
            }
        }.runTaskTimer(plugin, 1L, period);
    }

    public void stopAnimation() {
        if (animationTask != null) { animationTask.cancel(); animationTask = null; }
    }

    private void addPlayer(org.bukkit.entity.Player player) {
        if (bar != null && !bar.getPlayers().contains(player)) bar.addPlayer(player);
    }

    @Override public boolean handle(CommandSender sender, String label, String[] args) {
        if (bar == null) enable();
        if (args.length == 0) {
            plugin.msg(sender, "&e/bb <文本> | /bb start <文本> | /bb stop | /bb progress <0-100> | /bb color <颜色>");
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("stop")) { stopAnimation(); plugin.msg(sender, "&aBossBar 动画已停止。"); return true; }
        if (sub.equals("start")) {
            if (args.length > 1) setTitle(ModuleUtil.join(args, 1));
            startAnimation(); plugin.msg(sender, "&aBossBar 动画已启动。"); return true;
        }
        if (sub.equals("progress")) {
            try { bar.setProgress(Math.max(0.0, Math.min(1.0, Double.parseDouble(args[1]) / 100.0))); }
            catch (Exception e) { plugin.msg(sender, "&c请输入 0-100 的进度。"); return true; }
            return true;
        }
        if (sub.equals("color")) {
            if (args.length < 2) { plugin.msg(sender, "&e可用颜色: BLUE GREEN RED PINK PURPLE WHITE YELLOW" ); return true; }
            try { bar.setColor(BarColor.valueOf(args[1].toUpperCase(Locale.ROOT))); plugin.msg(sender, "&aBossBar 颜色已更新。"); }
            catch (IllegalArgumentException e) { plugin.msg(sender, "&c未知颜色。"); }
            return true;
        }
        setTitle(ModuleUtil.join(args, 0));
        plugin.broadcast("&bBossBar 文本已更新。 ");
        return true;
    }

    private void setTitle(String value) {
        title = plugin.color(value);
        bar.setTitle(title);
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
    }

    private BarColor parseColor(String value) { try { return BarColor.valueOf(value.toUpperCase(Locale.ROOT)); } catch (Exception e) { return BarColor.BLUE; } }
    private BarStyle parseStyle(String value) { try { return BarStyle.valueOf(value.toUpperCase(Locale.ROOT)); } catch (Exception e) { return BarStyle.SOLID; } }
}
