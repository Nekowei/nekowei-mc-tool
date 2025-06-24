# nekowei-mc-tool
a toolkit mod for minecraft

---

## 说明

这是一个基于fabric的mod工具包，请确保已安装fabric相关模组

## 安装

在release中下载最新jar包，放到minecraft的mod文件中即可

## 工具

目前支持的工具

### /removeInvisibleBlock 移除不可见方块指令

- 作用

本工具主要用于大型建筑剔除冗余材料，节约宝贵的时间精力

根据选取的区域范围，检测所有方块，如果其周围**直接相邻**的方块都是不透明的实心方块，
则视为完全被遮挡的不可见方块，在完成检测后统一移除

如果相邻存在半透明方块或者空气，则不会被标记处理

执行后会打印信息告知匹配到的方块名称和数量

- 指令格式

/removeInvisibleBlock x1 y1 z1 x2 y2 z2

- 参数含义

选择区域两端顶点的三维坐标, 例如(0, 64, 0)到(100, 128, 100)
对应指令 /removeInvisibleBlock 0 64 0 100 128 100

输入的坐标无所谓先后大小关系，只要确保在区域的对角方向即可
