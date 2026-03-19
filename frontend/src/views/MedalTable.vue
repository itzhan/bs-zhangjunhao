<template>
  <div class="page-container">
    <div class="page-header"><h1><Trophy :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 各院系奖牌榜</h1></div>
    <n-select v-model:value="eventId" :options="eventOptions" placeholder="选择赛事" style="width:300px; margin-bottom: 24px;" @update:value="loadTable" clearable />
    <n-data-table :columns="columns" :data="table" :bordered="false" />
    <n-empty v-if="table.length===0" description="暂无奖牌数据" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { getEvents, getMedalTable } from '@/api'
import { Trophy } from 'lucide-vue-next'
const eventId = ref<number|null>(null)
const eventOptions = ref<any[]>([])
const table = ref<any[]>([])
const columns = [
  { title: '排名', key: 'index', width: 80, render:(_:any,i:number)=>h('span',{style:{fontWeight:'700'}}, i+1) },
  { title: '院系', key: 'departmentName' },
  { title: '金牌', key: 'gold', render:(r:any)=>h('span',{style:{fontWeight:'700',color:'#D97706'}},r.gold) },
  { title: '银牌', key: 'silver', render:(r:any)=>h('span',{style:{fontWeight:'700',color:'#6B7280'}},r.silver) },
  { title: '铜牌', key: 'bronze', render:(r:any)=>h('span',{style:{fontWeight:'700',color:'#B45309'}},r.bronze) },
  { title: '总计', key: 'total', render:(r:any)=>h('span',{style:{fontWeight:'800',color:'var(--primary)'}},r.total) },
]
const loadTable = async () => {
  try { const res:any = await getMedalTable(eventId.value||undefined); table.value = res.data||[] } catch(e){}
}
onMounted(async () => {
  try { const res:any = await getEvents(); eventOptions.value = (res.data||[]).map((e:any)=>({label:e.name,value:e.id})) } catch(e){}
  loadTable()
})
</script>
