# FunnyTimeLine
Android基于自定义View实现的时间选择器（时间轴状），支持三级惯性滑动，内建时间管理类

# FunnyTimeLine

FunnyTimeLine是我第二个开源的自定义View *（第一个FunnyUnlockView见[这里](https://blog.csdn.net/qq_43596067/article/details/103650095)）* ，本质上是一个时间轴样式的时间选择器。

## 示例
![成品](https://github.com/FunnySaltyFish/FunnyTimeLine/raw/master/run.gif "")

*（因GIF压缩问题，此处展示的图片并不清晰且卡顿，建议您下载demo自行体验）*
## 特点
该View具有以下特点：

 - 三级惯性滑动：对 低中高 三种速度提供了三种惯性滑动，使用起来更加流畅自然
 - 动态生成图像：基于数据实时生成当前绘制图像（而不是全部绘制后区域加载）
 - 双缓冲绘图：基于内存画布+真实画布绘制，降低真实绘制次数，减少频闪

 
## 使用
### 引入包

 1. build.gradle添加implementa
 

```bash
maven { url "https://jitpack.io" }
```

 2. 引入依赖
 

```bash
implementation 'com.github.FunnySaltyFish:FunnyTimeLine:1.0.0'
```
### 使用FunnyTimeLine
支持Java代码和xml两种方式
|代码| 属性 | 解释 |
|--|--| -- |
| getTimeData() | 无 | 返回与View关联的TimeData（解释见下） |
| setTimeData(TimeData) | 无 | 设置与View关联的TimeData |
| getLightColor() | lightColor | 获取浅颜色（见图） |
| setLightColor(Color color) | lightColor | 设置浅颜色 |
| getNormalColor() | normalColor | 获取普通颜色（见图） |
| setNormalColor(Color color) | normalColor | 设置普通颜色 |
| getStressColor() | stressColor | 获取强调颜色（见图） |
| setStressColor(Color color) |stressColor | 设置强调颜色 |
| getShowNums() | showNums | 获取当前横轴一共显示多少个数字。（奇数） |
| setShowNums(int) |showNums | 设置显示数字 |
| getTimeKind() | timeKind | 获取当前更改的时间单位（0：秒，1：分，2：小时） |
| setTimeKind(short) |timeKind | 设置时间单位 |
| setFPS(int) |无| 设置View更新的理论帧率（实际帧率会小于该值） |
| setTimeMS(int) |time| 设置时间轴当前的时间 （以毫秒为单位）|

### 解释
#### TimeData

 - TimeData是FunnyTimeLine中管理时间的类，每个View持有一个TimeData，View读取它的数据显示
 - 内部有 *int h, int min, int s, int ms* 四个变量分别表示小时、分钟、秒、毫秒
 - 提供了一些用于操作时间的方法

#### 颜色图示![在这里插入图片描述](https://img-blog.csdnimg.cn/20200911233353459.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNTk2MDY3,size_16,color_FFFFFF,t_70#pic_center)
## 目录说明


 - app/ 为示例项目
 - library/ 为源代码

