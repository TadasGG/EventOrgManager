<template>
  <div class="theme-toggle">
    <USwitch
        v-model="isDarkMode"
        unchecked-icon="ion:sunny-outline"
        checked-icon="ion:moon-outline"
        size="xl"
        :ui="{
        base: [
          'shadow-xl inline-flex items-center rounded-full button-item w-16 h-8',
          'transition-[background] duration-200 cursor-pointer border-1 border-[var(--background-color)]'
        ],
        container: 'flex items-center',
        thumb: 'data-[state=unchecked]:bg-[var(--background-color)] data-[state=checked]:bg-[var(--background-color)] rounded-full border shadow-lg transition-transform duration-400 data-[state=unchecked]:translate-x-1 data-[state=checked]:translate-x-8 w-7 h-7',
        icon: [
          'absolute shrink-0 group-data-[state=checked]:text-muted group-data-[state=unchecked]:text-highlighted opacity-0 size-10/12',
          'transition-[color,opacity] duration-200'
        ]
      }"
    />
  </div>
</template>

<script setup lang="ts">
const colorMode = useColorMode()

const isDarkMode = computed({
  get: () => colorMode.preference === 'dark',
  set: (val: boolean) => {
    colorMode.preference = val ? 'dark' : 'light'
  }
})
</script>

