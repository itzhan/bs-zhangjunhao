<template>
  <div class="page-container">
    <div class="page-header"><h1><ClipboardList :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 我的报名</h1></div>
    <n-data-table :columns="columns" :data="list" :bordered="false" :loading="loading" />
    <n-empty v-if="!loading && list.length===0" description="暂无报名记录" style="margin-top: 24px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { NButton, NTag, NPopconfirm } from 'naive-ui'
import { getMyRegistrations, cancelRegistration } from '@/api'
import { ClipboardList } from 'lucide-vue-next'
const list = ref<any[]>([])
const loading = ref(true)

const statusMap: any = { 0:{text:'待审核',type:'warning'}, 1:{text:'已通过',type:'success'}, 2:{text:'已拒绝',type:'error'}, 3:{text:'已取消',type:'default'} }
const columns = [
  { title: '赛事', key: 'eventName' },
  { title: '项目', key: 'sportName' },
  { title: '状态', key: 'status', render:(r:any)=>h(NTag,{type:statusMap[r.status]?.type,size:'small'},()=>statusMap[r.status]?.text) },
  { title: '拒绝原因', key: 'rejectReason', render:(r:any)=>r.rejectReason||'-' },
  { title: '报名时间', key: 'createdAt', render:(r:any)=>r.createdAt?.substring(0,10) },
  { title: '操作', key: 'actions', render:(r:any)=>r.status===0?h(NPopconfirm,{onPositiveClick:()=>handleCancel(r.id)},{trigger:()=>h(NButton,{size:'small',type:'error',text:true},()=>'取消'),default:()=>'确定取消报名？'}):null }
]

const handleCancel = async (id:number) => {
  try { await cancelRegistration(id); window.$message?.success('已取消'); loadData() } catch(e){}
}
const loadData = async () => {
  loading.value = true
  try { const res:any = await getMyRegistrations(); list.value = res.data||[] } catch(e){} finally { loading.value = false }
}
onMounted(loadData)
</script>
