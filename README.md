# AI 记账本 (AI Ledger)

一款基于 Spring Boot 开发的智能记账应用，集成 AI 识别功能，支持票据自动识别和分类。

## 📋 项目简介

AI 记账本是一个全栈记账解决方案，提供后端 API 服务和 Android 移动端应用。通过集成 OpenAI 视觉识别能力，用户可以通过拍照自动识别票据信息，实现快速记账。

### 核心特性

- 🔐 **用户认证** - 基于 JWT 的安全认证机制
- 📝 **账单管理** - 支持收入/支出记录的完整 CRUD 操作
- 🤖 **AI 识别** - 集成 OpenAI GPT-4 Vision，自动识别票据内容
- 📊 **统计分析** - 按时间、类目统计收支情况
- 📷 **图片上传** - 集成阿里云 OSS 对象存储
- 📱 **移动端支持** - 配套 Android 客户端应用
- 📚 **操作日志** - 完整的用户操作追踪记录
- 🏷️ **类目管理** - 灵活的收支类目分类

## 🛠️ 技术栈

### 后端技术

- **框架**: Spring Boot 2.2.6
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **安全**: Spring Security Crypto + JWT
- **缓存**: Ehcache
- **连接池**: HikariCP
- **API 文档**: Swagger 3.0
- **对象存储**: 阿里云 OSS
- **AI 服务**: OpenAI GPT-4 Vision

### 前端技术

- **移动端**: Android (Java)
- **网络请求**: Retrofit + OkHttp
- **UI**: Material Design

## 📦 项目结构

```
AI-ledget/
├── src/main/java/com/jizhang/
│   ├── config/          # 配置类（Swagger、Web、跨域等）
│   ├── controller/      # 控制器层
│   │   ├── AuthController.java         # 认证接口
│   │   ├── UserController.java         # 用户管理
│   │   ├── TransactionController.java  # 账单管理
│   │   ├── CategoryController.java     # 类目管理
│   │   ├── UploadController.java       # 文件上传
│   │   └── HealthController.java       # 健康检查
│   ├── service/         # 服务层
│   │   ├── UserService.java            # 用户服务
│   │   ├── TransactionService.java     # 账单服务
│   │   ├── OcrService.java             # OCR 识别服务
│   │   ├── OssService.java             # OSS 上传服务
│   │   └── OperationLogService.java    # 操作日志服务
│   ├── repository/      # 数据访问层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── enums/           # 枚举类
│   ├── utils/           # 工具类（JWT、JSON等）
│   └── exception/       # 异常处理
├── src/main/resources/
│   ├── application.yml           # 主配置文件
│   ├── application-local.yml     # 本地环境配置
│   └── config/ehcache.xml        # 缓存配置
├── android/             # Android 客户端
│   ├── app/
│   │   └── src/main/java/com/jizhang/ledger/
│   │       ├── ui/              # UI 界面
│   │       ├── network/         # 网络请求
│   │       ├── model/           # 数据模型
│   │       └── utils/           # 工具类
├── database.sql         # 数据库初始化脚本
├── pom.xml             # Maven 依赖配置
└── README.md           # 项目文档
```

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis（可选）
- 阿里云 OSS 账号（用于图片存储）
- OpenAI API Key（用于 OCR 识别）

### 安装步骤

1. **克隆项目**

```bash
git clone <repository-url>
cd AI-ledget
```

2. **创建数据库**

```bash
mysql -u root -p < database.sql
```

3. **配置文件**

编辑 `src/main/resources/application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jizhang
    username: your_username
    password: your_password

jwt:
  secret: your-jwt-secret-key

oss:
  endpoint: your-oss-endpoint
  accessKeyId: your-access-key-id
  accessKeySecret: your-access-key-secret
  bucketName: your-bucket-name

openai:
  apiKey: your-openai-api-key
```

4. **编译运行**

```bash
# 编译项目
mvn clean package

# 运行项目
java -jar target/jizhang-api-0.0.1-SNAPSHOT.jar

# 或使用 Maven 直接运行
mvn spring-boot:run
```

5. **访问接口**

- API 地址: `http://localhost:8084`
- Swagger 文档: `http://localhost:8084/swagger-ui/`
- 健康检查: `http://localhost:8084/health`

## 📖 API 文档

### 认证接口

- `POST /auth/register` - 用户注册
- `POST /auth/login` - 用户登录

### 用户接口

- `GET /user/profile` - 获取用户信息
- `PUT /user/profile` - 更新用户信息

### 账单接口

- `GET /transactions` - 获取账单列表
- `POST /transactions` - 创建账单
- `PUT /transactions/{id}` - 更新账单
- `DELETE /transactions/{id}` - 删除账单
- `GET /transactions/statistics` - 获取统计数据

### 上传接口

- `POST /upload/image` - 上传图片
- `POST /upload/ocr` - OCR 识别票据

### 类目接口

- `GET /categories` - 获取所有类目

详细 API 文档请访问 Swagger UI。


## 📱 Android 客户端

Android 客户端位于 `android/` 目录，使用 Android Studio 打开即可运行。

### 主要功能

- 用户注册/登录
- 添加/编辑/删除账单
- 拍照上传票据
- 账单列表查看
- 统计图表展示
- 个人资料管理

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证。

## 📧 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至项目维护者

---

**注意**: 本项目仅供学习交流使用，请勿用于商业用途。使用 OpenAI API 和阿里云 OSS 将产生相应费用，请注意成本控制。
