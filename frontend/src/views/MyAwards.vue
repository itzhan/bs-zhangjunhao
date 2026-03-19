<template>
  <div class="page-container">
    <div class="page-header"><h1><Medal :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 我的奖项</h1></div>
    <div class="card-grid">
      <div class="card" v-for="a in list" :key="a.id">
        <div style="text-align: center;">
          <div style="margin-bottom: 8px;">
            <Medal v-if="a.awardType==='GOLD'" :size="48" color="#D97706" />
            <Medal v-else-if="a.awardType==='SILVER'" :size="48" color="#6B7280" />
            <Medal v-else-if="a.awardType==='BRONZE'" :size="48" color="#B45309" />
            <Trophy v-else :size="48" color="var(--primary)" />
          </div>
          <h3 style="font-weight: 700; font-size: 16px; margin-bottom: 8px;">{{ a.awardName }}</h3>
          <p style="font-size: 13px; color: var(--text-secondary);">{{ a.sportName }}</p>
          <p style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">{{ a.eventName }}</p>
          <p v-if="a.certificateNo" style="font-size: 11px; color: var(--text-muted); margin-top: 8px;">证书编号: {{ a.certificateNo }}</p>
        </div>
      </div>
    </div>
    <n-empty v-if="list.length===0" description="暂无获奖记录" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyAwards } from '@/api'
import { Medal, Trophy } from 'lucide-vue-next'
const list = ref<any[]>([])
onMounted(async () => {
  try { const res:any = await getMyAwards(); list.value = res.data||[] } catch(e){}
})
</script>
