<template>
  <div class="page-container">
    <n-button text @click="$router.back()" style="margin-bottom: 16px;">← 返回</n-button>
    <div class="card" v-if="ann" style="max-width: 800px; margin: 0 auto;">
      <h1 style="font-size: 24px; font-weight: 700; margin-bottom: 16px;">{{ ann.title }}</h1>
      <div style="display: flex; gap: 16px; font-size: 13px; color: var(--text-muted); margin-bottom: 24px;">
        <span><Calendar :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ ann.publishTime?.substring(0,10) }}</span>
        <span><PenLine :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ ann.authorName || '管理员' }}</span>
        <span><Eye :size="13" style="display:inline;vertical-align:middle;margin-right:2px" /> {{ ann.viewCount }} 次浏览</span>
      </div>
      <n-divider />
      <div style="line-height: 1.8; white-space: pre-wrap; color: var(--text-secondary);">{{ ann.content }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAnnouncementDetail } from '@/api'
import { Calendar, PenLine, Eye } from 'lucide-vue-next'
const route = useRoute()
const ann = ref<any>(null)
onMounted(async () => {
  try { const res: any = await getAnnouncementDetail(Number(route.params.id)); ann.value = res.data } catch(e) {}
})
</script>
