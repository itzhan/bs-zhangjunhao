<template>
  <div class="page-container">
    <div class="page-header"><h1><CalendarDays :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 我的赛程</h1></div>
    <div class="card-grid">
      <div class="card" v-for="s in list" :key="s.id">
        <div style="display:flex; justify-content: space-between; margin-bottom: 8px;">
          <h4 style="font-weight: 600;">{{ s.sportName }}</h4>
          <n-tag :type="s.status===0?'info':s.status===1?'warning':'success'" size="small">
            {{ s.status===0?'未开始':s.status===1?'进行中':'已结束' }}
          </n-tag>
        </div>
        <div style="font-size: 13px; color: var(--text-secondary); display: flex; flex-direction: column; gap: 4px;">
          <span><CalendarDays :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ s.matchDate }} {{ s.startTime }} - {{ s.endTime }}</span>
          <span><MapPin :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ s.venue }}</span>
          <span><RefreshCw :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ s.roundName }} | 第 {{ s.groupNo }} 组</span>
        </div>
      </div>
    </div>
    <n-empty v-if="list.length===0" description="暂无赛程安排" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMySchedules } from '@/api'
import { CalendarDays, MapPin, RefreshCw } from 'lucide-vue-next'
const list = ref<any[]>([])
onMounted(async () => {
  try { const res:any = await getMySchedules(); list.value = res.data||[] } catch(e){}
})
</script>
