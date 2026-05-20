<template>
  <div
      class="admin-wrapper"
      :style="{
        '--primary-color-light': primaryColor,
        '--secondary-color-light': secondaryColor,
        '--button-color-light': buttonColor,
        '--object-color-light': objectColor,
        '--primary-color-dark': darken(primaryColor, 0.35),
        '--secondary-color-dark': darken(secondaryColor, 0.35),
        '--button-color-dark': darken(buttonColor, 0.35),
        '--object-color-dark': darken(objectColor, 0.35)
      }"

  >
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.adminPanel') }}</h1>
      </div>
    </header>

    <main class="mx-auto w-full">
      <div class="secondary-color w-1/2 rounded-t-xl px-2 pt-2">
        <UTabs
            v-model="activeTab"
            :items="tabItems"
            class="w-full"
        />
      </div>

      <template v-if="activeTab === 0">
        <!-- Website Information -->
        <section class="secondary-color shadow-lg rounded-xl rounded-tl-none p-8">
          <div class="grid grid-cols-1 md:grid-cols-[1fr_auto_1fr] gap-8">
            <!-- Left Column -->
            <div>
              <h2 class="text-xl font-semibold mb-4">{{ t('admin.webInfo') }}</h2>
              <div class="flex flex-col gap-4">
                <!-- LOGO -->
                <div class="flex flex-col">
                  <div
                      class="flex self-end mr-2 w-[256px] h-[256px] border rounded-lg items-center justify-center overflow-hidden"
                  >
                    <!-- New file preview -->
                    <img
                        v-if="previewLogo"
                        :src="previewLogo"
                        alt="Logo"
                        class="max-h-full max-w-full object-contain"
                    />
                    <!-- Backend logo -->
                    <img
                        v-else-if="styleInfo.websiteLogo"
                        :src="`${apiBase}${styleInfo.websiteLogo}`"
                        alt="Logo"
                        class="max-h-full max-w-full object-contain"
                    />
                    <!-- No logo -->
                    <span v-else class="primary-color">Logo</span>
                  </div>
                </div>

                <!-- Website Title -->
                <div class="flex flex-col gap-4 items-end">
                  <UFormField class="flex flex-col items-start" :label="t('admin.logo')" name="logo">
                    <UInput
                        ref="logoInputRef"
                        placeholder="Logo"
                        type="file"
                        accept="image/*"
                        size="xl"
                        class="w-100"
                        @change="onFileSelected"
                    />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('admin.title')" name="title">
                    <UInput
                        class="w-100"
                        size="xl"
                        placeholder="..."
                        v-model="styleInfo.websiteName"
                    />
                  </UFormField>
                </div>
              </div>
            </div>

            <USeparator orientation="vertical" size="xs" class="hidden md:block" />

            <!-- Right Column -->
            <div>
              <h2 class="text-xl font-semibold mb-4">{{ t('admin.webStyles') }}</h2>

              <UFormField name="uiTheme">
                <USelect
                    v-model="selectedThemeId"
                    :items="availableThemes"
                    option-attribute="label"
                    value-attribute="value"
                    :placeholder="t('admin.selectTheme')+ '...' "
                    class="w-[350px]"
                    size="xl"
                />
              </UFormField>
            </div>
          </div>

          <!-- Submit button -->
          <div class="flex justify-end">
            <UButton class="px-4 py-2 mt-5 button-item rounded-lg" @click="submitWebStyleInfo">{{ t('misc.submit') }}</UButton>
          </div>
        </section>
      </template>

      <template v-if="activeTab === 1">
        <!-- Company Information -->
        <section class="secondary-color shadow-lg rounded-xl rounded-tl-none p-8">
          <UForm class="grid grid-cols-1 md:grid-cols-[1fr_auto_1fr] gap-8" :validate="validateCompanyInfo" :state="companyInfo" @error="onCompanyInfoError" @submit="onCompanyInfoSubmit">
            <!-- Left Column -->
            <div>
              <h2 class="text-xl font-semibold mb-4">{{ t('admin.generalCompanyInfo') }}</h2>
              <div class="flex flex-col gap-4 items-center">
                <!-- Company Name -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.companyName')" name="companyName">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.companyName" />
                </UFormField>
                <!-- Company Address -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.companyAddress')" name="companyAddress">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.companyAddress" />
                </UFormField>
                <!-- Company City -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.companyCity')" name="companyCity">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.companyCity" />
                </UFormField>
                <!-- Company Country -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.companyCountry')" name="companyCountry">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.companyCountry" />
                </UFormField>
                <!-- Company Number -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.companyNumber')" name="companyNumber">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.companyNumber" />
                </UFormField>
                <!-- Company VAT Number -->
                <UFormField class="flex flex-col items-start" :label="t('admin.companyVatNumber')" name="companyVatNumber">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.companyVatNumber" />
                </UFormField>
              </div>
            </div>

            <USeparator orientation="vertical" size="xs" class="hidden md:block" />

            <!-- Right Column -->
            <div>
              <h2 class="text-xl font-semibold mb-4">{{ t('admin.financialCompanyInfo') }}</h2>
              <div class="flex flex-col gap-4 items-center">
                <!-- Company Name -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.bankCode')" name="bankCode">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.bankCode" />
                </UFormField>
                <!-- Company Address -->
                <UFormField required class="flex flex-col items-start" :label="t('admin.bankAccountCode')" name="bankAccountCode">
                  <UInput class="w-[350px]" variant="input" size="xl" v-model="companyInfo.bankAccountCode" />
                </UFormField>
              </div>
            </div>

            <!-- Submit button -->
            <div class="flex col-span-full justify-end mt-4">
              <UButton type="submit" class="px-4 py-2 rounded-lg">{{ t('misc.submit') }}</UButton>
            </div>
          </UForm>
        </section>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, reactive, watch } from "vue"
import { useAuth } from "#imports"
import type { FormError, FormErrorEvent, FormSubmitEvent, TabsItem } from "@nuxt/ui"

definePageMeta({ middleware: "auth-admin" })

const { public: { apiBase } } = useRuntimeConfig()
const { t } = useI18n()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const router = useRouter()
const toast = useToast()

const refreshStyles = inject<() => Promise<void>>("refreshStyles")

const tabItems = computed<TabsItem[]>(() => ([
  { label: t('admin.webInfo'), icon: 'streamline-plump:browser-website-1-solid', value: 0 },
  { label: t('admin.companyInfo'), icon: 'mdi:company', value: 1 }
]))
const activeTab = ref(0)

const state = reactive({
  companyName: undefined,
  companyAddress: undefined,
  companyCity: undefined,
  companyCountry: undefined,
  companyNumber: undefined,
  companyVatNumber: undefined,
  bankCode: undefined,
  bankAccountCode: undefined
})

const validateCompanyInfo = (state: any): FormError[] => {
  const errors: FormError[] = []
  if (!state.companyName) errors.push({ name: 'companyName', message: t('errors.required') })
  if (!state.companyAddress) errors.push({ name: 'companyAddress', message: t('errors.required') })
  if (!state.companyCity) errors.push({ name: 'companyCity', message: t('errors.required') })
  if (!state.companyCountry) errors.push({ name: 'companyCountry', message: t('errors.required') })
  if (!state.companyNumber) errors.push({ name: 'companyNumber', message: t('errors.required') })
  if (!state.bankCode) errors.push({ name: 'bankCode', message: t('errors.required') })
  if (!state.bankAccountCode) errors.push({ name: 'bankAccountCode', message: t('errors.required') })
  return errors
}

async function onCompanyInfoError(event: FormErrorEvent) {
  if (event?.errors?.[0]?.id) {
    const element = document.getElementById(event.errors[0].id)
    element?.focus()
    element?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

type UiTheme = {
  id: number
  themeName: string
  primaryColor: string
  secondaryColor: string
  buttonColor: string
  objectColor: string
}

type StyleInformation = {
  id: number
  websiteName: string
  websiteLogo: string
  uiThemes: UiTheme | null
}

type ThemeOption = { label: string; value: string; theme: UiTheme }

const availableThemes = ref<ThemeOption[]>([])
const selectedThemeId = ref<string>("")

const styleInfo = ref<StyleInformation>({
  id: 0,
  websiteName: "",
  websiteLogo: "",
  uiThemes: null
})

const logoInputRef = ref<any>(null)
const selectedFile = ref<File | null>(null)
const previewLogo = ref<string | null>(null)

function onFileSelected() {
  const nativeInput = logoInputRef.value?.$el?.querySelector(
      'input[type="file"]'
  ) as HTMLInputElement

  if (nativeInput?.files?.length) {
    selectedFile.value = nativeInput.files[0]
    previewLogo.value = URL.createObjectURL(selectedFile.value)
  } else {
    selectedFile.value = null
    previewLogo.value = null
  }
}

function darken(hex: string, amount = 0.3) {
  try {
    if (!hex?.startsWith("#") || hex.length < 7) return hex
    const num = parseInt(hex.slice(1), 16)
    let r = (num >> 16) & 0xff
    let g = (num >> 8) & 0xff
    let b = num & 0xff

    r = Math.max(0, Math.min(255, Math.round(r - 255 * amount)))
    g = Math.max(0, Math.min(255, Math.round(g - 255 * amount)))
    b = Math.max(0, Math.min(255, Math.round(b - 255 * amount)))

    return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)}`
  } catch {
    return hex
  }
}

async function fetchAllThemes() {
  try {
    const res = await fetch(`${apiBase}/public/styles/themes`, { method: "GET" })
    if (!res.ok) throw new Error("Failed to fetch themes")
    const themes = (await res.json()) as UiTheme[]

    availableThemes.value = (themes || []).map((th) => ({
      label: th.themeName ?? `Theme #${th.id}`,
      value: String(th.id),
      theme: th
    }))
  } catch (e) {
    console.error(e)
    availableThemes.value = []
  }
}

async function findWebsiteStyle() {
  try {
    const response = await fetch(`${apiBase}/public/styles`)
    if (!response.ok) throw new Error("Failed to fetch style")
    const styleData = (await response.json()) as StyleInformation

    styleInfo.value = {
      id: styleData.id,
      websiteName: styleData.websiteName ?? "",
      websiteLogo: styleData.websiteLogo ?? "",
      uiThemes: styleData.uiThemes ?? null
    }

    if (styleInfo.value.uiThemes?.id != null) {
      selectedThemeId.value = String(styleInfo.value.uiThemes.id)
    }
  } catch (err) {
    console.error("Error fetching website style:", err)
  }
}

watch(
    () => selectedThemeId.value,
    (newId) => {
      if (!newId) return
      const opt = availableThemes.value.find(o => o.value === newId)
      if (!opt) return

      styleInfo.value.uiThemes = opt.theme
    }
)

const primaryColor = computed(() => styleInfo.value.uiThemes?.primaryColor ?? "#2563eb")
const secondaryColor = computed(() => styleInfo.value.uiThemes?.secondaryColor ?? "#f3f4f6")
const buttonColor = computed(() => styleInfo.value.uiThemes?.buttonColor ?? "#3b82f6")
const objectColor = computed(() => styleInfo.value.uiThemes?.objectColor ?? "#e5e7eb")

const companyInfo = ref({
  id: "",
  companyName: "",
  companyAddress: "",
  companyCity: "",
  companyCountry: "",
  companyNumber: "",
  companyVatNumber: "",
  bankCode: "",
  bankAccountCode: ""
})

async function findCompanyInfo() {
  try {
    const response = await fetch(`${apiBase}/admin/company-info`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error("Failed to fetch information!")
    const responseCompanyInfo = await response.json()
    companyInfo.value = { ...responseCompanyInfo, id: responseCompanyInfo.id }
  } catch (error) {
    console.error(error)
  }
}

async function submitWebStyleInfo() {
  try {
    let logoUrl = styleInfo.value.websiteLogo

    if (selectedFile.value) {
      const formData = new FormData()
      formData.append("file", selectedFile.value)

      const uploadRes = await fetch(
          `${apiBase}/public/styles/uploadLogo/${styleInfo.value.id}`,
          {
            method: "POST",
            headers: { Authorization: `Bearer ${userToken}` },
            body: formData
          }
      )

      if (!uploadRes.ok) throw new Error("Logo upload failed.")
      const updatedStyle = await uploadRes.json()
      logoUrl = updatedStyle.websiteLogo
      styleInfo.value.websiteLogo = logoUrl
    }

    const response = await fetch(`${apiBase}/public/styles/${styleInfo.value.id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${userToken}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        websiteName: styleInfo.value.websiteName,
        websiteLogo: logoUrl,
        uiThemes: styleInfo.value.uiThemes
            ? { id: styleInfo.value.uiThemes.id }
            : null
      })
    })

    if (!response.ok) throw new Error("Failed to update style information!")

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.webStyleUpdated'),
      icon: 'qlementine-icons:success-12'
    })
    router.push('/admin')
    await refreshStyles?.()

  } catch (error) {
    console.error(error)
  }
}

async function onCompanyInfoSubmit(event: FormSubmitEvent<typeof state>) {
  try {
    const response = await fetch(`${apiBase}/admin/company-info/${companyInfo.value.id}`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        companyName: companyInfo.value.companyName,
        companyAddress: companyInfo.value.companyAddress,
        companyCity: companyInfo.value.companyCity,
        companyCountry: companyInfo.value.companyCountry,
        companyNumber: companyInfo.value.companyNumber,
        companyVatNumber: companyInfo.value.companyVatNumber,
        bankCode: companyInfo.value.bankCode,
        bankAccountCode: companyInfo.value.bankAccountCode,
      }),
    })

    if (!response.ok) throw new Error('Failed to update company information!')

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.companyInformationUpdated'),
      icon: 'qlementine-icons:success-12'
    })
    router.push('/admin')
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await Promise.all([findWebsiteStyle(), findCompanyInfo(), fetchAllThemes()])

  if (!selectedThemeId.value && styleInfo.value.uiThemes?.id != null) {
    selectedThemeId.value = String(styleInfo.value.uiThemes.id)
  }
})

defineExpose({ darken, primaryColor, secondaryColor, buttonColor, objectColor })
</script>


