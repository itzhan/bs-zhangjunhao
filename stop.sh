#!/bin/bash
cd "$(dirname "$0")"
echo "🛑 停止所有服务..."

if [ -f .backend.pid ]; then
    kill $(cat .backend.pid) 2>/dev/null
    rm -f .backend.pid
    echo "✅ 后端已停止"
fi

if [ -f .frontend.pid ]; then
    kill $(cat .frontend.pid) 2>/dev/null
    rm -f .frontend.pid
    echo "✅ 前端已停止"
fi

if [ -f .admin.pid ]; then
    kill $(cat .admin.pid) 2>/dev/null
    rm -f .admin.pid
    echo "✅ 管理端已停止"
fi

# 兜底：查找可能残留的进程
lsof -ti:8080 | xargs kill -9 2>/dev/null
lsof -ti:5174 | xargs kill -9 2>/dev/null
lsof -ti:9528 | xargs kill -9 2>/dev/null

echo "🎉 所有服务已停止"
