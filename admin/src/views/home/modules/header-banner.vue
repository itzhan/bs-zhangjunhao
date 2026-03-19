<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { useAppStore } from '@/store/modules/app';
import { useAuthStore } from '@/store/modules/auth';
import { fetchDashboard } from '@/service/api';
import { $t } from '@/locales';

defineOptions({ name: 'HeaderBanner' });

const appStore = useAppStore();
const authStore = useAuthStore();
const gap = computed(() => (appStore.isMobile ? 0 : 16));

const stats = ref({ userCount: 0, eventCount: 0, sportCount: 0 });

onMounted(async () => {
  const { data, error } = await fetchDashboard();
  if (!error && data) {
    stats.value.userCount = data.userCount || 0;
    stats.value.eventCount = data.eventCount || 0;
    stats.value.sportCount = data.sportCount || 0;
  }
});

interface StatisticData { id: number; label: string; value: string; }
const statisticData = computed<StatisticData[]>(() => [
  { id: 0, label: '注册用户', value: String(stats.value.userCount) },
  { id: 1, label: '赛事数量', value: String(stats.value.eventCount) },
  { id: 2, label: '比赛项目', value: String(stats.value.sportCount) }
]);
</script>

<template>
  <NCard :bordered="false" class="card-wrapper">
    <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
      <NGi span="24 s:24 m:18">
        <div class="flex-y-center">
          <div class="size-72px shrink-0 flex-center rd-1/2 bg-primary/10">
            <SvgIcon icon="mdi:trophy" class="text-36px text-primary" />
          </div>
          <div class="pl-12px">
            <h3 class="text-18px font-semibold">
              {{ $t('page.home.greeting', { userName: authStore.userInfo.userName }) }}
            </h3>
            <p class="text-#999 leading-30px">欢迎使用高校运动会管理系统后台</p>
          </div>
        </div>
      </NGi>
      <NGi span="24 s:24 m:6">
        <NSpace :size="24" justify="end">
          <NStatistic v-for="item in statisticData" :key="item.id" class="whitespace-nowrap" v-bind="item" />
        </NSpace>
      </NGi>
    </NGrid>
  </NCard>
</template>

<style scoped></style>
