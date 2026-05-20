<template>
  <div class="flex flex-col">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'" v-text="event.title" />
        <p class="text-center" style="font-family: 'Arial'" v-text="event.date" />
        <p class="text-center" style="font-family: 'Arial'" v-text="event.address" />
      </div>
    </header>

    <main class="flex flex-col gap-6">
      <section class="secondary-color shadow-lg rounded-xl p-8">
        <div class="grid grid-cols-1 md:grid-cols-[1fr_auto_1fr] gap-8">
          <div class="flex flex-col gap-4">
            <div class="flex items-center" v-for="(label, key) in timeFields" :key="key">
              <label class="font-medium w-1/3 text-right pr-4">{{ t(label) }}</label>
              <UInput disabled placeholder="00:00" class="w-2/3 p-2 rounded-lg" size="xl" v-model="event[key]" />
            </div>

            <div class="flex">
              <div class="w-1/3 text-right pr-4 mt-3">
                <label class="font-medium">{{ t('events.comments') }}</label>
              </div>
              <UTextarea
                  disabled
                  class="w-2/3 rounded-lg p-2"
                  :rows="5"
                  size="xl"
                  placeholder="..."
                  v-model="event.comments"
                  :maxlength="250"
              />
            </div>
          </div>

          <USeparator orientation="vertical" size="xs" class="hidden md:block" />

          <div class="flex flex-col">
            <UTabs
                v-model="activeTab"
                :items="tabItems"
                class="mt-2"
            />

            <template v-if="activeTab === 0">
              <!-- EMPLOYEES -->
              <div class="flex-col h-full secondary-object-item rounded p-2">
                <div class="flex justify-between items-center">
                  <label class="font-bold text-left">{{ t('events.employees') }}</label>
                </div>

                <div class="w-full rounded-md mt-3 object-color h-[565px] p-3 overflow-auto">
                  <div v-if="isLoading" class="grid gap-3">
                    <p>Loading...</p>
                  </div>
                  <div v-else class="grid gap-3 w-full">
                    <div
                        v-for="staff in staffList"
                        :key="staff.id"
                        class="flex items-center p-3 border rounded relative font-bold gap-2"
                    >
                      <UAvatar size="md" :alt="staff.firstName + ' ' + staff.lastName" />
                      <div>{{ staff.firstName }} {{ staff.lastName }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </template>

            <template v-if="activeTab === 1">
              <!-- ITEMS -->
              <div class="flex-col secondary-object-item rounded p-2">
                <div class="flex justify-between items-center">
                  <label class="font-bold text-left">{{ t('events.items') }}</label>
                </div>

                <div class="w-full rounded-md mt-3 object-color h-[565px] p-3 overflow-auto">
                  <div v-if="isLoading" class="grid gap-3">
                    <p>Loading...</p>
                  </div>
                  <div v-else class="grid gap-3 w-full">
                    <div
                        v-for="group in groupedItems"
                        :key="group.key"
                        class="flex justify-between items-center p-3 border rounded relative"
                        :class="{ 'bg-yellow-400 text-black': group.external }"
                    >
                      <div>
                        {{ group.name }} x {{ group.count }}
                        <span v-if="group.external" class="text-sm text-red-500 font-semibold ml-2">
                          ({{ t('events.rented') }})
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>

        <div class="flex justify-between mt-10">
          <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/events`">{{ t('misc.back') }}</UButton>
          <div v-if="isAdmin">
            <UButton class="px-4 py-2 rounded-lg" :to="`/events/${$route.params.id}/edit`">
              {{ t('misc.edit') }}
            </UButton>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth-events' })

import { ref, onMounted, computed } from 'vue'
import { useAuth, useRoute } from '#imports'
import type { TabsItem } from '@nuxt/ui'

const { t } = useI18n()
const route = useRoute()

const returnTo = computed(() => route.query.callback as string | undefined)

const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const { id } = route.params

const event = ref({
  title: '',
  date: '',
  address: '',
  meetupTime: '',
  arrivalTime: '',
  readyTime: '',
  soundCheckTime: '',
  guestTime: '',
  startTime: '',
  endTime: '',
  comments: '',
  staffList: [],
  id: ''
})

const isAdmin = ref(false)
const isLoading = ref(false)

const timeFields: Record<string, string> = {
  meetupTime: 'events.atWarehouse',
  arrivalTime: 'events.atLocation',
  soundCheckTime: 'events.soundCheck',
  readyTime: 'events.beReadyBy',
  guestTime: 'events.guestsArrive',
  startTime: 'events.eventStarts',
  endTime: 'events.eventEnds'
}

const tabItems = computed<TabsItem[]>(() => ([
  { label: t('events.employees'), icon: 'i-lucide-user', value: 0 },
  { label: t('events.items'), icon: 'i-lucide-box', value: 1 }
]))
const activeTab = ref(0)

const staffList = ref<any[]>([])

const itemList = ref<any[]>([])

type EventByDateDto = { id: number; date: string }

type StoredItemDto = {
  id: number
  externalRental?: boolean
  item: { id: number; name: string }
}

type WarehouseStoredItemDto = {
  id: number
  externalRental: boolean
  item: { id: number; name: string }
}

function toDateOnly(d: string): string {
  return d?.includes('T') ? d.split('T')[0] : d
}

const warehouseStock = ref<Map<number, number>>(new Map())

async function fetchWarehouseStock() {
  try {
    const res = await fetch(`${apiBase}/admin/stored-items`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch warehouse stored items')

    const storedItems = (await res.json()) as WarehouseStoredItemDto[]

    const map = new Map<number, number>()
    for (const si of storedItems) {
      const itemId = si.item?.id
      if (!itemId) continue

      if (si.externalRental === true) continue

      map.set(itemId, (map.get(itemId) || 0) + 1)
    }

    warehouseStock.value = map
  } catch (e) {
    console.error('fetchWarehouseStock failed:', e)
    warehouseStock.value = new Map()
  }
}

function groupByItemId(list: StoredItemDto[]) {
  const map = new Map<number, StoredItemDto[]>()
  for (const si of list) {
    const itemId = si.item?.id
    if (!itemId) continue
    if (!map.has(itemId)) map.set(itemId, [])
    map.get(itemId)!.push(si)
  }
  return map
}

async function applyRentalTags() {
  const date = toDateOnly(event.value.date)
  if (!date) return
  if (!itemList.value?.length) return

  try {
    if (warehouseStock.value.size === 0) {
      await fetchWarehouseStock()
    }

    const eventsRes = await fetch(`${apiBase}/admin/events/date/${encodeURIComponent(date)}`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!eventsRes.ok) throw new Error('Failed to fetch same-day events')
    const events = (await eventsRes.json()) as EventByDateDto[]

    const otherEvents = events.filter(e => String(e.id) !== String(id))

    const otherUsedByItemId = new Map<number, number>()
    if (otherEvents.length) {
      const responses = await Promise.all(
          otherEvents.map(e =>
              fetch(`${apiBase}/admin/events/${e.id}/items`, {
                method: 'GET',
                headers: { Authorization: `Bearer ${userToken}` }
              })
          )
      )
      const failed = responses.find(r => !r.ok)
      if (failed) throw new Error('Failed to fetch items for same-day events')

      const lists = (await Promise.all(responses.map(r => r.json()))) as StoredItemDto[][]
      for (const l of lists) {
        for (const si of l) {
          if (si.externalRental === true) continue
          const itemId = si.item?.id
          if (!itemId) continue
          otherUsedByItemId.set(itemId, (otherUsedByItemId.get(itemId) || 0) + 1)
        }
      }
    }

    const current = itemList.value as StoredItemDto[]
    const currentGrouped = groupByItemId(current)
    const updatedById = new Map<number, StoredItemDto>()

    for (const [itemId, arr] of currentGrouped.entries()) {
      const capacity = warehouseStock.value.get(itemId) || 0
      const alreadyUsedInternal = otherUsedByItemId.get(itemId) || 0
      const remaining = Math.max(capacity - alreadyUsedInternal, 0)

      const sorted = [...arr].sort((a, b) => a.id - b.id)

      for (let i = 0; i < sorted.length; i++) {
        const si = sorted[i]
        const shouldBeRental = i >= remaining
        updatedById.set(si.id, { ...si, externalRental: shouldBeRental })
      }
    }

    itemList.value = current.map(si => updatedById.get(si.id) || si)
  } catch (e) {
    console.error('applyRentalTags failed:', e)
  }
}

const groupedItems = computed(() => {
  const map = new Map<string, { key: string; itemId: number; name: string; count: number; external: boolean; storedItemIds: number[] }>()
  for (const storedItem of itemList.value) {
    const itemId = storedItem.item.id
    const isExternal = storedItem.externalRental === true
    const key = `${itemId}_${isExternal ? 'external' : 'internal'}`
    if (!map.has(key)) {
      map.set(key, {
        key,
        itemId,
        name: storedItem.item.name,
        count: 0,
        external: isExternal,
        storedItemIds: []
      })
    }
    const group = map.get(key)!
    group.count++
    group.storedItemIds.push(storedItem.id)
  }
  return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name))
})

const findEvent = async () => {
  try {
    const response = await fetch(`${apiBase}/admin/events/${id}`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error('Failed to fetch event!')
    const eventData = await response.json()
    event.value = { ...eventData, staffList: eventData.staffList || [], id: eventData.id }
  } catch (err: any) {
    const status = err?.status || err?.response?.status
    if (status === 404) {
      throw createError({ statusCode: 404, statusMessage: 'Event not found' })
    }
    throw err
  }
}

const findStaff = async () => {
  isLoading.value = true
  try {
    const res = await fetch(`${apiBase}/admin/events/${id}/employees`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch staff!')
    staffList.value = (await res.json()).sort((a: any, b: any) => a.lastName.localeCompare(b.lastName))
  } catch (e) {
    console.error(e)
  } finally {
    isLoading.value = false
  }
}

const findItems = async () => {
  isLoading.value = true
  try {
    const res = await fetch(`${apiBase}/admin/events/${id}/items`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch items!')
    itemList.value = await res.json()
  } catch (e) {
    console.error(e)
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([findEvent(), findStaff(), findItems()])

  if (!data.value?.user) return
  const userEmail = data.value.user.email
  const token = data.value.user.token

  try {
    const response = await fetch(`${apiBase}/admin/employees/roles/find?email=${userEmail}`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) throw new Error('Failed to fetch roles')
    const roles = await response.json()
    isAdmin.value = roles.some((role: any) => role.name === 'Admin')
  } catch (error) {
    console.error('Error fetching roles:', error)
  }

  await fetchWarehouseStock()
  await applyRentalTags()
})
</script>
