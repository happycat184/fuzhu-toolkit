# 腐竹工具箱配置文档

插件数据目录：`plugins/FuzhuToolkit/`

## config.yml

### 基础配置

```yaml
prefix: '&8[&b腐竹工具箱&8] &7'
search-default-radius: 100
search-max-radius: 300
welcome-message: '&b欢迎 &f%player% &b加入服务器！'
motd: '&b欢迎来到服务器！使用 /fuzhu list 查看工具箱模块。'
```

- `prefix`：所有 Chat 消息前缀，支持 `&` 颜色代码。
- `search-default-radius`：`/bs` 未填写半径时使用的半径。
- `search-max-radius`：`/bs` 允许的最大搜索半径，防止过大范围造成卡顿。
- `welcome-message`：玩家加入时的欢迎消息，`%player%` 会替换成玩家名。
- `motd`：`/motd` 显示的公告。

### BetterBossbar

```yaml
bossbar:
  color: BLUE
  style: SOLID
  animation:
    enabled: false
    step: 0.02
    period-ticks: 2
    colors: [BLUE, GREEN, YELLOW, RED, PURPLE]
```

- `color`：初始颜色，可用 `BLUE/GREEN/RED/PINK/PURPLE/WHITE/YELLOW`。
- `style`：`SOLID`、`SEGMENTED_6`、`SEGMENTED_10`、`SEGMENTED_12`、`SEGMENTED_20`。
- `animation.enabled`：启动时是否自动播放进度动画。
- `animation.step`：每次更新的进度增量，范围 `0.001` 到 `1.0`。
- `animation.period-ticks`：更新间隔，20 ticks 约等于 1 秒。
- `animation.colors`：进度到达边界时循环切换的颜色列表。

运行时命令：

```text
/bb <文本>
/bb start [文本]
/bb stop
/bb progress <0-100>
/bb color <颜色>
```

## mods.yml

`modules` 控制模块是否启用：

```yaml
modules:
  BlockSearch: true
  BetterBossbar: true
```

`access` 控制模块访问权限：

```yaml
access:
  Home: all
  Fly: op
```

- `op`：仅 OP 或拥有 `fuzhu.admin` 权限的管理员可使用。
- `all`：普通玩家也可使用。

推荐使用命令修改权限并自动保存：

```text
/fuzhu setop <模块名>
/fuzhu setnop <模块名>
```

## configs/

每个模块启动时会自动创建独立配置文件，例如 `configs/Home.yml`、`configs/BetterBossbar.yml`。模块数据与主配置分离，升级插件时不会覆盖服务器自定义数据。

## 重载与排错

修改 YAML 后执行：

```text
/fuzhu reload
```

如果插件无法加载，优先检查 YAML 缩进、冒号后的空格、特殊字符是否使用引号，并查看服务器 `latest.log`。
