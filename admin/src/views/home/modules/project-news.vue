<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { fetchAnnouncementList } from '@/service/api';

defineOptions({ name: 'ProjectNews' });

interface NewsItem { id: number; content: string; time: string; icon: string; }
const newses = ref<NewsItem[]>([]);
const loading = ref(true);

onMounted(async () => {
  const { data, error } = await fetchAnnouncementList({ page: 1, size: 5, status: 1 });
  if (!error && data) {
    const records = (data as any)?.records || [];
    newses.value = records.map((a: any) => ({
      id: a.id,
      content: a.title,
      time: a.publishTime ? String(a.publishTime).slice(0, 16).replace('T', ' ') : '',
      icon: a.isTop ? 'mdi:pin' : 'mdi:bullhorn-outline'
    }));
  }
  if (newses.value.length === 0) {
    newses.value = [
      { id: 1, content: '暂无公告', time: '', icon: 'mdi:information-outline' }
    ];
  }
  loading.value = false;
});
</script>
<template>
  <NCard title="最新公告" :bordered="false" size="small" segmented class="card-wrapper">
    <NSpin :show="loading">
      <NList>
        <NListItem v-for="item in newses" :key="item.id">
          <template #prefix>
            <div class="size-48px flex-center rd-1/2 bg-primary/10">
              <SvgIcon :icon="item.icon" class="text-24px text-primary" />
            </div>
          </template>
          <NThing :title="item.content" :description="item.time" />
        </NListItem>
      </NList>
    </NSpin>
  </NCard>
</template>
<style scoped></style>
