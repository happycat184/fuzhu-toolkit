package cn.fuzhu.toolkit.module;

import org.bukkit.command.CommandSender;

public interface ToolkitModule {
    String id();
    String description();
    void enable();
    void disable();
    boolean handle(CommandSender sender, String label, String[] args);
}
