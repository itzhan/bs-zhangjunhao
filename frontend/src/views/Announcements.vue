<template>
  <div class="page-container">
    <div class="page-header"><h1><Megaphone :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 公告通知</h1></div>
    <n-tabs type="line" v-model:value="currentType" @update:value="loadData">
      <n-tab-pane name="all" tab="全部" />
      <n-tab-pane name="0" tab="通知" />
      <n-tab-pane name="1" tab="公告" />
      <n-tab-pane name="2" tab="规则" />
    </n-tabs>
    <div style="margin-top: 24px;">
      <div class="card fade-in" v-for="ann in list" :key="ann.id"
           style="margin-bottom: 16px; cursor: pointer;" @click="$router.push(`/announcements/${ann.id}`)">
        <div style="display:flex; justify-content: space-between; align-items: center;">
          <div>
            <span class="status-tag" :class="ann.isTop ? 'gold' : 'approved'" style="margin-right: 8px;">
              {{ ann.isTop ? '置顶' : typeText(ann.type) }}
            </span>
            <span style="font-weight: 600;">{{ ann.title }}</span>
          </div>
          <span style="font-size: 12px; color: var(--text-muted);">{{ ann.publishTime?.substring(0,10) }}</span>
        </div>
      </div>
    </div>
    <n-pagination v-if="total > 10" v-model:page="page" :page-count="Math.ceil(total/10)"
                  style="justify-content: center; margin-top: 24px;" @update:page="loadData" />
    <n-empty v-if="list.length===0" description="暂无公告" style="margin-top: 48px;" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAnnouncements } from '@/api'
import { Megaphone } from 'lucide-vue-next'
const list = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const currentType = ref('all')
const typeText = (t:number) => t===0?'通知':t===1?'公告':'规则'
const loadData = async () => {
  try {
    const params: any = { page: page.value, size: 10 }
    if (currentType.value !== 'all') params.type = Number(currentType.value)
    const res: any = await getAnnouncements(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch(e) {}
}
onMounted(loadData)
</script>
