@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo.
echo ╔══════════════════════════════════════════════════════╗
echo ║       🏆 高校运动会管理系统 - 启动工具                  ║
echo ╠══════════════════════════════════════════════════════╣
echo ║  后端:   http://localhost:8080                       ║
echo ║  前端:   http://localhost:5173                       ║
echo ║  管理端: http://localhost:9527                       ║
echo ╠══════════════════════════════════════════════════════╣
echo ║  管理员: admin / admin123                            ║
echo ║  用户:   zhangsan / 123456                           ║
echo ╚══════════════════════════════════════════════════════╝
echo.

echo 📦 初始化数据库...
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS sports_meeting DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
mysql -u root -proot sports_meeting < sql\init.sql 2>nul
mysql -u root -proot sports_meeting < sql\data.sql 2>nul
echo ✅ 数据库初始化完成

echo.
echo 🚀 启动后端服务...
cd backend
start "Sports Backend" cmd /c "mvnw.cmd spring-boot:run -q"
cd ..

echo 🚀 启动用户端前端...
cd frontend
start "Sports Frontend" cmd /c "npm run dev"
cd ..

echo 🚀 启动管理端前端...
cd admin
start "Sports Admin" cmd /c "pnpm dev"
cd ..

echo.
echo ✅ 所有服务已启动！
echo    关闭此窗口不会停止服务，请使用 stop.bat 停止
pause
