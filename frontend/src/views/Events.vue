<template>
  <div class="page-container">
    <div class="page-header"><h1><Medal :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 赛事列表</h1><p>查看所有运动会赛事信息</p></div>
    <div class="card-grid">
      <div class="card fade-in" v-for="event in events" :key="event.id" style="cursor:pointer"
           @click="$router.push(`/events/${event.id}`)">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
          <span class="status-tag" :class="statusClass(event.status)">{{ statusText(event.status) }}</span>
          <span style="font-size: 12px; color: var(--text-muted);"><MapPin :size="12" style="display:inline;vertical-align:middle" /> {{ event.location }}</span>
        </div>
        <h3 style="font-size: 18px; font-weight: 600; margin-bottom: 8px;">{{ event.name }}</h3>
        <p style="color: var(--text-secondary); font-size: 14px; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
          {{ event.description }}
        </p>
        <div style="display: flex; gap: 16px; font-size: 13px; color: var(--text-muted);">
          <span><Calendar :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ event.startDate }} ~ {{ event.endDate }}</span>
        </div>
      </div>
    </div>
    <n-empty v-if="events.length === 0" description="暂无赛事" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getEvents } from '@/api'
import { Medal, Calendar, MapPin } from 'lucide-vue-next'

const events = ref<any[]>([])

const statusText = (s: number) => s === 0 ? '筹备中' : s === 1 ? '报名中' : s === 2 ? '进行中' : '已结束'
const statusClass = (s: number) => s === 0 ? 'pending' : s === 1 ? 'approved' : s === 2 ? 'gold' : 'cancelled'

onMounted(async () => {
  try {
    const res: any = await getEvents()
    events.value = res.data || []
  } catch (e) {}
})
</script>
