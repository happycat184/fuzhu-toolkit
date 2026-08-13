package cn.fuzhu.toolkit.modules;
import cn.fuzhu.toolkit.FuzhuToolkitPlugin; import org.bukkit.command.CommandSender; import org.bukkit.entity.Player;
final class ModuleUtil { static Player player(FuzhuToolkitPlugin p, CommandSender s){ if(s instanceof Player x)return x; p.msg(s,"&c该命令只能由玩家执行。"); return null;} static String join(String[] a,int from){return String.join(" ",java.util.Arrays.copyOfRange(a,from,a.length));} }
