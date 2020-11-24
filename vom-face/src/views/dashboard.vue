<template>
  <v-container fluid>
    <v-row>
      <v-col xs="12" sm="12" md="3">
        <chart-cpu/>
      </v-col>
      <v-col md="3" xs="12">
        <chart-memory />
      </v-col>

      <v-col md="6" xs="12">
        <chart-troves />
      </v-col>

    </v-row>
  </v-container>
</template>
<script>
import ChartCpu from "../components/dashboard/ChartCpu.vue";
import ChartMemory from "../components/dashboard/ChartMemory";
import ChartTroves from "@/components/dashboard/ChartTroves";

export default {
  components: {ChartTroves, ChartMemory, ChartCpu}
}
</script>
