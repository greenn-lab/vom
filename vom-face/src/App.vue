<template>
  <v-app>
    <v-container fluid>
      <router-view></router-view>
    </v-container>
  </v-app>
</template>

<script>
export default {
  name: 'App'
};
</script>

<style>
.v-application {
  font-weight: 300
}
</style>
