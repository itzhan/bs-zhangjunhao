#!/bin/bash
# 高校运动会管理系统 - 全接口联调测试脚本
# 覆盖所有用户端+管理端接口（含CRUD全流程）
# set -e removed to allow all tests to run
BASE="http://localhost:8080/api"
PASS=0; FAIL=0; TOTAL=0
TIMESTAMP=$(date +%s)
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'

test_api() {
  TOTAL=$((TOTAL+1))
  local method="$1" url="$2" desc="$3" data="$4" token="$5" expect="${6:-200}"
  local args=(-s -o /tmp/resp.json -w "%{http_code}" -X "$method" "$BASE$url" -H "Content-Type: application/json")
  [ -n "$token" ] && args+=(-H "Authorization: Bearer $token")
  [ -n "$data" ] && args+=(-d "$data")
  local code; code=$(curl "${args[@]}" 2>/dev/null)
  local body; body=$(cat /tmp/resp.json 2>/dev/null)
  local bcode; bcode=$(echo "$body" | python3 -c "import sys,json;print(json.load(sys.stdin).get('code','?'))" 2>/dev/null || echo "?")
  if [ "$code" = "$expect" ] && ([ "$bcode" = "200" ] || [ "$bcode" = "?" ]); then
    PASS=$((PASS+1)); printf "${GREEN}✅ [%02d] %-6s %-45s ${NC}HTTP=%s code=%s\n" "$TOTAL" "$method" "$desc" "$code" "$bcode"
  else
    FAIL=$((FAIL+1)); printf "${RED}❌ [%02d] %-6s %-45s ${NC}HTTP=%s code=%s\n" "$TOTAL" "$method" "$desc" "$code" "$bcode"
    echo "   Response: $(echo "$body" | head -c 200)"
  fi
}

# 提取JSON字段
json_field() { echo "$1" | python3 -c "import sys,json;print(json.load(sys.stdin)$2)" 2>/dev/null; }

echo ""
echo "╔═══════════════════════════════════════════════════════╗"
echo "║  🏆 高校运动会管理系统 - 全接口联调测试                   ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

# ============ 1. AUTH ============
echo "${YELLOW}━━━ 认证模块 ━━━${NC}"
ADMIN_RESP=$(curl -s -X POST "$BASE/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}')
ADMIN_TOKEN=$(json_field "$ADMIN_RESP" ".get('data',{}).get('token','')")
if [ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "" ]; then
  TOTAL=$((TOTAL+1)); PASS=$((PASS+1)); printf "${GREEN}✅ [%02d] %-6s %-45s ${NC}token=%s...\n" "$TOTAL" "POST" "管理员登录" "${ADMIN_TOKEN:0:20}"
else
  TOTAL=$((TOTAL+1)); FAIL=$((FAIL+1)); printf "${RED}❌ [%02d] %-6s %-45s ${NC}%s\n" "$TOTAL" "POST" "管理员登录" "$ADMIN_RESP"
  echo "管理员登录失败，终止测试"; exit 1
fi

USER_RESP=$(curl -s -X POST "$BASE/auth/login" -H "Content-Type: application/json" -d '{"username":"zhangsan","password":"123456"}')
USER_TOKEN=$(json_field "$USER_RESP" ".get('data',{}).get('token','')")
if [ -n "$USER_TOKEN" ] && [ "$USER_TOKEN" != "" ]; then
  TOTAL=$((TOTAL+1)); PASS=$((PASS+1)); printf "${GREEN}✅ [%02d] %-6s %-45s ${NC}token=%s...\n" "$TOTAL" "POST" "用户登录 (zhangsan)" "${USER_TOKEN:0:20}"
else
  TOTAL=$((TOTAL+1)); FAIL=$((FAIL+1)); printf "${RED}❌ [%02d] %-6s %-45s ${NC}%s\n" "$TOTAL" "POST" "用户登录 (zhangsan)" "$USER_RESP"
fi

# 注册测试
test_api POST "/auth/register" "用户注册" "{\"username\":\"testuser${TIMESTAMP}\",\"password\":\"test123456\",\"realName\":\"测试用户\",\"gender\":1,\"departmentId\":1,\"className\":\"测试班级\",\"studentNo\":\"99${TIMESTAMP}\"}" ""

# ============ 2. 用户端接口 ============
echo ""; echo "${YELLOW}━━━ 用户端 - 用户 ━━━${NC}"
test_api GET "/users/profile" "获取用户资料" "" "$USER_TOKEN"
test_api PUT "/users/profile" "更新用户资料" '{"realName":"张明辉","phone":"13800010001"}' "$USER_TOKEN"
test_api PUT "/users/password" "修改密码" '{"oldPassword":"123456","newPassword":"123456"}' "$USER_TOKEN"

echo ""; echo "${YELLOW}━━━ 用户端 - 赛事 ━━━${NC}"
test_api GET "/events?page=1&size=10" "赛事列表" "" ""
test_api GET "/events/1" "赛事详情(ID=1)" "" ""
test_api GET "/events/current" "当前赛事" "" ""

echo ""; echo "${YELLOW}━━━ 用户端 - 运动项目 ━━━${NC}"
test_api GET "/sports?eventId=1&page=1&size=20" "项目列表(赛事1)" "" ""
test_api GET "/sports/2" "项目详情(ID=2)" "" ""

echo ""; echo "${YELLOW}━━━ 用户端 - 报名 ━━━${NC}"
test_api GET "/registrations/my" "我的报名" "" "$USER_TOKEN"
test_api GET "/departments" "院系列表" "" ""
test_api GET "/departments/medal-table?eventId=1" "奖牌榜" "" ""

echo ""; echo "${YELLOW}━━━ 用户端 - 赛程/成绩/奖项/公告 ━━━${NC}"
test_api GET "/schedules/my" "我的赛程" "" "$USER_TOKEN"
test_api GET "/results/my" "我的成绩" "" "$USER_TOKEN"
test_api GET "/results/ranking?eventId=1&sportId=1" "排行榜" "" ""
test_api GET "/awards/my" "我的奖项" "" "$USER_TOKEN"
test_api GET "/awards?eventId=1" "奖项列表" "" "$USER_TOKEN"
test_api GET "/announcements?page=1&size=10" "公告列表" "" ""
test_api GET "/announcements/1" "公告详情" "" ""

# ============ 3. 管理端接口 - 查询类 ============
echo ""; echo "${YELLOW}━━━ 管理端 - 仪表盘 ━━━${NC}"
test_api GET "/admin/dashboard" "仪表盘数据" "" "$ADMIN_TOKEN"
test_api GET "/admin/statistics/medal-table?eventId=1" "奖牌榜统计" "" "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 用户管理 ━━━${NC}"
test_api GET "/admin/users?page=1&size=10" "用户列表" "" "$ADMIN_TOKEN"
test_api GET "/admin/users?page=1&size=5&keyword=zhang" "用户搜索" "" "$ADMIN_TOKEN"
test_api GET "/admin/users?page=1&size=5&role=ADMIN" "角色筛选" "" "$ADMIN_TOKEN"
test_api PUT "/admin/users/2" "编辑用户" '{"realName":"张明辉","phone":"13800010001","status":1}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 赛事管理 ━━━${NC}"
test_api GET "/admin/events?page=1&size=10" "赛事列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/events" "新增赛事" "{\"name\":\"APITEST${TIMESTAMP}\",\"startDate\":\"2026-06-01\",\"endDate\":\"2026-06-02\",\"location\":\"测试场地\",\"status\":0}" "$ADMIN_TOKEN"
# 获取新赛事ID
NEW_EVENT_RESP=$(curl -s "$BASE/admin/events?page=1&size=10" -H "Authorization: Bearer $ADMIN_TOKEN")
NEW_EVENT_ID=$(echo "$NEW_EVENT_RESP" | python3 -c "import sys,json;rs=json.load(sys.stdin).get('data',{}).get('records',[]);print(next((r['id'] for r in rs if 'APITEST${TIMESTAMP}' in r.get('name','')),0))" 2>/dev/null)
echo "  [DEBUG] NEW_EVENT_ID=$NEW_EVENT_ID"
test_api PUT "/admin/events/$NEW_EVENT_ID" "编辑赛事" "{\"name\":\"APITEST${TIMESTAMP}(updated)\",\"startDate\":\"2026-06-01\",\"endDate\":\"2026-06-02\",\"location\":\"测试场地\",\"status\":0}" "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 运动项目 ━━━${NC}"
test_api GET "/admin/sports?page=1&size=10&eventId=1" "项目列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/sports" "新增项目" "{\"eventId\":$NEW_EVENT_ID,\"name\":\"测试5000米\",\"category\":\"径赛\",\"genderLimit\":1,\"unit\":\"秒\",\"sortType\":1}" "$ADMIN_TOKEN"
NEW_SPORT_RESP=$(curl -s "$BASE/admin/sports?page=1&size=10&eventId=$NEW_EVENT_ID" -H "Authorization: Bearer $ADMIN_TOKEN")
NEW_SPORT_ID=$(json_field "$NEW_SPORT_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")
test_api PUT "/admin/sports/$NEW_SPORT_ID" "编辑项目" '{"name":"测试5000米(已更新)","maxParticipants":32}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 报名管理 ━━━${NC}"
test_api GET "/admin/registrations?page=1&size=10&eventId=1" "报名列表" "" "$ADMIN_TOKEN"
test_api GET "/admin/registrations?page=1&size=5&status=0" "待审核报名" "" "$ADMIN_TOKEN"
# 审核操作(使用现有待审核记录)
PENDING_RESP=$(curl -s "$BASE/admin/registrations?page=1&size=1&status=0" -H "Authorization: Bearer $ADMIN_TOKEN")
PENDING_ID=$(json_field "$PENDING_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")
if [ "$PENDING_ID" != "0" ] && [ -n "$PENDING_ID" ]; then
  test_api PUT "/admin/registrations/$PENDING_ID/approve" "审核通过报名" "" "$ADMIN_TOKEN"
fi
# 批量审核
test_api PUT "/admin/registrations/batch-approve" "批量审核" '{"ids":[]}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 赛程管理 ━━━${NC}"
test_api GET "/admin/schedules?page=1&size=10&eventId=1" "赛程列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/schedules" "新增赛程" "{\"sportId\":$NEW_SPORT_ID,\"eventId\":$NEW_EVENT_ID,\"roundName\":\"预赛\",\"matchDate\":\"2026-06-01\",\"startTime\":\"08:00:00\",\"endTime\":\"09:00:00\",\"venue\":\"测试场地\",\"groupNo\":1,\"status\":0}" "$ADMIN_TOKEN"
NEW_SCHED_RESP=$(curl -s "$BASE/admin/schedules?page=1&size=10&eventId=$NEW_EVENT_ID" -H "Authorization: Bearer $ADMIN_TOKEN")
NEW_SCHED_ID=$(json_field "$NEW_SCHED_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")
test_api PUT "/admin/schedules/$NEW_SCHED_ID" "编辑赛程" '{"roundName":"决赛","status":0}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 成绩管理 ━━━${NC}"
test_api GET "/admin/results?page=1&size=10&eventId=1" "成绩列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/results" "录入成绩" '{"userId":9,"sportId":3,"eventId":1,"score":"23.78","scoreNumeric":23.780,"ranking":3}' "$ADMIN_TOKEN"
NEW_RESULT_RESP=$(curl -s "$BASE/admin/results?page=1&size=5&eventId=1&sportId=3" -H "Authorization: Bearer $ADMIN_TOKEN")
NEW_RESULT_ID=$(json_field "$NEW_RESULT_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")
test_api PUT "/admin/results/$NEW_RESULT_ID" "编辑成绩" '{"score":"23.50","scoreNumeric":23.500,"ranking":2}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 奖项管理 ━━━${NC}"
test_api GET "/admin/awards?page=1&size=10&eventId=1" "奖项列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/awards" "颁发奖项" '{"userId":9,"sportId":3,"eventId":1,"awardType":"BRONZE","awardName":"男子200米铜牌","certificateNo":"AWD-TEST-001"}' "$ADMIN_TOKEN"
NEW_AWARD_RESP=$(curl -s "$BASE/admin/awards?page=1&size=5&eventId=1&awardType=BRONZE" -H "Authorization: Bearer $ADMIN_TOKEN")
NEW_AWARD_ID=$(json_field "$NEW_AWARD_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")
test_api PUT "/admin/awards/$NEW_AWARD_ID" "编辑奖项" '{"awardName":"男子200米铜牌(已修改)"}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 公告管理 ━━━${NC}"
test_api GET "/admin/announcements?page=1&size=10" "公告列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/announcements" "发布公告" '{"title":"测试联调公告","content":"这是一条测试公告内容","type":0,"status":1}' "$ADMIN_TOKEN"
NEW_ANN_RESP=$(curl -s "$BASE/admin/announcements?page=1&size=5" -H "Authorization: Bearer $ADMIN_TOKEN")
NEW_ANN_ID=$(json_field "$NEW_ANN_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")
test_api PUT "/admin/announcements/$NEW_ANN_ID" "编辑公告" '{"title":"测试联调公告(已修改)","content":"修改后的公告内容"}' "$ADMIN_TOKEN"

echo ""; echo "${YELLOW}━━━ 管理端 - 院系/配置 ━━━${NC}"
test_api GET "/admin/departments" "院系列表" "" "$ADMIN_TOKEN"
test_api POST "/admin/departments" "新增院系" "{\"name\":\"DEPTEST${TIMESTAMP}\",\"code\":\"DT${TIMESTAMP}\",\"contactPerson\":\"张三\",\"contactPhone\":\"13900000001\"}" "$ADMIN_TOKEN"
test_api GET "/admin/settings" "系统配置" "" "$ADMIN_TOKEN"
test_api PUT "/admin/settings" "更新配置" '[{"id":1,"configKey":"site_name","configValue":"高校运动会管理系统"}]' "$ADMIN_TOKEN"

# ============ 4. 清理测试数据(DELETE接口) ============
echo ""; echo "${YELLOW}━━━ 清理 - DELETE接口 ━━━${NC}"
# 获取刚创建的院系ID
DEP_RESP=$(curl -s "$BASE/admin/departments" -H "Authorization: Bearer $ADMIN_TOKEN")
DEP_ID=$(echo "$DEP_RESP" | python3 -c "import sys,json;ds=json.load(sys.stdin).get('data',[]);print(next((d['id'] for d in ds if 'DT${TIMESTAMP}' in d.get('code','')),0))" 2>/dev/null)
# 获取测试注册用户ID
TEST_USER_RESP=$(curl -s "$BASE/admin/users?page=1&size=5&keyword=testuser${TIMESTAMP}" -H "Authorization: Bearer $ADMIN_TOKEN")
TEST_USER_ID=$(json_field "$TEST_USER_RESP" ".get('data',{}).get('records',[{}])[0].get('id',0)")

test_api DELETE "/admin/announcements/$NEW_ANN_ID" "删除公告" "" "$ADMIN_TOKEN"
test_api DELETE "/admin/awards/$NEW_AWARD_ID" "删除奖项" "" "$ADMIN_TOKEN"
test_api DELETE "/admin/results/$NEW_RESULT_ID" "删除成绩" "" "$ADMIN_TOKEN"
test_api DELETE "/admin/schedules/$NEW_SCHED_ID" "删除赛程" "" "$ADMIN_TOKEN"
test_api DELETE "/admin/sports/$NEW_SPORT_ID" "删除项目" "" "$ADMIN_TOKEN"
test_api DELETE "/admin/events/$NEW_EVENT_ID" "删除赛事" "" "$ADMIN_TOKEN"
[ "$DEP_ID" != "0" ] && test_api DELETE "/admin/departments/$DEP_ID" "删除院系" "" "$ADMIN_TOKEN"
[ "$TEST_USER_ID" != "0" ] && test_api DELETE "/admin/users/$TEST_USER_ID" "删除测试用户" "" "$ADMIN_TOKEN"

# ============ 结果汇总 ============
echo ""
echo "╔═══════════════════════════════════════════════════════╗"
printf "║  测试完成: 共 ${TOTAL} 个   "
printf "${GREEN}通过 ${PASS}${NC}   "
printf "${RED}失败 ${FAIL}${NC}"
printf "                  ║\n"
if [ "$FAIL" -eq 0 ]; then
  echo "║  🎉 全部通过！所有接口联调无问题                          ║"
else
  echo "║  ⚠️  存在失败项，请检查后端日志                           ║"
fi
echo "╚═══════════════════════════════════════════════════════╝"
echo ""
exit $FAIL
