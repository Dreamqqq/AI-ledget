# AI记账本 - Android 客户端

## 项目结构

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/jizhang/ledger/
│   │   │   ├── network/          # 网络请求层
│   │   │   │   ├── ApiService.java
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── RetrofitClient.java
│   │   │   │   └── AuthInterceptor.java
│   │   │   ├── model/            # 数据模型
│   │   │   │   ├── User.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── RegisterResponse.java
│   │   │   │   ├── TransactionListResponse.java
│   │   │   │   └── StatisticsResponse.java
│   │   │   ├── ui/               # UI 层
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── MainActivity.java
│   │   │   │   └── fragment/
│   │   │   │       ├── HomeFragment.java
│   │   │   │       ├── AddTransactionFragment.java
│   │   │   │       └── ProfileFragment.java
│   │   │   ├── utils/            # 工具类
│   │   │   │   └── TokenManager.java
│   │   │   └── LedgerApplication.java
│   │   ├── res/
│   │   │   ├── layout/           # 布局文件
│   │   │   ├── menu/             # 菜单文件
│   │   │   └── values/           # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## 功能说明

### 已实现功能 (Day 10)
- ✅ 用户登录
- ✅ 用户注册
- ✅ Token 持久化存储
- ✅ 底部导航栏（首页、记账、我的）
- ✅ 首页框架（显示收支统计）
- ✅ 我的页面（个人信息、统计数据、退出登录）

### 待实现功能 (Day 11-12)
- ⏳ 首页账单列表展示
- ⏳ 记账页面完整功能
- ⏳ 图片上传和识别功能

## 运行说明

1. 确保后端服务已启动（http://localhost:8084）
2. 使用 Android Studio 打开项目
3. 配置模拟器或连接真机
4. 点击运行

## API 地址配置

模拟器访问本机服务器使用特殊地址：
- `http://10.0.2.2:8084/api/` - Android 模拟器专用地址
- 如需真机测试，需修改 `RetrofitClient.java` 中的 BASE_URL 为实际 IP

## 技术栈

- Android SDK: API 26+
- Retrofit 2.9.0 - 网络请求
- OkHttp 4.11.0 - HTTP 客户端
- Gson - JSON 解析
- Material Design - UI 组件
- Glide 4.15.1 - 图片加载
