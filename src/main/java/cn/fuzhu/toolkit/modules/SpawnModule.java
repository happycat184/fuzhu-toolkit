package cn.fuzhu.toolkit.modules;
import cn.fuzhu.toolkit.FuzhuToolkitPlugin; import cn.fuzhu.toolkit.module.AbstractModule; import org.bukkit.command.*; import org.bukkit.entity.Player;
public class SpawnModule extends AbstractModule {public SpawnModule(FuzhuToolkitPlugin p){super(p);config=p.moduleConfig(id());}public String id(){return "Spawn";}public String description(){return "安全传送至世界出生点";}public boolean handle(CommandSender s,String l,String[] a){Player p=ModuleUtil.player(plugin,s);if(p!=null){p.teleport(p.getWorld().getSpawnLocation());plugin.msg(s,"&a已传送至出生点。");}return true;}}
