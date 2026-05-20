<template>
  <div class="flex flex-col min-h-screen">
    <!-- Header Section -->
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">
          {{ t('titles.itemInfo') }}
        </h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <div class="flex flex-col items-center gap-8">
        <!-- Category -->
        <div class="flex flex-col items-start">
          <label class="mx-2 font-medium text-right mb-1">{{ t('items.itemCategory') }}</label>
          <select
              class="w-[285px] rounded-lg p-2 secondary-object-item border"
              v-model="selectedCategoryId"
          >
            <option disabled value="">{{ t('items.selectItemCategory') }}</option>
            <option
                v-for="cat in categories"
                :key="cat.id"
                :value="String(cat.id)"
            >
              {{ cat.name }}
            </option>
          </select>
        </div>

        <!-- Name -->
        <div class="flex flex-col items-start">
          <div class="flex justify-between">
            <label class="mx-3 font-medium text-right mb-1">{{ t('items.itemTitle') }}</label>
            <p v-for="(error, index) in errorMessages" :key="index" class="text-center text-red-500 px-3">{{ error }}</p>
          </div>
          <UInput
              class="w-[300px] rounded-lg p-2"
              size="xl"
              placeholder="..."
              v-model="item.name"
          />
        </div>

        <!-- Description -->
        <div class="flex flex-col items-start">
          <label class="mx-3 font-medium text-right mb-1">{{ t('items.itemDesc') }}</label>
          <UTextarea
              class="w-[300px] rounded-lg p-2"
              size="xl"
              placeholder="..."
              v-model="item.description"
          />
        </div>
      </div>

      <!-- Submit button -->
      <div class="flex justify-between mt-4">
        <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/items`">{{ t('misc.back') }}</UButton>
        <UButton class="px-4 py-2 rounded-lg" @click="submitInfo">{{ t('misc.submit') }}</UButton>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: 'auth-admin'
})

import { onMounted, ref } from 'vue'
import { useAuth } from '#imports'

const route = useRoute()
const returnTo = computed(() => route.query.callback as string | undefined)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()

const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const router = useRouter();
const toast = useToast();

const item = ref({
  name: '',
  description: ''
})

const categories = ref([])
const selectedCategoryId = ref('')
const errorMessages = ref([])

const getCategories = async () => {
  try {
    const response = await fetch(`${apiBase}/admin/items-categories`, {
      headers: { 'Authorization': `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error('Failed to fetch categories')

    const data = await response.json()
    categories.value = data.map((c: any) => ({ id: c.id, name: c.name }))
  } catch (error) {
    console.error('Error fetching categories:', error)
  }
}

const submitInfo = async () => {
  errorMessages.value = []

  if (!item.value.name) errorMessages.value.push('Name is required!')
  if (errorMessages.value.length) return

  const requestBody = {
    name: item.value.name,
    description: item.value.description,
    itemCategory: selectedCategoryId.value
        ? { id: Number(selectedCategoryId.value) }
        : null
  }

  try {
    const response = await fetch(`${apiBase}/admin/items`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestBody)
    })

    if (!response.ok) {
      throw new Error('Failed to add item!')
    }

    toast.add({
      title: 'Success!',
      description: `An new item has been successfully added!`,
      icon: 'qlementine-icons:success-12'
    })
    router.push(returnTo.value || '/items')

  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getCategories()
})
</script>
