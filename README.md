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

## 模块控制

* `/fuzhu list` 查看状态
* `/fuzhu enable <模块名>` / `/fuzhu disable <模块名>` 动态启停
* `/fuzhu setop <模块名>` 设置模块仅 OP 可用（默认）
* `/fuzhu setnop <模块名>` 开放模块给普通玩家
* `/fuzhu reload` 重载主配置

模块开关与访问策略会保存到 `mods.yml`，模块专属数据保存在 `configs/`，新增模块只需实现 `ToolkitModule` 并在主类注册。

构建：`mvn -DskipTests package`。
