<template>
  <div class="page-container">
    <div class="page-header"><h1><BarChart3 :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 我的成绩</h1></div>
    <n-data-table :columns="columns" :data="list" :bordered="false" />
    <n-empty v-if="list.length===0" description="暂无成绩记录" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { NTag } from 'naive-ui'
import { getMyResults } from '@/api'
import { BarChart3 } from 'lucide-vue-next'
const list = ref<any[]>([])
const topTagTypes = ['warning', 'default', 'info'] as const
const columns = [
  { title: '赛事', key: 'eventName' },
  { title: '项目', key: 'sportName' },
  { title: '成绩', key: 'score', render:(r:any)=>h('span',{style:{fontWeight:'700',color:'var(--accent)'}},r.score) },
  { title: '名次', key: 'ranking', render:(r:any)=>r.ranking<=3?h(NTag,{type:topTagTypes[r.ranking-1],size:'small',round:true},()=>['第1名','第2名','第3名'][r.ranking-1]):r.ranking },
  { title: '破纪录', key: 'isRecord', render:(r:any)=>r.isRecord?h(NTag,{type:'warning',size:'small'},()=>'破纪录'):'-' },
]
onMounted(async () => {
  try { const res:any = await getMyResults(); list.value = res.data||[] } catch(e){}
})
</script>
