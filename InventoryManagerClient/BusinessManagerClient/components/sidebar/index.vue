<script setup lang="ts">
const open = ref(false);
</script>

<template>
  <div class="relative z-50 ">
    <div class="flex lg:hidden z-50 items-center justify-between w-full h-full p-4">
      <div class="overflow-hidden rounded-lg">
        <Logo class="size-10" @click="open = true" />
      </div>

      <div v-if="open" class="absolute top-0 left-0 z-50 w-full h-full h-screen">
        <Icon class="absolute z-50 cursor-pointer right-4 top-4" size="40" name="material-symbols:close"
              @click="open = false"/>

        <SidebarMenu />

      </div>
    </div>

    <div class="fixed hidden lg:flex w-[250px] h-screen flex flex-col justify-between">
      <sidebar-menu class="h-full"/>
    </div>
  </div>
</template>