package cn.fuzhu.toolkit.modules;

import cn.fuzhu.toolkit.FuzhuToolkitPlugin;
import cn.fuzhu.toolkit.module.AbstractModule;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import java.util.*;

public final class NovelModules {
    private NovelModules() {}
    static abstract class M extends AbstractModule { M(FuzhuToolkitPlugin p){super(p);config=p.moduleConfig(id());} Player p(CommandSender s){return ModuleUtil.player(plugin,s);} boolean admin(CommandSender s){if(!s.hasPermission("fuzhu.admin")){plugin.msg(s,"&c你没有权限。");return false;}return true;} }

    public static final class Sit extends M {
        private final Map<UUID, ArmorStand> seats=new HashMap<>(); private Listener listener;
        public Sit(FuzhuToolkitPlugin p){super(p);} public String id(){return "Sit";} public String description(){return "坐下互动动作";}
        public void enable(){super.enable(); listener=new Listener(){@EventHandler public void quit(PlayerQuitEvent e){remove(e.getPlayer());}}; plugin.getServer().getPluginManager().registerEvents(listener,plugin);}
        public void disable(){seats.values().forEach(a->{a.remove();});seats.clear();if(listener!=null)HandlerList.unregisterAll(listener);super.disable();}
        public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;if(seats.containsKey(p.getUniqueId())){remove(p);plugin.msg(s,"&a已站起。");return true;}Location loc=p.getLocation().clone().subtract(0,1.35,0);ArmorStand seat=p.getWorld().spawn(loc,ArmorStand.class,a0->{a0.setInvisible(true);a0.setInvulnerable(true);a0.setMarker(true);a0.setGravity(false);a0.setSilent(true);});seat.addPassenger(p);seats.put(p.getUniqueId(),seat);plugin.msg(s,"&a你坐下了。");return true;}
        private void remove(Player p){ArmorStand a=seats.remove(p.getUniqueId());if(a!=null)a.remove();}
    }
    public static final class Glow extends M { public Glow(FuzhuToolkitPlugin p){super(p);} public String id(){return "Glow";} public String description(){return "切换自身发光轮廓";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p!=null){p.setGlowing(!p.isGlowing());plugin.msg(s,"&a发光效果: "+(p.isGlowing()?"开启":"关闭"));}return true;} }
    public static final class Biome extends M { public Biome(FuzhuToolkitPlugin p){super(p);} public String id(){return "Biome";} public String description(){return "查看当前位置生物群系信息";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;Location o=p.getLocation();org.bukkit.block.Biome b=o.getBlock().getBiome();plugin.msg(s,"&b生物群系: &f"+b+" &7| 温度: &f"+String.format(Locale.ROOT,"%.2f",p.getWorld().getTemperature(o.getBlockX(),o.getBlockY(),o.getBlockZ()))+" &7| 湿度: &f"+String.format(Locale.ROOT,"%.2f",p.getWorld().getHumidity(o.getBlockX(),o.getBlockY(),o.getBlockZ())));return true;} }
    public static final class ChunkInfo extends M { public ChunkInfo(FuzhuToolkitPlugin p){super(p);} public String id(){return "ChunkInfo";} public String description(){return "查看区块坐标和实体诊断";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;Chunk c=p.getChunk();plugin.msg(s,"&b区块: &f"+c.getX()+", "+c.getZ()+" &7| 世界: &f"+p.getWorld().getName()+" &7| 实体: &f"+c.getEntities().length+" &7| 已加载: &f"+c.isLoaded());return true;} }
    public static final class Light extends M { public Light(FuzhuToolkitPlugin p){super(p);} public String id(){return "Light";} public String description(){return "检查当前位置光照等级";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;Block b=p.getLocation().getBlock();plugin.msg(s,"&b光照: &f"+b.getLightLevel()+" &7| 天空光: &f"+b.getLightFromSky()+" &7| 方块光: &f"+b.getLightFromBlocks());return true;} }
    public static final class Durability extends M { public Durability(FuzhuToolkitPlugin p){super(p);} public String id(){return "Durability";} public String description(){return "查看手中物品耐久度";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;ItemStack i=p.getInventory().getItemInMainHand();if(i.getType().isAir()||!(i.getItemMeta() instanceof Damageable d)){plugin.msg(s,"&e手中物品没有耐久度。");return true;}int max=i.getType().getMaxDurability();plugin.msg(s,"&b耐久: &f"+(max-d.getDamage())+"&7/&f"+max+" &7("+String.format(Locale.ROOT,"%.1f",(max-d.getDamage())*100.0/max)+"%)");return true;} }
    public static final class ItemId extends M { public ItemId(FuzhuToolkitPlugin p){super(p);} public String id(){return "ItemId";} public String description(){return "查看手中物品 Namespaced ID";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;ItemStack i=p.getInventory().getItemInMainHand();if(i.getType().isAir()){plugin.msg(s,"&c手中没有物品。");return true;}plugin.msg(s,"&b物品 ID: &f"+i.getType().getKey());return true;} }
    public static final class DeathLoc extends M { private final Map<UUID,Location> last=new HashMap<>(); private Listener listener; public DeathLoc(FuzhuToolkitPlugin p){super(p);} public String id(){return "DeathLoc";} public String description(){return "记录并查看最近死亡位置";} public void enable(){super.enable();listener=new Listener(){@EventHandler public void death(PlayerDeathEvent e){last.put(e.getEntity().getUniqueId(),e.getEntity().getLocation().clone());}};plugin.getServer().getPluginManager().registerEvents(listener,plugin);} public void disable(){last.clear();if(listener!=null)HandlerList.unregisterAll(listener);super.disable();} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;Location d=last.get(p.getUniqueId());if(d==null){plugin.msg(s,"&e暂无死亡记录。");return true;}plugin.msg(s,"&b最近死亡: &f"+d.getWorld().getName()+" "+d.getBlockX()+", "+d.getBlockY()+", "+d.getBlockZ());return true;} }
    public static final class InvSort extends M { public InvSort(FuzhuToolkitPlugin p){super(p);} public String id(){return "InvSort";} public String description(){return "按物品 ID 整理背包";} public boolean handle(CommandSender s,String l,String[] a){Player p=p(s);if(p==null)return true;ItemStack[] items=p.getInventory().getStorageContents();Arrays.sort(items,Comparator.nullsLast(Comparator.comparing(i->i==null?"":i.getType().getKey().toString())));p.getInventory().setStorageContents(items);plugin.msg(s,"&a背包已整理。");return true;} }
    public static final class ChatMute extends M { private boolean muted; private Listener listener; public ChatMute(FuzhuToolkitPlugin p){super(p);} public String id(){return "ChatMute";} public String description(){return "临时关闭全服聊天";} public void enable(){super.enable();listener=new Listener(){@EventHandler public void chat(AsyncPlayerChatEvent e){if(muted&&!e.getPlayer().hasPermission("fuzhu.admin")){e.setCancelled(true);plugin.msg(e.getPlayer(),"&c聊天当前已关闭。");}}};plugin.getServer().getPluginManager().registerEvents(listener,plugin);} public void disable(){muted=false;if(listener!=null)HandlerList.unregisterAll(listener);super.disable();} public boolean handle(CommandSender s,String l,String[] a){if(!admin(s))return true;muted=!muted;plugin.broadcast(muted?"&c全服聊天已关闭。":"&a全服聊天已开启。");return true;} }
}
