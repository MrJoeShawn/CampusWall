
# 🎓 CampusWall - 校园社交平台

CampusWall 是一款基于 Spring Boot 的校园社交平台，专为高校学生设计，支持动态发布、评论点赞、好友互动和一对一实时聊天，打造一个轻量级且高效的校园社交环境。
![1](https://github.com/user-attachments/assets/4ec1f383-2f78-41b0-9399-fd6a0a8f18ab)
---

## 🧩 项目简介

CampusWall 提供了一个分享校园生活、交流互动的空间。用户可以发布图文动态，评论和点赞好友的动态，添加好友并查看好友动态，通过 WebSocket 实现实时一对一聊天。

后端采用 Spring Boot + MyBatis-Plus 构建 RESTful API，前端可对接 Vue 或 React 等主流框架。系统设计注重扩展性与安全性，适合校园社交类应用的开发与部署。

---

## ⚙️ 技术栈

| 类别    | 技术 / 工具库                      |
| ----- | ----------------------------- |
| 后端框架  | Spring Boot, MyBatis-Plus     |
| 安全认证  | JWT, Spring Security          |
| 数据缓存  | Redis                         |
| 实时通信  | WebSocket                     |
| 数据库   | MySQL                         |
| 工具库   | FastJSON, Knife4j, Aliyun SDK |
| 富文本编辑 | TinyMCE                       |
| 部署环境  | JDK 1.8, Maven, Nginx         |

---

## ✨ 核心功能

* ✅ 用户注册与登录（基于 JWT 的无状态认证）
* ✅ 动态发布与管理（支持多图上传）
* ✅ 评论、点赞、收藏等社交互动功能
* ✅ 好友关系管理（添加好友、查看好友动态）
* ✅ 基于 WebSocket 的实时一对一聊天
* ✅ 消息已读回执，提升交互体验
* ✅ Redis 缓存热点数据，提升系统性能
* ✅ AOP 实现统一日志记录与异常处理
* ✅ Knife4j 提供可视化接口文档

---

## 📦 项目目录结构

```
campus-wall/
├── CampusWall-admin/        # 后台管理模块
├── CampusWall-framework/    # 核心框架模块（工具类、配置、公共组件）
├── CampusWall-web/          # Web 接口模块（核心业务逻辑）
│   ├── src/main/java/       # Java 源码
│   └── src/main/resources/  # 配置文件
└── pom.xml                  # Maven 项目配置
```
