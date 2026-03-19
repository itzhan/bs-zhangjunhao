<script setup lang="ts">
import { $t } from '@/locales';

defineOptions({ name: 'CreativityBanner' });
</script>

<template>
  <NCard :title="$t('page.home.creativity')" :bordered="false" size="small" class="h-full card-wrapper">
    <div class="h-full flex-col flex-center gap-16px py-24px">
      <div class="flex-center gap-12px">
        <SvgIcon icon="lucide:medal" class="text-48px text-primary" />
        <SvgIcon icon="lucide:trophy" class="text-64px text-primary" />
        <SvgIcon icon="lucide:medal" class="text-48px text-primary" />
      </div>
      <h2 class="text-24px font-bold text-primary">高校运动会管理系统</h2>
      <p class="text-14px text-#999 text-center px-24px">
        赛事管理 · 成绩录入 · 数据统计
      </p>
      <div class="flex gap-16px mt-8px">
        <NTag type="success" round>
          <template #icon><SvgIcon icon="lucide:calendar-check" class="text-14px" /></template>
          赛事管理
        </NTag>
        <NTag type="info" round>
          <template #icon><SvgIcon icon="lucide:clipboard-list" class="text-14px" /></template>
          成绩管理
        </NTag>
        <NTag type="warning" round>
          <template #icon><SvgIcon icon="lucide:bar-chart-3" class="text-14px" /></template>
          奖牌统计
        </NTag>
      </div>
    </div>
  </NCard>
</template>

<style scoped></style>
