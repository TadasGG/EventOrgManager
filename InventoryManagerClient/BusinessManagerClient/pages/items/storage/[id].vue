<template>
  <div class="flex flex-col min-h-screen">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.itemInfo') }}</h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <h2 class="text-xl font-semibold mb-4 text-start">{{ t('storage.itemsList') }}</h2>

      <div class="flex flex-col gap-8">
        <!-- Item list -->
        <div class="flex-col secondary-object-item rounded-xl items-center">
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

            <div>
              <div v-if="isLoading" class="grid gap-4 mt-4">
                <p>Loading...</p>
              </div>

              <div v-else class="grid gap-2 mt-4">
                <div class="grid gap-4 w-full">
                  <p class="font-bold">{{ t('items.itemTitle') }} - {{ t('items.itemDesc') }}</p>

                  <div
                      v-for="(items, categoryName) in groupedItemsList"
                      :key="categoryName"
                      class="grid gap-4 w-full"
                  >
                    <USeparator class="font-bold text-lg mb-2" color="neutral" size="xs">
                      {{ categoryName }}
                    </USeparator>

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
            <UInputNumber
                id="quantity-input"
                class="w-[300px] rounded-lg p-2"
                size="xl"
                :v-model="item.quantity"
                :min="0"
            />
          </div>
        </div>
      </div>

      <!-- Submit -->
      <div class="flex justify-between mt-8">
        <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/items/storage`">{{ t('misc.back') }}</UButton>
        <UButton class="px-4 py-2 rounded-lg" :disabled="!selectedItemId" @click="submitInfo">
          {{ t('misc.submit') }}
        </UButton>
      </div>

      <!-- Errors -->
      <div class="text-red-500 text-center mt-4">
        <p v-for="(error, index) in errorMessages" :key="index">{{ error }}</p>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
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
const isLoading = ref(false)

const searchQuery = ref<string>(route.query.q ? String(route.query.q) : '');

watch(searchQuery, (val) => {
  const q = val?.trim() || undefined
  router.replace({ query: { ...route.query, q } })
})

watch(() => route.query.q, (q) => {
  const v = q ? String(q) : ''
  if (v !== searchQuery.value) searchQuery.value = v
})

const { id } = useRoute().params
const page_id = Array.isArray(id) ? Number(id[0]) : Number(id)

const selectedItemId = ref<number | null>(null)

const storedItems = ref<Array<{ id: number; rentPrice: number; availability: boolean; item: any }>>([])

const item = ref({
  rent_price: '',
  quantity: '',
  availability: ''
})

const errorMessages = ref<string[]>([])

async function findItems () {
  isLoading.value = true
  try {
    const res = await fetch(`${apiBase}/admin/items`, {
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch items!')
    const data = await res.json()
    itemsList.value = data.sort((a: any, b: any) => {
      const ca = a.itemCategory?.name?.toLowerCase() || 'zzzz'
      const cb = b.itemCategory?.name?.toLowerCase() || 'zzzz'
      if (ca !== cb) return ca.localeCompare(cb)
      return (a.name || '').toLowerCase().localeCompare((b.name || '').toLowerCase())
    })
  } finally {
    isLoading.value = false
  }
}

async function loadStoredItemsForSelected () {
  storedItems.value = []
  item.value.quantity = ''
  item.value.rent_price = ''

  if (!selectedItemId.value) return

  const url = `${apiBase}/admin/stored-items?itemId=${selectedItemId.value}`

  try {
    const res = await fetch(url, { headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) throw new Error('Failed to fetch stored items!')
    const list = await res.json()

    const onlyThisItem = (list || []).filter((s: any) => Number(s?.item?.id) === selectedItemId.value)

    storedItems.value = onlyThisItem.map((s: any) => ({
      id: s.id,
      rentPrice: Number(s.rentPrice),
      availability: Boolean(s.availability),
      item: s.item
    }))

    item.value.quantity = String(storedItems.value.length)
    const firstPrice = storedItems.value[0]?.rentPrice
    item.value.rent_price = Number.isFinite(firstPrice) ? String(firstPrice) : ''
  } catch (e) {
    console.error(e)
  }
}

function setSelectedItemById (itemId: number) {
  const exists = itemsList.value.some(it => it.id === itemId)
  selectedItemId.value = exists ? itemId : null
}

const selectedItem = computed(() => {
  if (selectedItemId.value == null) return null
  return itemsList.value.find(it => it.id === selectedItemId.value) ?? null
})

function selectItem (id: number) {
  selectedItemId.value = selectedItemId.value === id ? null : id
}

const groupedItemsList = computed(() => {
  const q = searchQuery.value.toLowerCase()
  const filtered = itemsList.value.filter((it: any) =>
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
  const qty = Number(item.value.quantity)
  if (!Number.isFinite(qty) || qty < 0) errorMessages.value.push('Quantity must be 0 or more.')
  const newPrice = Number(item.value.rent_price)
  if (!Number.isFinite(newPrice) || newPrice < 0) errorMessages.value.push('Rent price must be 0 or more.')
  if (errorMessages.value.length) return

  const currentQty = storedItems.value.length
  const targetQty = qty

  try {
    if (targetQty > currentQty) {
      const fullItem = itemsList.value.find(it => it.id === selectedItemId.value)
      for (let i = 0; i < targetQty - currentQty; i++) {
        const body = { item: fullItem, rentPrice: newPrice, availability: true }
        const res = await fetch(`${apiBase}/admin/stored-items`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${userToken}`, 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        })
        if (!res.ok) throw new Error('Failed to add stored item')
      }
    }
    else if (targetQty < currentQty) {
      const toRemove = currentQty - targetQty
      const ids = storedItems.value.slice(-toRemove).map(s => s.id)
      for (const sid of ids) {
        const res = await fetch(`${apiBase}/admin/stored-items/${sid}`, {
          method: 'DELETE',
          headers: { Authorization: `Bearer ${userToken}` }
        })
        if (!res.ok) throw new Error('Failed to delete stored item')
      }
    }

    await loadStoredItemsForSelected()

    const needsUpdate = storedItems.value.some(s => Number(s.rentPrice) !== newPrice)
    if (needsUpdate) {
      for (const s of storedItems.value) {
        if (Number(s.rentPrice) === newPrice) continue
        const body = { item: s.item, availability: s.availability, rentPrice: newPrice }
        const res = await fetch(`${apiBase}/admin/stored-items/${s.id}`, {
          method: 'PUT',
          headers: { Authorization: `Bearer ${userToken}`, 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        })
        if (!res.ok) throw new Error('Failed to update stored item price')
      }
    }

    await loadStoredItemsForSelected()

    toast.add({
      title: 'Success!',
      description: `An item has successfully been updated!`,
      icon: 'qlementine-icons:success-12'
    })
    router.push(returnTo.value || '/items/storage')

  } catch (e: any) {
    console.error(e)
    errorMessages.value.push(e?.message || 'Failed to save changes.')
  }
}

onMounted(async () => {
  await findItems()
  setSelectedItemById(page_id)
})

watch(selectedItemId, async (id) => {
  if (!id) {
    storedItems.value = []
    item.value.quantity = ''
    item.value.rent_price = ''
    return
  }
  await loadStoredItemsForSelected()
})
</script>
