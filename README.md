# [TyTool](https://github.com/luotengyuan/TyTool)

[![luotengyuan.com](https://img.shields.io/badge/%E6%8A%80%E6%9C%AF%E5%8D%9A%E5%AE%A2-Tamsiree-brightgreen.svg)](https://luotengyuan.com/)  [![TyTool](https://jitpack.io/v/luotengyuan/TyTool.svg)](https://jitpack.io/#luotengyuan/TyTool) [![](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)  [![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)  [![Gradle-5.6.4](https://img.shields.io/badge/Gradle-6.5-brightgreen.svg)](https://img.shields.io/badge/Gradle-6.5-brightgreen.svg)

<img src="https://lois-pictures.oss-cn-hangzhou.aliyuncs.com/picture/ty_tool_framework.png" alt="image" style="zoom: 33%;" />

>	TyTool是在开发Android过程中积累的一些工具集合，主要是为了自己在开发过程中避免一些重复工作，希望也能帮助到同样困扰的朋友。

---

# 我的运行环境

> Android Studio 4.1.1
>
> Windows 10 64bit
>
> ndkVersion 21.3.6528147
>
> targetSdkVersion 29
>
> minSdkVersion 16
>
> Android Gradle Plugin Version 4.1.1
>
> Gradle Version 6.5
>

---

# 如何使用它


## Step 1.先在 build.gradle(Project:XXXX) 的 repositories 添加:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

## Step 2.然后在 build.gradle(Module:app) 的 dependencies 添加:

```gradle
dependencies {
  //基础工具库
  implementation 'com.github.luotengyuan.TyTool:TyKit:1.0.6'
}
```

## Step 3.在Application中初始化

```java
TyTool.init(this);
```

# API使用文档

## 可以参考文档来调用相对应的API，欢迎指教


# 更新日志

|  VERSION  |  Description  |
| :-------: | ------------- |
| __1.0.3__ | 创建初始版本，包含基础工具集合和WebRtc、离线TTS以及串口工具库。 |
| __1.0.4__ | 增加本地Maven发布功能，新增TyTool类中开启日志和Crash接口。 |
| __1.0.5__ | 调整依赖结构和包名结构。 |
| __1.0.6__ | 增加TyExif库，包含Exif插入音频功能。 |

# Demo介绍


# License
