<template>
  <div class="page-container">
    <div class="page-header"><h1><Trophy :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 成绩排行榜</h1></div>
    <div style="display:flex; gap: 16px; margin-bottom: 24px; flex-wrap: wrap;">
      <n-select v-model:value="eventId" :options="eventOptions" placeholder="选择赛事" style="width:280px" @update:value="onEventChange" />
      <n-select v-model:value="sportId" :options="sportOptions" placeholder="选择项目（可选）" clearable style="width:280px" @update:value="loadRanking" />
    </div>
    <n-data-table :columns="columns" :data="ranking" :bordered="false" />
    <n-empty v-if="ranking.length===0" description="暂无排名数据" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { NTag } from 'naive-ui'
import { getEvents, getSportsByEvent, getRanking } from '@/api'
import { Trophy } from 'lucide-vue-next'

const eventId = ref<number|null>(null)
const sportId = ref<number|null>(null)
const eventOptions = ref<any[]>([])
const sportOptions = ref<any[]>([])
const ranking = ref<any[]>([])
const topTagTypes = ['warning', 'default', 'info'] as const

const columns = [
  { title: '名次', key: 'ranking', width: 80, render: (row:any) => h(NTag,{type: row.ranking<=3?topTagTypes[row.ranking-1]:'default', size:'small', round:true, style:{fontWeight:'700'}}, ()=> row.ranking<=3?['第1名','第2名','第3名'][row.ranking-1]:'第'+row.ranking+'名') },
  { title: '姓名', key: 'userName' },
  { title: '院系', key: 'departmentName' },
  { title: '项目', key: 'sportName' },
  { title: '成绩', key: 'score', render:(row:any)=>h('span',{style:{fontWeight:'600',color:'var(--accent)'}}, row.score) },
  { title: '破纪录', key: 'isRecord', width: 80, render:(row:any)=>row.isRecord?h(NTag,{type:'warning',size:'small'},()=>'破纪录'):null }
]

const onEventChange = async (id: number) => {
  sportId.value = null
  try { const res:any = await getSportsByEvent(id); sportOptions.value = (res.data||[]).map((s:any)=>({label:s.name,value:s.id})) } catch(e){}
  await loadRanking()
}
const loadRanking = async () => {
  try { const res:any = await getRanking({eventId:eventId.value,sportId:sportId.value}); ranking.value = res.data||[] } catch(e){}
}
onMounted(async () => {
  try {
    const res:any = await getEvents()
    eventOptions.value = (res.data||[]).map((e:any)=>({label:e.name,value:e.id}))
    if (eventOptions.value.length > 0) {
      eventId.value = eventOptions.value[0].value
      await onEventChange(eventId.value)
    }
  } catch(e){}
})
</script>
