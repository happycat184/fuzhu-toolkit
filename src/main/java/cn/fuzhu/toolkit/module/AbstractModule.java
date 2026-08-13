package cn.fuzhu.toolkit.module;

import cn.fuzhu.toolkit.FuzhuToolkitPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class AbstractModule implements ToolkitModule {
    protected final FuzhuToolkitPlugin plugin;
    protected FileConfiguration config;
    protected boolean enabled;
    protected AbstractModule(FuzhuToolkitPlugin plugin) { this.plugin = plugin; }
    @Override public void enable() { enabled = true; }
    @Override public void disable() { enabled = false; }
    public boolean isEnabled() { return enabled; }
    protected String text(String key, String fallback) { return config == null ? fallback : config.getString(key, fallback); }
}
