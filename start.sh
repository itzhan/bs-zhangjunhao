#!/bin/bash
cd "$(dirname "$0")"

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║       🏆 高校运动会管理系统 - 启动工具                  ║"
echo "╠══════════════════════════════════════════════════════╣"
echo "║  后端:   http://localhost:8080                       ║"
echo "║  前端:   http://localhost:5174                       ║"
echo "║  管理端: http://localhost:9528                       ║"
echo "╠══════════════════════════════════════════════════════╣"
echo "║  管理员: admin / admin123                            ║"
echo "║  用户:   zhangsan / 123456                           ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# 清理可能残留的端口占用
echo "🔍 清理端口..."
lsof -ti:8080 | xargs kill -9 2>/dev/null
lsof -ti:5174 | xargs kill -9 2>/dev/null
lsof -ti:9528 | xargs kill -9 2>/dev/null
sleep 1

# 检查 MySQL
echo "🔍 检查 MySQL..."
if command -v mysql &> /dev/null; then
    mysql -u root -pab123168 -e "SELECT 1" &>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ MySQL 连接成功"
        echo "📦 初始化数据库..."
        mysql -u root -pab123168 -e "CREATE DATABASE IF NOT EXISTS sports_meeting DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
        mysql -u root -pab123168 sports_meeting < sql/init.sql 2>/dev/null
        mysql -u root -pab123168 sports_meeting < sql/data.sql 2>/dev/null
        echo "✅ 数据库初始化完成"
    else
        echo "⚠️  MySQL 连接失败，请检查密码配置"
    fi
else
    echo "⚠️  未找到 mysql 命令，请手动初始化数据库"
fi

echo ""

# 启动后端（每次重新打包，避免复用旧 jar）
echo "🚀 编译并启动后端 (port 8080)..."
echo "   正在打包最新后端代码..."
cd backend && ./mvnw package -DskipTests -q && cd ..
nohup java -jar backend/target/sports-meeting-0.0.1-SNAPSHOT.jar > /tmp/sports_backend.log 2>&1 &
echo $! > .backend.pid
echo "   后端 PID: $(cat .backend.pid)"

# 等待后端就绪
echo "   等待后端启动..."
for i in $(seq 1 15); do
    sleep 2
    if curl -s 'http://localhost:8080/api/events?page=1&size=1' 2>/dev/null | grep -q '"code":200'; then
        echo "   ✅ 后端就绪 ($((i*2))秒)"
        break
    fi
done

# 启动用户端前端
echo "🚀 启动用户端前端 (port 5174)..."
cd frontend
nohup npm run dev > /tmp/sports_frontend.log 2>&1 &
echo $! > ../.frontend.pid
echo "   前端 PID: $(cat ../.frontend.pid)"
cd ..

# 启动管理端
echo "🚀 启动管理端 (port 9528)..."
cd admin
nohup pnpm dev > /tmp/sports_admin.log 2>&1 &
echo $! > ../.admin.pid
echo "   管理端 PID: $(cat ../.admin.pid)"
cd ..

echo ""
echo "✅ 所有服务已启动！"
echo "   停止所有服务: ./stop.sh"
echo ""
echo "📋 实时日志输出（Ctrl+C 退出日志，服务不会停止）"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Ctrl+C 只退出 tail，不杀服务
trap 'echo ""; echo "📋 日志已停止，服务仍在运行。停止服务请执行: ./stop.sh"; exit 0' INT

tail -f /tmp/sports_backend.log /tmp/sports_frontend.log /tmp/sports_admin.log
