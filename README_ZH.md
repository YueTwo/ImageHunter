# 映像猎手（ImageHunter）

[![English README](https://img.shields.io/badge/Language-English-2463eb?style=flat-square)](README.md)

手机让记录生活变得轻而易举，也让照片和视频在不知不觉间汇成一座庞大的个人记忆库。那些没有被命名的片段，常常因为难以翻找而沉入时间深处。映像猎手希望让每一段值得珍藏的影像，都能在需要时被重新看见。

面向图片与视频，系统提供独立的视觉描述生成与自然语言检索能力：前者将媒体中的视觉信息转化为文字，后者直接将用户的查询与媒体内容匹配，无需事先标注或生成描述。映像猎手支持 Android 与 Web 双端访问，通过同步的个人账号、云端存储与跨设备管理，陪伴用户整理、保存并重访自己的数字记忆。

## 获奖荣誉

### 省级荣誉

**2023 年第五届全球校园人工智能算法精英大赛·精英 I 赛区选拔赛（省级）二等奖**

<img src="figures/rewards/第五届全球校园人工智能算法精英大赛省级二等奖.png" alt="省级二等奖获奖证书" width="46%">

### 国家级荣誉

**2023 年第五届全球校园人工智能算法精英大赛·全国总决赛二等奖**

<img src="figures/rewards/第五届全球校园人工智能算法精英大赛全国二等奖.png" alt="全国二等奖获奖证书" width="46%">

## 系统总览

<img src="figures/framework.png" alt="映像猎手系统架构：Android 与 Web 客户端经由 Nginx 和 CMS 连接模型处理、对象存储与数据库" width="100%">

### 映像猎手运行流程

1. 用户在 Android 或 Web 端登录，获得具有时效性的身份验证令牌。
2. 对于上传、检索或描述生成请求，Nginx 验证令牌后，将已授权请求路由至 CMS。
3. CMS 协调对象存储和数据库更新；需要进行媒体分析时，调用模型服务。
4. Web 服务器将处理结果返回客户端。描述生成与检索是相互独立的模型能力。

## 移动端

移动端是上传和管理媒体、生成描述与检索内容的主要使用入口。

### 首页与上传

首页提供醒目的上传入口，并可快速进入映像描述与映像检索功能。用户可从设备中上传图片或视频。

<img src="figures/mobile/main_interface.png" alt="移动端启动页与首页" width="72%">

### 自然语言检索

输入对内容的文字描述后，选择图片或视频、本地或云端计算，并可限定时间范围。检索能力会独立地将查询与媒体内容匹配，并按置信度返回结果。

<img src="figures/mobile/search_interface.png" alt="移动端检索流程与结果" width="100%">

### 映像描述生成

拍摄或选择图片、视频，选择媒体类型后即可一键生成文字描述。

<img src="figures/mobile/generate_interface.png" alt="移动端映像描述生成" width="72%">

### 个人空间

个人中心集中展示每日签到、云空间、隐私说明及账户管理等内容。

<img src="figures/mobile/user_interface.png" alt="移动端个人空间" width="38%">

## Web 端

Web 端以精简的桌面体验补充移动端工作流。

### 首页

产品入口与导航。

<img src="figures/web/main_page.png" alt="Web 首页" width="72%">

### 检索

文本检索及结果。

<img src="figures/web/search_page.png" alt="Web 检索页" width="72%">

### 生成

按媒体类型上传。

<img src="figures/web/generate_page.png" alt="Web 生成页" width="72%">

### 个人空间

浏览检索历史。

<img src="figures/web/user_home.png" alt="Web 个人空间" width="72%">

---

[Read the English version →](README.md)
