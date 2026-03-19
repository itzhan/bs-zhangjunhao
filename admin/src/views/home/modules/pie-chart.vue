<script setup lang="ts">
import { onMounted } from 'vue';
import { useEcharts } from '@/hooks/common/echarts';
import { fetchMedalTable } from '@/service/api';

defineOptions({ name: 'PieChart' });
const { domRef, updateOptions } = useEcharts(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: '1%', left: 'center', itemStyle: { borderWidth: 0 } },
  series: [{
    color: ['#5da8ff', '#8e9dff', '#fedc69', '#26deca', '#f68057', '#ff6b81', '#a29bfe', '#00b894'],
    name: '奖牌统计', type: 'pie', radius: ['45%', '75%'],
    avoidLabelOverlap: false,
    itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 1 },
    label: { show: false, position: 'center' },
    emphasis: { label: { show: true, fontSize: '12' } },
    labelLine: { show: false },
    data: [] as { name: string; value: number }[]
  }]
}));

onMounted(async () => {
  const { data, error } = await fetchMedalTable(1);
  if (!error && data && Array.isArray(data)) {
    updateOptions(opts => {
      opts.series[0].data = data.slice(0, 8).map((item: any) => ({
        name: item.departmentName || item.name || '未知',
        value: (item.gold || 0) + (item.silver || 0) + (item.bronze || 0)
      }));
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
