# 腐竹工具箱（FuzhuToolkit）

基于 Paper API 1.21.1、Java 21 的模块化 All-in-one 服务器插件。将构建产物放入 `plugins/` 后启动服务器，会自动生成：

```text
plugins/FuzhuToolkit/
├─ config.yml
├─ mods.yml
└─ configs/
   ├─ BlockSearch.yml
   ├─ BetterBossbar.yml
   └─ ...
```

## 模块与命令

| 模块 | 命令 | 功能 |
|---|---|---|
| BlockSearch | `/bs <方块名> [半径]` | 搜索半径内方块坐标，默认 100、最多 300 |
| BetterBossbar | `/bb <文本>` | 设置全服彩色 BossBar |
| Welcome | 无 | 玩家加入欢迎语 |
| Spawn | `/spawn` | 返回世界出生点 |
| Home | `/sethome`、`/home` | 个人家 |
| Warp | `/warp <名>`、`/warp set/delete <名>` | 公共传送点（管理权限） |
| ClearLag | `/clearlag` | 清理所有世界掉落物（管理权限） |
| PlayerInfo | `/playerinfo [玩家]` | 查看位置、血量、Ping |
| FeedHeal | `/feed`、`/heal` | 回复饥饿/生命 |
| Weather | `/weather sun\|rain\|storm` | 设置天气（管理权限） |
| PersonalTime | `/ptime day\|night` | 设置个人时间 |
| Back | `/back` | 返回上次传送前位置 |
| Rules | `/rules` | 查看服务器规则 |
| Motd | `/motd` | 查看服务器公告 |
| Afk | `/afk` | 切换挂机状态 |
| Near | `/near [半径]` | 查看附近玩家 |
| Tpa | `/tpa`、`/tpaccept`、`/tpdeny` | 玩家传送请求 |
| Rtp | `/rtp [半径]` | 随机安全传送 |
| Invsee | `/invsee <玩家>` | 管理员查看背包 |
| EnderChest | `/ec` | 打开末影箱 |
| Fly | `/fly` | 管理员切换飞行 |
| GameMode | `/gm <模式>` | 管理员切换模式 |
| Nick | `/nick <昵称>` | 设置显示昵称 |
| Repair | `/repair` | 修复手中物品 |
| Hat | `/hat` | 手持物品戴头上 |
| Anvil | `/anvil` | 打开铁砧 |
| Craft | `/craft` | 打开工作台 |
| Kit | `/kit starter` | 新手礼包 |
| Vanish | `/vanish` | 管理员隐身 |
| ChatColor | `/chatcolor <颜色>` | 设置聊天颜色 |
| Broadcast | `/broadcast <文本>` | 管理员全服广播 |
| ServerStats | `/serverstats` | 查看 TPS、内存和在线人数 |
| Ping | `/ping` | 查看个人延迟 |
| Online | `/online` | 查看在线玩家 |
| Trash | `/trash` | 打开临时垃圾桶，关闭时自动清空 |
| Condense | `/condense` | 九个铁/金/铜/钻石/绿宝石/煤压缩成方块 |
| More | `/more` | 将手中物品补满一组 |
| GlobalTime | `/gtime day\|night\|noon` | 设置当前世界时间（管理权限） |
| Top | `/top` | 传送到当前位置最高安全点 |
| Depth | `/depth` | 查看当前高度、海平面和世界范围 |
| ItemName | `/itemname <名称>` | 修改手中物品名称 |
| ItemLore | `/itemlore <说明>` | 为手中物品添加 Lore |
| Sit | `/sit` | 坐下互动动作 |
| Glow | `/glow` | 切换自身发光轮廓 |
| Biome | `/biome` | 查看生物群系、温度和湿度 |
| ChunkInfo | `/chunkinfo` | 查看区块坐标、实体数和加载状态 |
| Light | `/light` | 查看天空光与方块光等级 |
| Durability | `/durability` | 查看手中物品剩余耐久 |
| ItemId | `/itemid` | 查看物品 Namespaced ID |
| DeathLoc | `/deathloc` | 记录并查看最近死亡地点 |
| InvSort | `/invsort` | 按物品 ID 整理背包 |
| ChatMute | `/chatmute` | 管理员临时关闭普通玩家聊天 |

## 模块控制

* `/fuzhu list` 查看状态
* `/fuzhu enable <模块名>` / `/fuzhu disable <模块名>` 动态启停
* `/fuzhu setop <模块名>` 设置模块仅 OP 可用（默认）
* `/fuzhu setnop <模块名>` 开放模块给普通玩家
* `/fuzhu reload` 重载主配置

模块开关与访问策略会保存到 `mods.yml`，模块专属数据保存在 `configs/`，新增模块只需实现 `ToolkitModule` 并在主类注册。

构建：`mvn -DskipTests package`。

详细的配置项、BossBar 动画参数、模块开关和权限策略请查看 [docs/CONFIGURATION.md](docs/CONFIGURATION.md)。
