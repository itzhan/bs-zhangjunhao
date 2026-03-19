<template>
  <div class="page-container">
    <div class="hero fade-in">
      <h1><PersonStanding :size="28" :stroke-width="2.5" style="display:inline;vertical-align:middle;margin-right:4px" /> {{ currentEvent?.name || '高校运动会管理系统' }}</h1>
      <p>{{ currentEvent?.description || '赛事报名、成绩查询、奖牌榜一站式管理平台' }}</p>
      <div class="hero-stats" v-if="currentEvent">
        <div class="hero-stat">
          <div class="num">{{ sportCount }}</div>
          <div class="label">比赛项目</div>
        </div>
        <div class="hero-stat">
          <div class="num">{{ currentEvent?.status === 1 ? '报名中' : statusText }}</div>
          <div class="label">赛事状态</div>
        </div>
        <div class="hero-stat">
          <div class="num">{{ currentEvent?.location }}</div>
          <div class="label">比赛地点</div>
        </div>
      </div>
      <div style="margin-top: 24px; position: relative; z-index: 1;">
        <n-space>
          <n-button type="primary" size="large" @click="$router.push('/events')">浏览赛事</n-button>
          <n-button size="large" @click="$router.push('/ranking')">查看排行榜</n-button>
        </n-space>
      </div>
    </div>

    <!-- 最新公告 -->
    <div class="page-header">
      <h2 style="font-size: 22px; font-weight: 700;"><Megaphone :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 最新公告</h2>
    </div>
    <div class="card-grid" style="margin-bottom: 48px;">
      <div class="card fade-in" v-for="ann in announcements" :key="ann.id"
           @click="$router.push(`/announcements/${ann.id}`)" style="cursor: pointer;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
          <span class="status-tag" :class="ann.isTop ? 'gold' : 'approved'">
            <template v-if="ann.isTop"><Pin :size="12" style="display:inline;vertical-align:middle;margin-right:2px" /> 置顶</template>
            <template v-else>{{ typeText(ann.type) }}</template>
          </span>
          <span style="font-size: 12px; color: var(--text-muted);">{{ formatDate(ann.publishTime) }}</span>
        </div>
        <h3 style="font-size: 16px; font-weight: 600; margin-bottom: 8px;">{{ ann.title }}</h3>
        <p style="color: var(--text-secondary); font-size: 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
          {{ stripHtml(ann.content) }}
        </p>
        <div style="margin-top: 12px; font-size: 12px; color: var(--text-muted);">
          <Eye :size="14" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ ann.viewCount }} 次浏览
        </div>
      </div>
    </div>

    <!-- 快速入口 -->
    <div class="page-header">
      <h2 style="font-size: 22px; font-weight: 700;"><Zap :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 快速入口</h2>
    </div>
    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px;">
      <div class="card" style="text-align: center; cursor: pointer;" @click="$router.push('/events')">
        <div style="margin-bottom: 8px;"><Medal :size="32" color="var(--primary)" /></div>
        <div style="font-weight: 600;">赛事报名</div>
      </div>
      <div class="card" style="text-align: center; cursor: pointer;" @click="$router.push('/my/registrations')">
        <div style="margin-bottom: 8px;"><ClipboardList :size="32" color="var(--primary)" /></div>
        <div style="font-weight: 600;">我的报名</div>
      </div>
      <div class="card" style="text-align: center; cursor: pointer;" @click="$router.push('/my/schedules')">
        <div style="margin-bottom: 8px;"><Calendar :size="32" color="var(--primary)" /></div>
        <div style="font-weight: 600;">我的赛程</div>
      </div>
      <div class="card" style="text-align: center; cursor: pointer;" @click="$router.push('/my/results')">
        <div style="margin-bottom: 8px;"><BarChart3 :size="32" color="var(--primary)" /></div>
        <div style="font-weight: 600;">我的成绩</div>
      </div>
      <div class="card" style="text-align: center; cursor: pointer;" @click="$router.push('/medal-table')">
        <div style="margin-bottom: 8px;"><Trophy :size="32" color="var(--primary)" /></div>
        <div style="font-weight: 600;">奖牌榜</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getCurrentEvent, getSportsByEvent, getAnnouncements } from '@/api'
import { PersonStanding, Megaphone, Pin, Eye, Zap, Medal, ClipboardList, Calendar, BarChart3, Trophy } from 'lucide-vue-next'

const currentEvent = ref<any>(null)
const sportCount = ref(0)
const announcements = ref<any[]>([])

const statusText = computed(() => {
  if (!currentEvent.value) return ''
  const s = currentEvent.value.status
  return s === 0 ? '筹备中' : s === 1 ? '报名中' : s === 2 ? '进行中' : '已结束'
})

const typeText = (type: number) => {
  return type === 0 ? '通知' : type === 1 ? '公告' : '规则'
}

const formatDate = (d: string) => d ? d.substring(0, 10) : ''
const stripHtml = (s: string) => s?.replace(/<[^>]+>/g, '').replace(/\n/g, ' ').substring(0, 100) || ''

onMounted(async () => {
  try {
    const res: any = await getCurrentEvent()
    currentEvent.value = res.data
    if (res.data?.id) {
      const sportsRes: any = await getSportsByEvent(res.data.id)
      sportCount.value = sportsRes.data?.length || 0
    }
  } catch (e) {}
  try {
    const annRes: any = await getAnnouncements({ page: 1, size: 6 })
    announcements.value = annRes.data?.records || []
  } catch (e) {}
})
</script>
