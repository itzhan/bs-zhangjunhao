<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { createReusableTemplate } from '@vueuse/core';
import { useThemeStore } from '@/store/modules/theme';
import { fetchDashboard } from '@/service/api';

defineOptions({ name: 'CardData' });

interface CardData {
  key: string;
  title: string;
  value: number;
  unit: string;
  color: { start: string; end: string };
  icon: string;
}

const cards = ref<CardData[]>([
  { key: 'userCount', title: '注册用户', value: 0, unit: '', color: { start: '#ec4786', end: '#b955a4' }, icon: 'mdi:account-multiple' },
  { key: 'eventCount', title: '赛事数量', value: 0, unit: '', color: { start: '#865ec0', end: '#5144b4' }, icon: 'mdi:calendar-star' },
  { key: 'sportCount', title: '比赛项目', value: 0, unit: '', color: { start: '#56cdf3', end: '#719de3' }, icon: 'mdi:run-fast' },
  { key: 'registrationCount', title: '报名总数', value: 0, unit: '', color: { start: '#fcbc25', end: '#f68057' }, icon: 'mdi:clipboard-list-outline' }
]);

onMounted(async () => {
  const { data, error } = await fetchDashboard();
  if (!error && data) {
    cards.value[0].value = data.userCount || 0;
    cards.value[1].value = data.eventCount || 0;
    cards.value[2].value = data.sportCount || 0;
    cards.value[3].value = data.registrationCount || 0;
  }
});

interface GradientBgProps { gradientColor: string; }
const [DefineGradientBg, GradientBg] = createReusableTemplate<GradientBgProps>();
const themeStore = useThemeStore();
function getGradientColor(color: CardData['color']) {
  return `linear-gradient(to bottom right, ${color.start}, ${color.end})`;
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <DefineGradientBg v-slot="{ $slots, gradientColor }">
      <div class="px-16px pb-4px pt-8px text-white" :style="{ backgroundImage: gradientColor, borderRadius: themeStore.themeRadius + 'px' }">
        <component :is="$slots.default" />
      </div>
    </DefineGradientBg>
    <NGrid cols="s:1 m:2 l:4" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi v-for="item in cards" :key="item.key">
        <GradientBg :gradient-color="getGradientColor(item.color)" class="flex-1">
          <h3 class="text-16px">{{ item.title }}</h3>
          <div class="flex justify-between pt-12px">
            <SvgIcon :icon="item.icon" class="text-32px" />
            <CountTo :prefix="item.unit" :start-value="1" :end-value="item.value" class="text-30px text-white dark:text-dark" />
          </div>
        </GradientBg>
      </NGi>
    </NGrid>
  </NCard>
</template>

<style scoped></style>
