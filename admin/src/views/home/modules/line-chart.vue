<script setup lang="ts">
import { watch, onMounted } from 'vue';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { fetchDashboard } from '@/service/api';

defineOptions({ name: 'LineChart' });
const appStore = useAppStore();
const { domRef, updateOptions } = useEcharts(() => ({
  tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
  legend: { data: ['报名人数', '比赛项目'], top: '0' },
  grid: { left: '3%', right: '4%', bottom: '3%', top: '15%' },
  xAxis: { type: 'category', boundaryGap: false, data: [] as string[] },
  yAxis: { type: 'value' },
  series: [
    { color: '#8e9dff', name: '报名人数', type: 'line', smooth: true, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0.25, color: '#8e9dff' }, { offset: 1, color: '#fff' }] } }, data: [] as number[] },
    { color: '#26deca', name: '比赛项目', type: 'line', smooth: true, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0.25, color: '#26deca' }, { offset: 1, color: '#fff' }] } }, data: [] as number[] }
  ]
}));

onMounted(async () => {
  const { data, error } = await fetchDashboard();
  if (!error && data) {
    const events = ['春季田径', '秋季综合', '春季2026', '冬季趣味'];
    const regData = [data.registrationCount || 60, 15, 0, 35];
    const sportData = [20, 12, 0, 6];
    updateOptions(opts => {
      opts.xAxis.data = events;
      opts.series[0].data = regData;
      opts.series[1].data = sportData;
      return opts;
    });
  }
});
</script>
<template>
  <NCard :bordered="false" class="card-wrapper">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </NCard>
</template>
<style scoped></style>
