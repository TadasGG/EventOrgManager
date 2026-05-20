<template>
  <div class="flex flex-col min-h-screen">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.itemInfo') }}</h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <div class="flex flex-col items-center gap-8">
        <!-- Category -->
        <div class="flex flex-col items-start">
          <label class="mx-2 font-medium text-right mb-3">{{ t('items.itemCategory') }}</label>
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
          <label class="mx-3 font-medium text-right mb-1">{{ t('items.itemTitle') }}</label>
          <UInput
              class="w-[300px] rounded-lg p-2"
              size="xl"
              placeholder="Name"
              v-model="item.name"
          />
        </div>

        <!-- Description -->
        <div class="flex flex-col items-start">
          <label class="mx-3 font-medium text-right mb-1">{{ t('items.itemDesc') }}</label>
          <UTextarea
              class="w-[300px] rounded-lg p-2"
              size="xl"
              placeholder="Description"
              v-model="item.description"
          />
        </div>
      </div>

      <div class="flex justify-between mt-4">
        <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/items`">{{ t('misc.back') }}</UButton>
        <UButton class="px-4 py-2 rounded-lg" @click="submitInfo">{{ t('misc.submit') }}</UButton>
      </div>

      <div v-if="errorMessages.length" class="mt-4 text-red-600">
        <ul>
          <li v-for="(message, index) in errorMessages" :key="index">{{ message }}</li>
        </ul>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth-admin' })

import { ref, onMounted } from 'vue'
import { useAuth } from '#imports'

const route = useRoute()
const returnTo = computed(() => route.query.callback as string | undefined)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const router = useRouter()
const toast = useToast();

const item = ref({
  id: '',
  name: '',
  description: '',
  itemCategory: null as null | { id: number; name?: string }
})

const categories = ref<Array<{ id: number; name: string }>>([])
const selectedCategoryId = ref<string>('')
const errorMessages = ref<string[]>([])
const { id } = useRoute().params

const getCategories = async () => {
  try {
    const res = await fetch(`${apiBase}/admin/items-categories`, {
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch categories')
    const data = await res.json()
    categories.value = data.map((c: any) => ({ id: c.id, name: c.name }))
  } catch (e) {
    console.error('Error fetching categories:', e)
  }
}

const findItem = async () => {
  try {
    const res = await fetch(`${apiBase}/admin/items/${id}`, {
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch item!')
    const itemData = await res.json()

    item.value = {
      id: itemData.id,
      name: itemData.name || '',
      description: itemData.description || '',
      itemCategory: itemData.itemCategory ? { id: itemData.itemCategory.id, name: itemData.itemCategory.name } : null
    }

    selectedCategoryId.value = item.value.itemCategory?.id ? String(item.value.itemCategory.id) : ''
  } catch (e) {
    console.error('Error fetching item:', e)
  }
}

const submitInfo = async () => {
  errorMessages.value = []

  const categoryId = selectedCategoryId.value ? Number(selectedCategoryId.value) : null

  const requestBody = {
    id: item.value.id,
    name: item.value.name,
    description: item.value.description,
    itemCategory: categoryId ? { id: categoryId } : null
  }

  try {
    const res = await fetch(`${apiBase}/admin/items/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${userToken}`
      },
      body: JSON.stringify(requestBody)
    })

    const text = await res.text()
    if (!res.ok) {
      let msg = 'Failed to update item information!'
      try {
        const parsed = JSON.parse(text)
        msg = parsed.message || msg
      } catch {}
      throw new Error(msg)
    }

    toast.add({
      title: 'Success!',
      description: `An new item has been successfully updated!`,
      icon: 'qlementine-icons:success-12'
    })
    router.push(returnTo.value || '/items')

  } catch (e: any) {
    console.error(e)
    errorMessages.value.push(e?.message || 'An error occurred while updating item information.')
  }
}

onMounted(async () => {
  await getCategories()
  await findItem()
})
</script>