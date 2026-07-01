-- ============================================
-- TaskForce 演示用 SQL 查询脚本
-- 演示前请先连接到 Oracle 数据库
-- ============================================

-- ============================================
-- 第一部分：查看数据库结构
-- ============================================

-- 1. 查看所有项目相关表
SELECT table_name FROM user_tables WHERE table_name LIKE '%_V6' ORDER BY 1;

-- 2. 查看所有序列（Sequence）
SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE '%_V6_SEQ' ORDER BY 1;


-- ============================================
-- 第二部分：用户和认证相关查询（演示重点！）
-- ============================================

-- 3. 查看所有用户（展示 BCrypt 加密密码！）
SELECT 
    id, 
    username, 
    password,  -- 这里是加密的密码 $2a$10$...
    role,
    created_at
FROM USERS_V6 
ORDER BY id;

-- 4. 查看特定用户
SELECT * FROM USERS_V6 WHERE username = 'admin';

-- 5. 查看刚注册的新用户
SELECT * FROM USERS_V6 WHERE username = 'student';


-- ============================================
-- 第三部分：项目和任务查询
-- ============================================

-- 6. 查看所有项目
SELECT 
    id, 
    project_name, 
    description, 
    manager_id, 
    created_at
FROM PROJECTS_V6 
ORDER BY id DESC;

-- 7. 查看所有任务
SELECT 
    id, 
    title, 
    description, 
    status, 
    priority, 
    project_id, 
    assigned_to_id, 
    created_at
FROM TASKS_V6 
ORDER BY id DESC;

-- 8. 查看刚才新建的任务（用title查找）
SELECT * FROM TASKS_V6 WHERE title = 'Write Final Report';

-- 9. 查看某个项目的所有任务
SELECT t.* 
FROM TASKS_V6 t
JOIN PROJECTS_V6 p ON t.project_id = p.id
WHERE p.project_name = 'Final Year Project';


-- ============================================
-- 第四部分：审计日志查询（演示重点！）
-- ============================================

-- 10. 查看所有任务变更日志（按时间倒序）
SELECT 
    id,
    task_id,
    old_status,
    new_status,
    changed_by_id,
    TO_CHAR(change_time, 'YYYY-MM-DD HH24:MI:SS') as change_time_formatted
FROM TASK_LOGS_V6 
ORDER BY id DESC;

-- 11. 查看某个任务的所有变更历史
SELECT 
    id,
    task_id,
    old_status,
    new_status,
    changed_by_id,
    TO_CHAR(change_time, 'YYYY-MM-DD HH24:MI:SS') as change_time_formatted
FROM TASK_LOGS_V6 
WHERE task_id = 1  -- 替换为实际的 task_id
ORDER BY id DESC;

-- 12. 查看某个用户的操作记录
SELECT 
    tl.*,
    u.username as changed_by_username
FROM TASK_LOGS_V6 tl
JOIN USERS_V6 u ON tl.changed_by_id = u.id
WHERE u.username = 'admin'  -- 或 'user'
ORDER BY tl.id DESC;


-- ============================================
-- 第五部分：关联查询（JOIN）
-- ============================================

-- 13. 查看任务详情（关联用户名和项目名）
SELECT 
    t.id,
    t.title,
    t.status,
    t.priority,
    p.project_name,
    u.username as assigned_to,
    TO_CHAR(t.created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at
FROM TASKS_V6 t
LEFT JOIN PROJECTS_V6 p ON t.project_id = p.id
LEFT JOIN USERS_V6 u ON t.assigned_to_id = u.id
ORDER BY t.id DESC;

-- 14. 查看完整的审计日志（包含用户名）
SELECT 
    tl.id,
    t.title as task_title,
    tl.old_status,
    tl.new_status,
    u.username as changed_by,
    TO_CHAR(tl.change_time, 'YYYY-MM-DD HH24:MI:SS') as change_time
FROM TASK_LOGS_V6 tl
JOIN TASKS_V6 t ON tl.task_id = t.id
JOIN USERS_V6 u ON tl.changed_by_id = u.id
ORDER BY tl.id DESC;


-- ============================================
-- 第六部分：验证操作结果（演示用）
-- ============================================

-- 15. 验证任务是否被删除
SELECT * FROM TASKS_V6 WHERE id = 1;  -- 替换为删除的任务ID，应该返回 0 行

-- 16. 验证状态变更是否生效
SELECT title, status FROM TASKS_V6 WHERE id = 1;  -- 替换为任务ID


-- ============================================
-- 第七部分：清理测试数据（演示后用）
-- ============================================

-- 注意：删除顺序很重要，要先删子表再删主表
-- 建议使用 DatabaseResetter 工具，而不是手动删除

-- 删除日志
-- DELETE FROM TASK_LOGS_V6;

-- 删除任务
-- DELETE FROM TASKS_V6;

-- 删除项目
-- DELETE FROM PROJECTS_V6;

-- 删除测试用户（保留 admin, user, test）
-- DELETE FROM USERS_V6 WHERE username = 'student';


-- ============================================
-- 演示时的快速查询快捷键
-- ============================================

-- 查询1：看用户和密码加密
SELECT id, username, password, role FROM USERS_V6 ORDER BY id;

-- 查询2：看审计日志（演示状态变更后立即执行）
SELECT id, task_id, old_status, new_status, changed_by_id, 
       TO_CHAR(change_time, 'YYYY-MM-DD HH24:MI:SS') as time
FROM TASK_LOGS_V6 ORDER BY id DESC;

-- 查询3：看任务状态
SELECT id, title, status FROM TASKS_V6 ORDER BY id DESC;
