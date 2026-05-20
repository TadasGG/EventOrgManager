<template>
  <div
      v-if="loading"
      class="fixed inset-0 w-screen h-screen bg-white dark:bg-black transition-colors duration-300 z-[9999]"
  ></div>

  <UApp v-else :toaster="toaster">
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </UApp>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, provide } from "vue"
import { useAuth, useHead, useRuntimeConfig } from "#imports"
import { process } from "std-env"

const toaster = { position: "bottom-right" }

const { setLocale } = useI18n()
onMounted(() => {
  const saved = localStorage.getItem("locale")
  if (saved) setLocale(saved as any)
})

const loading = ref(true)

type UiTheme = {
  id: string | number
  primaryColor: string
  secondaryColor: string
  buttonColor: string
  objectColor: string
}

type StyleInfo = {
  id: string | number
  websiteName: string
  websiteLogo: string
  uiThemes: UiTheme
}

const styleInfo = ref<StyleInfo>({
  id: "",
  websiteName: "",
  websiteLogo: "",
  uiThemes: {
    id: "",
    primaryColor: "#fff",
    secondaryColor: "#fff",
    buttonColor: "#fff",
    objectColor: "#fff"
  }
})

const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token

function darken(hex: string, amount = 0.3) {
  try {
    const num = parseInt(hex.replace("#", ""), 16)
    let r = (num >> 16) & 0xff
    let g = (num >> 8) & 0xff
    let b = num & 0xff
    r = Math.min(255, Math.max(0, r - 255 * amount))
    g = Math.min(255, Math.max(0, g - 255 * amount))
    b = Math.min(255, Math.max(0, b - 255 * amount))
    return `#${(1 << 24 | (Math.round(r) << 16) | (Math.round(g) << 8) | Math.round(b))
        .toString(16)
        .slice(1)}`
  } catch {
    return hex
  }
}

async function findWebsiteStyle() {
  const response = await fetch(`${apiBase}/public/styles`, {
    method: "GET"
  })
  if (!response.ok) throw new Error("Failed to fetch styles")
  const data = (await response.json()) as StyleInfo

  styleInfo.value = {
    ...styleInfo.value,
    ...data,
    uiThemes: data.uiThemes ?? styleInfo.value.uiThemes
  }
}

function updateFavicon(url: string) {
  if (!process.client || !url) return
  let link = document.querySelector("link[rel~='icon']") as HTMLLinkElement
  if (!link) {
    link = document.createElement("link")
    link.rel = "icon"
    link.type = "image/png"
    document.head.appendChild(link)
  }
  link.href = url
}

function applyTheme() {
  if (!process.client) return

  const root = document.documentElement
  const theme = styleInfo.value.uiThemes

  root.style.setProperty("--primary-color-light", theme.primaryColor)
  root.style.setProperty("--secondary-color-light", theme.secondaryColor)
  root.style.setProperty("--button-color-light", theme.buttonColor)
  root.style.setProperty("--object-color-light", theme.objectColor)
  root.style.setProperty("--secondary-object-color-light", darken(theme.secondaryColor, -0.4))

  root.style.setProperty("--primary-color-dark", darken(theme.primaryColor, 0.35))
  root.style.setProperty("--secondary-color-dark", darken(theme.secondaryColor, 0.35))
  root.style.setProperty("--button-color-dark", darken(theme.buttonColor, 0.35))
  root.style.setProperty("--object-color-dark", darken(theme.objectColor, 0.35))
  root.style.setProperty("--secondary-object-color-dark", darken(theme.secondaryColor, -0.15))

  const logo = styleInfo.value.websiteLogo
  const faviconUrl = logo ? `${apiBase}${logo}` : "data:,"
  updateFavicon(faviconUrl)
}

async function refreshStyles() {
  try {
    await findWebsiteStyle()
    applyTheme()
  } catch (err) {
    console.error("refreshStyles failed:", err)
  }
}

provide("refreshStyles", refreshStyles)
provide("applyTheme", applyTheme)

onMounted(async () => {
  document.body.style.visibility = "hidden"

  await refreshStyles()

  await nextTick()
  document.body.style.visibility = "visible"
  loading.value = false
})

useHead({
  title: () => styleInfo.value.websiteName,
  link: [
    {
      rel: "icon",
      type: "image/png",
      href: styleInfo.value.websiteLogo ? `${apiBase}${styleInfo.value.websiteLogo}` : "data:,"
    }
  ]
})
</script>

<style>
html,
body {
  transition: background-color 0.3s ease, color 0.3s ease;
  margin: 0;
  padding: 0;
  min-height: 100%;
}
</style>
