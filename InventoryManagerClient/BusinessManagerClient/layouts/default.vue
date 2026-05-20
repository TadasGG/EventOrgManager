<template>
  <div class="grid items-start lg:flex">
    <sidebar v-if="!$route.meta.hideSidebar" />

    <div class="p-4 w-full lg:ml-[250px]">
      <slot />
    </div>
  </div>
</template>