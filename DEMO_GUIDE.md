# 🎯 TaskForce 演示工作流指南

**给老师演示用的完整步骤**

---

## 📋 演示准备清单

### 演示前确认

- [ ] 应用已编译并能正常启动
- [ ] 数据库连接配置正确（Oracle）
- [ ] Oracle SQL Developer / DBeaver 已打开并连接好数据库
- [ ] 浏览器已打开（Chrome/Edge）
- [ ] 如果需要演示重置功能：先备份当前数据库数据

---

## 🚀 完整演示工作流（建议 15-20 分钟）

---

### 第一阶段：项目介绍 & 启动（2分钟）

**台词示例**：
> "老师好，这是我们小组的项目 TaskForce，一个企业级任务管理系统。它使用 Spring Boot 3.x 框架，连接 Oracle 数据库，完全按照 MVC 架构设计，具有完整的 CRUD 功能和角色权限控制。"

**操作步骤**：
1. 打开 IntelliJ IDEA，展示项目结构
2. 运行 `TaskForceApplication.java`
3. 等待应用启动完成（看到 "Started TaskForceApplication"）

---

### 第二阶段：数据库验证（3分钟）

**演示目标**：展示 Oracle 数据库连接、序列、表结构

#### 步骤 1：查看数据库表结构

**操作步骤**：
1. 切换到 Oracle SQL Developer / DBeaver
2. 执行 SQL 查看所有表：
   ```sql
   SELECT table_name FROM user_tables WHERE table_name LIKE '%_V6' ORDER BY 1;
   ```
3. 预期结果：看到 4 张表
   - `USERS_V6`
   - `PROJECTS_V6`
   - `TASKS_V6`
   - `TASK_LOGS_V6`

#### 步骤 2：查看序列（Sequence）

**操作步骤**：
1. 执行 SQL 查看序列：
   ```sql
   SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE '%_V6_SEQ';
   ```
2. 预期结果：看到 4 个序列
   - `USERS_V6_SEQ`
   - `PROJECTS_V6_SEQ`
   - `TASKS_V6_SEQ`
   - `TASK_LOGS_V6_SEQ`

---

### 第三阶段：用户注册 & 登录演示（4分钟）

#### 步骤 1：查看演示数据（初始数据库）

**操作步骤**：
1. 执行 SQL 查看用户表数据：
   ```sql
   SELECT id, username, password, role FROM USERS_V6 ORDER BY id;
   ```
2. **展示给老师看**：
   - 指出 `password` 字段是 BCrypt 加密的（格式类似 `$2a$10$...`）
   - 指出 `role` 字段有 `ROLE_ADMIN` 和 `ROLE_EMPLOYEE`

#### 步骤 2：新用户注册演示

**操作步骤**：
1. 浏览器打开：`http://localhost:8080/register`
2. 填写注册表单：
   - Username: `student`
   - Password: `Password123`
   - Confirm Password: `Password123`
3. 点击 **Register** 按钮
4. 看到成功提示后，跳转到登录页面
5. **切换到数据库**，立即执行 SQL 验证：
   ```sql
   SELECT id, username, password, role FROM USERS_V6 WHERE username = 'student';
   ```
6. **展示给老师看**：新用户已创建，密码已经加密！

#### 步骤 3：登录演示（Admin 角色）

**操作步骤**：
1. 登录页面
2. 输入：
   - Username: `admin`
   - Password: `Password123`
3. 点击 **Login**
4. 成功登录进入 Dashboard
5. （可选）展示多语言切换功能，点击右上角 EN/MS/中

---

### 第四阶段：CRUD 功能演示（6分钟）

#### 演示 A：Project CRUD（Admin 权限）

**操作步骤**：
1. 点击导航栏 **Projects** 按钮
2. 点击 **New Project** 按钮
3. 填写表单：
   - Project Name: `Final Year Project`
   - Description: `CSC3402 Final Project Work`
   - Manager: 选择 `admin`
4. 点击 **Save Project**
5. **切换到数据库**，验证 Project 已创建：
   ```sql
   SELECT * FROM PROJECTS_V6 WHERE project_name = 'Final Year Project';
   ```

#### 演示 B：Task CRUD（Admin 权限）

**操作步骤**：
1. 返回 Dashboard，点击 **New Task** 按钮
2. 填写任务表单：
   - Title: `Write Final Report`
   - Description: `Complete the final project report`
   - Priority: `HIGH`
   - Project: `Final Year Project`
   - Assigned to: `user`
3. 点击 **Save Task**
4. **切换到数据库**，验证 Task 已创建：
   ```sql
   SELECT id, title, status, priority, project_id, assigned_to_id FROM TASKS_V6 WHERE title = 'Write Final Report';
   ```

#### 演示 C：Task 详情查看

**操作步骤**：
1. 在 Dashboard 点击任务标题 `Write Final Report`
2. 展示任务详情页面
3. 可以看到任务信息和历史记录（初始为空）

---

### 第五阶段：状态变更 & 审计日志（4分钟）

**这是演示重点！展示 Transactional 和 TaskLog**

#### 步骤 1：更新任务状态（Admin）

**操作步骤**：
1. 返回 Dashboard，找到刚才创建的任务 `Write Final Report`
2. 状态下拉框选择 `IN_PROGRESS`
3. 点击 **Update** 按钮
4. 看到状态标签变为蓝色（IN_PROGRESS）

#### 步骤 2：数据库验证（重要！）

**操作步骤**：
1. **立即切换到数据库**，执行两个查询：

**查询 1：检查 TASKS_V6 表状态已更新**
```sql
SELECT id, title, status FROM TASKS_V6 WHERE title = 'Write Final Report';
```

**查询 2：检查 TASK_LOGS_V6 审计日志（关键演示！）**
```sql
SELECT id, task_id, old_status, new_status, changed_by_id, change_time
FROM TASK_LOGS_V6
ORDER BY id DESC;
```

**展示给老师看**：
- `old_status` 是 `TODO`
- `new_status` 是 `IN_PROGRESS`
- `change_time` 是刚才的时间戳
- 说明在一个事务中同时更新了两张表！

#### 步骤 3：再次更新状态（Employee 角色）

**操作步骤**：
1. 先 Logout
2. 使用 Employee 账户登录：
   - Username: `user`
   - Password: `Password123`
3. 找到任务 `Write Final Report`
4. 状态下拉框选择 `DONE`
5. 点击 **Update** 按钮

#### 步骤 4：再次验证数据库

**操作步骤**：
```sql
SELECT id, task_id, old_status, new_status, changed_by_id, change_time
FROM TASK_LOGS_V6
ORDER BY id DESC;
```

**展示给老师看**：现在有两条日志了！第二次变更的 `changed_by` 是 Employee 用户！

---

### 第六阶段：权限控制演示（3分钟）

**演示目标**：展示 RBAC（基于角色的访问控制）

#### 步骤 1：Employee 看到禁用按钮

**操作步骤**：
1. 保持以 `user`（Employee）身份登录
2. 在 Dashboard 展示给老师看：
   - **Edit** 按钮是灰色的，点击弹出权限拒绝提示
   - **Delete** 按钮是灰色的，点击弹出权限拒绝提示
   - 没有 **Projects** 导航栏按钮
   - （如果有）没有 **New Task** 按钮

#### 步骤 2：尝试直接访问 Project 页面

**操作步骤**：
1. 浏览器地址栏直接输入：`http://localhost:8080/projects`
2. 预期看到 **403 Forbidden** 错误页面
3. 展示给老师看：权限控制生效了！

#### 步骤 3：Admin 删除功能

**操作步骤**：
1. Logout，用 `admin` 重新登录
2. 在 Dashboard 找到任务 `Write Final Report`
3. 点击 **Delete** 按钮
4. 确认删除

#### 步骤 4：数据库验证删除

**操作步骤**：
```sql
SELECT * FROM TASKS_V6 WHERE title = 'Write Final Report';
```
**展示给老师看**：查询结果为空（0行），数据已从数据库物理删除！

---

### 第七阶段：项目管理功能（2分钟）

**操作步骤**：
1. Admin 身份登录
2. 点击 **Projects** 导航栏
3. 展示项目列表
4. 点击删除某个项目
5. 验证级联删除（可选）

---

## 🎨 UI 特色展示（可选）

### 1. 状态和优先级的颜色
- `TODO` - 黄色
- `IN_PROGRESS` - 蓝色
- `DONE` - 绿色
- `HIGH` - 红色
- `MEDIUM` - 黄色
- `LOW` - 绿色

### 2. 固定布局
- Status 列不会因为状态文字长度变化而跳动
- 展示状态下拉框和 Update 按钮位置固定

### 3. 多语言切换
- 右上角 EN → MS → 中，来回切换展示

---

## 📊 数据库查询语句汇总

为了演示方便，准备好以下 SQL 随时复制粘贴：

```sql
-- 查看所有用户和加密密码
SELECT id, username, password, role FROM USERS_V6 ORDER BY id;

-- 查看所有项目
SELECT * FROM PROJECTS_V6 ORDER BY id;

-- 查看所有任务（含状态和优先级）
SELECT id, title, status, priority, project_id, assigned_to_id FROM TASKS_V6 ORDER BY id;

-- 查看任务审计日志（按时间倒序，最新的在前）
SELECT id, task_id, old_status, new_status, changed_by_id, change_time
FROM TASK_LOGS_V6
ORDER BY id DESC;

-- 查看某个任务的所有日志
SELECT * FROM TASK_LOGS_V6 WHERE task_id = <替换为实际的任务ID>;

-- 清理刚才演示创建的测试数据（可选）
DELETE FROM TASK_LOGS_V6;
DELETE FROM TASKS_V6;
DELETE FROM PROJECTS_V6;
DELETE FROM USERS_V6 WHERE username = 'student';
```

---

## 🎯 演示重点提醒

### 必须展示的核心功能：

1. ✅ **密码加密** - 展示 USERS_V6 表中 password 字段是 BCrypt 格式
2. ✅ **审计日志** - 展示 TASK_LOGS_V6 表记录了每次状态变更
3. ✅ **事务一致性** - 状态更新和日志记录同时发生
4. ✅ **权限控制** - Employee 看不到/用不了某些功能
5. ✅ **完整 CRUD** - Create, Read, Update, Delete 都要有
6. ✅ **Oracle 集成** - 使用 Oracle Sequence，不是自增 ID

---

## 🔄 如果需要演示重置数据库

**可选操作（演示前或演示后）**：

**方式 1：使用 DatabaseResetter 工具（推荐）**
1. 应用启动后，在控制台输入 `RESET` 按回车
2. 确认后，所有表被清空
3. 重启应用，DemoDataInitializer 自动重新创建演示数据

**方式 2：手动 SQL**
```sql
-- 删除顺序很重要！
DROP TABLE TASK_LOGS_V6 CASCADE CONSTRAINTS;
DROP TABLE TASKS_V6 CASCADE CONSTRAINTS;
DROP TABLE PROJECTS_V6 CASCADE CONSTRAINTS;
DROP TABLE USERS_V6 CASCADE CONSTRAINTS;
DROP SEQUENCE USERS_V6_SEQ;
DROP SEQUENCE PROJECTS_V6_SEQ;
DROP SEQUENCE TASKS_V6_SEQ;
DROP SEQUENCE TASK_LOGS_V6_SEQ;
```
然后重启应用，JPA 自动重新建表

---

## 💡 演示小技巧

1. **提前准备好 SQL** - 把查询语句保存到文件里，直接复制
2. **分屏显示** - 一边是浏览器，一边是数据库工具
3. **语速适中** - 一边操作一边解释
4. **强调关键点** - 比如"这里使用了 @Transactional，保证数据一致性"
5. **不要慌** - 如果出错，可以解释，或者用 DatabaseResetter 重置后再来

---

## 📝 演示总结台词

演示结束时可以说：

> "以上就是我们 TaskForce 项目的完整功能演示。总结一下：
> 
> 1. **安全性**：密码 BCrypt 加密，角色权限控制
> 2. **数据完整性**：使用 @Transactional 保证事务，审计日志完整
> 3. **架构规范**：严格遵循 MVC 架构
> 4. **技术栈**：Spring Boot + Oracle + Thymeleaf + Bootstrap
> 5. **用户体验**：多语言支持，响应式界面
> 
> 谢谢老师！"

---

祝演示顺利！🎉
