> 目前只在1.21.3版本中测试/使用过

### 目前有的功能(可通过配置文件打开或关闭功能，默认全部关闭)
- 手持打开潜影盒、魔影箱、工作台。
- 玩家死亡时在死亡地点生成箱子，将物品存放进箱子。
- 繁殖青蛙时回服务器公告[万恶不赦的<玩家名>又开始繁殖他的青蛙了！(服务器有一个玩家老师大量繁殖青蛙，加这个玩的)
- 输入命令 `/bingo <玩家名>` 在玩家前方释放一个随机的烟花。
- 地狱门传送。

### 地狱门传送功能介绍
在地狱门中间部分放上项目告示牌，第一行第二行随便填，第三行填传送的坐标，第四行写传送到的世界名称和传送后的朝向
如下:
```
随便写
随便写
-10,64,100
world,180​
```
玩家进入地狱门后会传送到 世界名称为 world 的 -10,64,100 坐标处，朝向北方。
