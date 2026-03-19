<template>
  <div class="page-container">
    <n-spin :show="loading">
      <div v-if="event">
        <div class="page-header">
          <n-space align="center">
            <n-button text @click="$router.back()">← 返回</n-button>
            <h1>{{ event.name }}</h1>
          </n-space>
          <p>{{ event.description }}</p>
        </div>
        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 32px;">
          <div class="card" style="text-align:center">
            <div style="font-size: 13px; color: var(--text-muted);">比赛时间</div>
            <div style="font-weight: 600; margin-top: 4px;">{{ event.startDate }} ~ {{ event.endDate }}</div>
          </div>
          <div class="card" style="text-align:center">
            <div style="font-size: 13px; color: var(--text-muted);">比赛地点</div>
            <div style="font-weight: 600; margin-top: 4px;">{{ event.location }}</div>
          </div>
          <div class="card" style="text-align:center">
            <div style="font-size: 13px; color: var(--text-muted);">赛事状态</div>
            <div style="font-weight: 600; margin-top: 4px; color: var(--accent);">{{ statusText(event.status) }}</div>
          </div>
          <div class="card" style="text-align:center">
            <div style="font-size: 13px; color: var(--text-muted);">运动项目</div>
            <div style="font-weight: 600; margin-top: 4px; color: var(--primary);">{{ sports.length }} 个</div>
          </div>
        </div>

        <h2 style="font-size: 20px; font-weight: 700; margin-bottom: 16px;"><PersonStanding :size="20" style="display:inline;vertical-align:middle;margin-right:4px" /> 运动项目列表</h2>
        <n-tabs type="line" animated>
          <n-tab-pane v-for="cat in categories" :key="cat" :name="cat" :tab="cat">
            <div class="card-grid" style="margin-top: 16px;">
              <div class="card" v-for="sport in sportsByCategory(cat)" :key="sport.id">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                  <h4 style="font-weight: 600;">{{ sport.name }}</h4>
                  <span style="font-size: 12px; color: var(--text-muted);">
                    {{ sport.registrationCount || 0 }} 人已报名
                  </span>
                </div>
                <p style="font-size: 13px; color: var(--text-secondary); margin-bottom: 12px;">{{ sport.description }}</p>
                <div style="display:flex; gap: 8px; flex-wrap: wrap; font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">
                  <n-tag size="small" v-if="sport.genderLimit === 1">仅男</n-tag>
                  <n-tag size="small" v-if="sport.genderLimit === 2">仅女</n-tag>
                  <n-tag size="small" v-if="sport.unit">单位: {{ sport.unit }}</n-tag>
                  <n-tag v-if="registrationStatusMap[sport.id] !== undefined"
                         size="small"
                         :type="registrationStatusConfig[registrationStatusMap[sport.id]]?.type">
                    {{ registrationStatusConfig[registrationStatusMap[sport.id]]?.text }}
                  </n-tag>
                </div>
                <n-button v-if="registrationStatusMap[sport.id] === undefined"
                          type="primary"
                          size="small"
                          block
                          @click="handleRegister(sport)"
                          :disabled="event.status !== 1">
                  {{ event.status === 1 ? '立即报名' : '报名未开放' }}
                </n-button>
              </div>
            </div>
          </n-tab-pane>
        </n-tabs>
      </div>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getEventDetail, getSportsByEvent, createRegistration, getMyRegistrations } from '@/api'
import { PersonStanding } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const event = ref<any>(null)
const sports = ref<any[]>([])
const registrationStatusMap = ref<Record<number, number>>({})

const registrationStatusConfig: Record<number, { text: string; type: 'warning' | 'success' | 'error' | 'default' }> = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已报名', type: 'success' },
  2: { text: '已拒绝', type: 'error' },
  3: { text: '已取消', type: 'default' }
}

const statusText = (s: number) => s === 0 ? '筹备中' : s === 1 ? '报名中' : s === 2 ? '进行中' : '已结束'

const categories = computed(() => {
  const cats = [...new Set(sports.value.map(s => s.category || '其他'))]
  return cats
})

const sportsByCategory = (cat: string) => sports.value.filter(s => (s.category || '其他') === cat)

const handleRegister = async (sport: any) => {
  if (!localStorage.getItem('token')) {
    window.$message?.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await createRegistration({ sportId: sport.id, eventId: event.value.id })
    registrationStatusMap.value = {
      ...registrationStatusMap.value,
      [sport.id]: 0
    }
    window.$message?.success('报名成功！请等待审核')
  } catch (e) {}
}

const loadMyRegistrationStatus = async (eventId: number) => {
  if (!localStorage.getItem('token')) {
    registrationStatusMap.value = {}
    return
  }
  try {
    const res: any = await getMyRegistrations()
    registrationStatusMap.value = (res.data || [])
      .filter((item: any) => item.eventId === eventId && item.status !== 3)
      .reduce((acc: Record<number, number>, item: any) => {
        acc[item.sportId] = item.status
        return acc
      }, {})
  } catch (e) {}
}

onMounted(async () => {
  try {
    const id = Number(route.params.id)
    const res: any = await getEventDetail(id)
    event.value = res.data
    const sRes: any = await getSportsByEvent(id)
    sports.value = sRes.data || []
    await loadMyRegistrationStatus(id)
  } catch (e) {} finally { loading.value = false }
})
</script>
