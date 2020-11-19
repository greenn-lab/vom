<template>
  <v-container fluid>
    <v-row>

      <v-col/>
      <v-col md="6">
        <v-card
          elevation="1"
          outlined
        >
          <v-card-title>CPU</v-card-title>
          <v-card-text>
            <ChartCPU />
          </v-card-text>
        </v-card>

      </v-col>

    </v-row>
  </v-container>
</template>
<script>
import ChartCPU from "../components/dashboard/ChartCPU";
import {mapGetters} from "vuex";

export default {
  components: {ChartCPU},
  computed: {
    ...mapGetters('dashboard', {cpu: 'cpuStats'})
  },
  mounted() {
    console.log('cpu', this.cpu)
  }
}
</script>
