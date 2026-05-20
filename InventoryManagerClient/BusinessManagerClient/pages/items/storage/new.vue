<template>
  <div class="flex flex-col min-h-screen">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.storeNewItems') }}</h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <h2 class="text-xl font-semibold mb-4 text-start">{{ t('storage.itemInfo') }}</h2>

      <div class="flex flex-col gap-8">
        <!-- Item -->
        <div class="flex-col items-center secondary-object-item rounded-xl">
          <div class="object-color flex-col rounded-lg p-4">
            <div class="ml-auto w-full">
              <UInput
                  icon="i-heroicons-magnifying-glass-20-solid"
                  size="xl"
                  v-model="searchQuery"
                  type="text"
                  :placeholder="t('misc.search') + '...'"
                  class="w-full"
              />
            </div>

            <!-- ITEMS -->
            <div>
              <div v-if="isLoading" class="grid gap-4 mt-4">
                <p>Loading...</p>
              </div>

              <div v-else class="grid gap-2 mt-4">
                <div v-if="Object.keys(groupedItemsList).length" class="grid gap-4 w-full">
                  <p class="font-bold">{{ t('items.itemTitle') }} - {{ t('items.itemDesc') }}</p>

                  <div
                      v-for="(items, categoryName) in groupedItemsList"
                      :key="categoryName"
                      id="category-sort"
                      class="grid gap-4 w-full"
                  >
                    <USeparator class="font-bold text-lg mb-2" size="xs" color="neutral">{{ categoryName }}</USeparator>

                    <UButton
                        v-for="it in items"
                        :key="it.id"
                        :variant="selectedItemId === it.id ? 'selectedButton' : 'button'"
                        class="flex justify-between p-2"
                        @click="selectItem(it.id)"
                    >
                      {{ it.name }} - {{ it.description }}
                    </UButton>
                  </div>
                </div>

                <div v-else class="text-center opacity-70 py-6">
                  {{ t('alerts.noItemsToStore') }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <h2 class="mx-3">
          Selected item:
          <span v-if="selectedItem">{{ selectedItem.name }}</span>
          <span v-else class="opacity-70">None</span>
        </h2>

        <!-- Price / Quantity -->
        <div class="flex justify-between">
          <div>
            <label class="mx-3 font-medium text-right mb-1">{{ t('storage.itemPrice') }} (€/{{ t('storage.pcs') }})</label>
            <UInput
                id="rent-price-input"
                class="w-[300px] rounded-lg p-2"
                size="xl"
                v-model="item.rent_price"
                type="number"
                min="0"
                step="0.01"
            />
          </div>

          <div>
            <label class="mx-3 font-medium text-right mb-1">{{ t('storage.quantity') }}</label>
            <UInput
                id="quantity-input"
                class="w-[300px] rounded-lg p-2"
                size="xl"
                v-model="item.quantity"
                type="number"
                min="1"
                step="1"
            />
          </div>
        </div>
      </div>

      <!-- Submit button -->
      <div class="flex justify-between mt-8">
        <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/items/storage`">{{ t('misc.back') }}</UButton>
        <UButton class="px-4 py-2 rounded-lg" :disabled="!selectedItemId" @click="submitInfo">
          {{ t('misc.submit') }}
        </UButton>
      </div>

      <!-- Error Messages -->
      <div class="text-red-500 text-center mt-4">
        <p v-for="(error, index) in errorMessages" :key="index">{{ error }}</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import {computed, ref, onMounted, watch} from 'vue'
import { useAuth } from '#imports'

definePageMeta({ middleware: 'auth-admin' })

const returnTo = computed(() => route.query.callback as string | undefined)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()
const route = useRoute()
const router = useRouter()
const toast = useToast();

const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token

const itemsList = ref<any[]>([])
const isLoading = ref<boolean>(false)
const errorMessages = ref<string[]>([])

const searchQuery = ref<string>(route.query.q ? String(route.query.q) : '');

watch(searchQuery, (val) => {
  const q = val?.trim() || undefined
  router.replace({ query: { ...route.query, q } })
})

watch(() => route.query.q, (q) => {
  const v = q ? String(q) : ''
  if (v !== searchQuery.value) searchQuery.value = v
})

const selectedItemId = ref<number | null>(null)

const storedItemIds = ref<Set<number>>(new Set())

const item = ref({
  rent_price: '',
  availability: '',
  quantity: ''
})

async function findItems () {
  isLoading.value = true
  try {
    const response = await fetch(`${apiBase}/admin/items`, {
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error('Failed to fetch items!')
    const data = await response.json()

    itemsList.value = data.sort((a: any, b: any) => {
      const categoryA = a.itemCategory?.name?.toLowerCase() || 'zzzz'
      const categoryB = b.itemCategory?.name?.toLowerCase() || 'zzzz'
      if (categoryA !== categoryB) return categoryA.localeCompare(categoryB)
      return (a.name || '').toLowerCase().localeCompare((b.name || '').toLowerCase())
    })
  } finally {
    isLoading.value = false
  }
}

async function findStoredItemIds () {
  const res = await fetch(`${apiBase}/admin/stored-items`, {
    headers: { Authorization: `Bearer ${userToken}` }
  })
  if (!res.ok) throw new Error('Failed to fetch stored items!')
  const list = await res.json()
  const ids = new Set<number>()
  for (const si of list || []) {
    const id = si?.item?.id
    if (typeof id === 'number') ids.add(id)
  }
  storedItemIds.value = ids
}

const selectedItem = computed(() => {
  if (selectedItemId.value == null) return null
  return itemsList.value.find(it => it.id === selectedItemId.value) ?? null
})

function selectItem (id: number) {
  selectedItemId.value = selectedItemId.value === id ? null : id
}

const availableItems = computed(() =>
    itemsList.value.filter(it => !storedItemIds.value.has(it.id))
)

const groupedItemsList = computed(() => {
  const q = searchQuery.value.toLowerCase()
  const filtered = availableItems.value.filter(it =>
      (it.name || '').toLowerCase().includes(q)
  )

  return filtered.reduce((acc: Record<string, any[]>, it: any) => {
    const key = it.itemCategory?.name || 'No Category'
    ;(acc[key] ||= []).push(it)
    return acc
  }, {})
})

async function submitInfo () {
  errorMessages.value = []

  if (!selectedItemId.value) errorMessages.value.push('Item selection is required.')

  const quantity = Number(item.value.quantity)
  if (!Number.isFinite(quantity) || quantity <= 0)
    errorMessages.value.push('Please enter a valid quantity greater than 0.')

  const price = Number(item.value.rent_price)
  if (!Number.isFinite(price) || price < 0)
    errorMessages.value.push('Please enter a valid price (0 or more).')

  if (errorMessages.value.length) return

  const fullItem = itemsList.value.find(it => it.id === selectedItemId.value)
  if (!fullItem) {
    errorMessages.value.push('Selected item not found.')
    return
  }

  for (let i = 0; i < quantity; i++) {
    const body = {
      item: fullItem,
      rentPrice: price,
      availability: true
    }
    const response = await fetch(`${apiBase}/admin/stored-items`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${userToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    })
    if (!response.ok) {
      const text = await response.text()
      console.error('Create stored item failed:', text)
      errorMessages.value.push(`Failed to add item on attempt ${i + 1}.`)
      return
    }
  }

  toast.add({
    title: 'Success!',
    description: `An new item has been successfully added!`,
    icon: 'qlementine-icons:success-12'
  })
  router.push(returnTo.value || '/items/storage')
}

onMounted(async () => {
  await Promise.all([findItems(), findStoredItemIds()])
})
</script>