<template>
  <div class="page-container">
    <div class="page-header"><h1><PersonStanding :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 运动项目</h1><p>浏览各个赛事的运动项目</p></div>
    <n-select v-model:value="selectedEventId" :options="eventOptions" placeholder="选择赛事" style="max-width:400px; margin-bottom: 24px;" @update:value="loadSports" />
    <div class="card-grid">
      <div class="card" v-for="sport in sports" :key="sport.id">
        <h4 style="font-weight: 600; margin-bottom: 8px;">{{ sport.name }}</h4>
        <p style="font-size: 13px; color: var(--text-secondary);">{{ sport.description }}</p>
        <div style="margin-top: 8px; font-size: 12px; color: var(--text-muted);">
          分类: {{ sport.category }} | 已报名: {{ sport.registrationCount || 0 }}
        </div>
      </div>
    </div>
    <n-empty v-if="sports.length === 0" description="请选择赛事查看项目" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getEvents, getSportsByEvent } from '@/api'
import { PersonStanding } from 'lucide-vue-next'

const eventOptions = ref<any[]>([])
const selectedEventId = ref<number|null>(null)
const sports = ref<any[]>([])

const getListData = (payload: any) => {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  return []
}

const loadSports = async (id: number | null) => {
  if (!id) {
    sports.value = []
    return
  }
  try {
    const res: any = await getSportsByEvent(id)
    sports.value = getListData(res.data)
  } catch (e) {
    sports.value = []
  }
}

onMounted(async () => {
  try {
    const res: any = await getEvents()
    const events = getListData(res.data)
    eventOptions.value = events.map((e: any) => ({ label: e.name, value: e.id }))
    if (eventOptions.value.length > 0) {
      selectedEventId.value = eventOptions.value[0].value
      await loadSports(selectedEventId.value)
    } else {
      sports.value = []
    }
  } catch (e) {
    eventOptions.value = []
    sports.value = []
  }
})
</script>
