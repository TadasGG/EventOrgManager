<template>
  <div class="flex flex-col">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto flex flex-col items-center gap-2">
        <UInput
            v-model="event.title"
            size="xl"
            class="font-bold"
            style="font-family: 'Arial'; font-size: 2rem;"
            :placeholder="t('events.eventTitle')"
        />

        <UInput
            v-model="event.date"
            type="date"
            size="sm"
            class="text-center text-xl"
            style="font-family: 'Arial';"
            :placeholder="t('events.eventDate')"
        />

        <UInput
            v-model="event.address"
            size="sm"
            class="text-center text-xl w-1/5"
            style="font-family: 'Arial';"
            :placeholder="t('events.eventAddress')"
        />
      </div>
    </header>

    <main class="flex flex-col gap-6">
      <section class="secondary-color shadow-lg rounded-xl p-8">
        <div class="grid grid-cols-1 md:grid-cols-[1fr_auto_1fr] gap-8">
          <div class="flex flex-col gap-4">
            <div class="flex items-center" v-for="(label, key) in timeFields" :key="key">
              <label class="font-medium w-1/3 text-right pr-4">{{ t(label) }}</label>
              <UInput v-maska="'##:##'" class="w-2/3 p-2 rounded-lg" size="xl" placeholder="00:00" v-model="event[key]" />
            </div>
            <div class="flex">
              <div class="w-1/3 text-right pr-4 mt-3">
                <label class="font-medium">{{ t('events.comments') }}</label>
                <p style="font-size: 12px;">{{ event.comments.length }}/250</p>
              </div>
              <UTextarea class="w-2/3 rounded-lg p-2" :rows="5" size="xl" placeholder="..." v-model="event.comments" :maxlength="250" />
            </div>
          </div>

          <USeparator orientation="vertical" size="xs" class="hidden md:block" />

          <div class="flex flex-col gap-4">
            <UTabs
                v-model="activeTab"
                :items="tabItems"
                class="mt-2"
            />

            <!-- EMPLOYEES -->
            <template v-if="activeTab === 0">
              <div class="flex-col h-full secondary-object-item rounded p-2">
                <div class="flex justify-between items-center">
                  <label class="font-bold text-left">{{ t('events.employees') }}</label>
                  <div class="relative inline-block">
                    <UButton
                        icon="gg:add"
                        class="rounded-xl dropdown-toggle"
                        @click="showEmployeeDropdown = !showEmployeeDropdown"
                    >
                      {{ t('misc.addEmployee') }}
                    </UButton>

                    <div
                        v-if="showEmployeeDropdown"
                        class="absolute sub-item-color border shadow-md rounded-lg p-2 z-100 dropdown-panel mt-2"
                        style="top: 100%; right: 0; min-width: 250px;"
                        @click.stop
                    >
                      <!-- Native select -->
                      <select
                          v-model="selectedEmployeeId"
                          class="w-full p-2 rounded-lg border secondary-object-item"
                      >
                        <option disabled value="">{{ t('misc.selectEmployee') }}</option>
                        <option
                            v-for="e in availableEmployees"
                            :key="e.id"
                            :value="String(e.id)"
                        >
                          {{ e.firstName }} {{ e.lastName }}
                        </option>
                      </select>

                      <UButton class="mt-2 w-full justify-center" @click="addEmployee">{{ t('misc.addEmployee') }}</UButton>
                    </div>
                  </div>
                </div>

                <div class="w-full rounded-md mt-3 object-color h-[565px] p-3 overflow-auto">
                  <div v-if="isLoading" class="grid gap-3">
                    <p>Loading...</p>
                  </div>
                  <div v-else class="grid gap-3 w-full">
                    <UButton
                        v-for="staff in staffList"
                        :key="staff.id"
                        variant="none"
                        class="flex w-full items-center gap-3 border rounded p-3 font-bold"
                    >
                      <UAvatar size="md" :alt="staff.firstName + ' ' + staff.lastName" />
                      <span class="flex-1 text-left">{{ staff.firstName }} {{ staff.lastName }}</span>
                      <UButton
                          class="shrink-0"
                          icon="mynaui:trash"
                          @click.stop.prevent="removeEmployee(staff.id)"
                      />
                    </UButton>
                  </div>
                </div>
              </div>
            </template>

            <!-- ITEMS -->
            <template v-if="activeTab === 1">
              <div class="flex-col secondary-object-item rounded p-2">
                <div class="flex justify-between items-center">
                  <label class="font-bold text-left">{{ t('events.items') }}</label>
                  <div class="relative inline-block">
                    <UButton
                        icon="gg:add"
                        class="rounded-xl dropdown-toggle"
                        @click="showItemDropdown = !showItemDropdown"
                    >
                      {{ t('misc.addItem') }}
                    </UButton>

                    <div
                        v-if="showItemDropdown"
                        class="absolute sub-item-color border shadow-md rounded-lg p-4 z-100 dropdown-panel"
                        style="top: 100%; right: 0; min-width: 250px;"
                        @click.stop
                    >
                      <!-- Native select -->
                      <select
                          v-model="selectedItemId"
                          class="w-full p-2 rounded-lg border secondary-object-item"
                      >
                        <option disabled value="">{{ t('misc.selectItem') }}</option>
                        <option
                            v-for="i in availableItems"
                            :key="i.id"
                            :value="String(i.id)"
                        >
                          {{ i.name }}
                        </option>
                      </select>

                      <UInputNumber
                          :min="1"
                          v-model.number="selectedItemQuantity"
                          orientation="horizontal"
                          class="w-full rounded mt-2"
                          :placeholder="t('misc.addAmount')"
                      />
                      <UButton class="mt-2 w-full justify-center" @click="addItem">{{ t('misc.addItem') }}</UButton>
                    </div>
                  </div>
                </div>

                <div class="w-full rounded-md mt-3 object-color h-[565px] p-3 overflow-auto">
                  <div v-if="isLoading" class="grid gap-3">
                    <p>Loading...</p>
                  </div>
                  <div v-else class="grid gap-3 w-full">
                    <div
                        v-for="group in groupedItems"
                        :key="group.key"
                        class="flex justify-between items-center p-2 border rounded relative"
                        :class="{ 'bg-yellow-400 text-black': group.external }"
                    >
                      <div>
                        {{ group.name }} x {{ group.count }}
                        <span v-if="group.external" class="text-sm text-red-500 font-semibold ml-2">({{ t('events.rented') }})</span>
                      </div>
                      <div class="flex gap-2 items-center">
                        <UButton
                            icon="mynaui:trash"
                            class="hover:scale-[110%] dropdown-toggle"
                            @click.prevent="toggleItemDropdown(group.key, $event)"
                        />
                      </div>
                      <div
                          v-if="activeItemDropdowns[group.key]"
                          class="absolute z-500 sub-item-color border shadow-md rounded-lg p-3 w-[250px] right-[50px] top-2 dropdown-panel"
                      >
                        <UInputNumber
                            :min="1"
                            :max="group.count"
                            v-model.number="removeQuantities[group.key]"
                            orientation="horizontal"
                            class="w-full rounded mb-2 object-color"
                            :placeholder="t('misc.removeAmount')"
                        />

                        <div class="flex gap-2 justify-between">
                          <UButton class="flex" @click.prevent="removeSpecificItems(group.storedItemIds.slice(0, removeQuantities[group.key] || 1))">{{ t('misc.remove') }}</UButton>
                          <UButton class="flex" @click.prevent="removeSpecificItems(group.storedItemIds.slice(0, group.count))">{{ t('misc.removeAll') }}</UButton>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>

        <div class="flex justify-between mt-10">
          <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/events/${route.params.id}`">{{ t('misc.back') }}</UButton>
          <div class="flex gap-2">
            <UButton class="px-4 py-2 rounded-lg" :to="{ path: '/invoices/new', query: { callback: route.fullPath, autoGenerate: 'true', event: route.params.id } }">{{ t('misc.generateInvoice') }}</UButton>
            <UButton class="px-4 py-2 rounded-lg" @click="submitInfo">{{ t('misc.submit') }}</UButton>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth-admin' })

import { ref, onMounted, computed, onBeforeUnmount, watch } from 'vue'
import { useAuth, useRoute } from '#imports'
import type { TabsItem } from '@nuxt/ui'
import { vMaska } from 'maska/vue'

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const toast = useToast()
const router = useRouter()
const route = useRoute()

const { id } = route.params
const returnTo = computed(() => route.query.callback as string | undefined)

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
const errorMessages = ref<string[]>([])
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
  { label: t('events.items'), icon: 'i-lucide-box',  value: 1 }
]))
const activeTab = ref(0)

const availableEmployees = ref<any[]>([])
const selectedEmployeeId = ref('')
const showEmployeeDropdown = ref(false)
const staffList = ref<any[]>([])

const availableItems = ref<any[]>([])
const selectedItemId = ref('')
const selectedItemQuantity = ref(1)
const showItemDropdown = ref(false)

const itemList = ref<any[]>([])

const activeItemDropdowns = ref<Record<string, boolean>>({})
const removeQuantities = ref<Record<string, number>>({})

const toggleItemDropdown = (key: string) => {
  activeItemDropdowns.value[key] = !activeItemDropdowns.value[key]
}

const closeAllDropdowns = () => {
  for (const key in activeItemDropdowns.value) activeItemDropdowns.value[key] = false
  showEmployeeDropdown.value = false
  showItemDropdown.value = false
}

const handleClickOutside = (ev: MouseEvent) => {
  const dropdowns = document.querySelectorAll('.dropdown-toggle, .dropdown-panel')
  const clickedInside = Array.from(dropdowns).some(el => el.contains(ev.target as Node))
  if (!clickedInside) closeAllDropdowns()
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))

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
      map.set(key, { key, itemId, name: storedItem.item.name, count: 0, external: isExternal, storedItemIds: [] })
    }
    const group = map.get(key)!
    group.count++
    group.storedItemIds.push(storedItem.id)
  }
  return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name))
})

const submitInfo = async () => {
  errorMessages.value = []

  const requestBody = {
    address: event.value.address,
    date: event.value.date,
    arrivalTime: event.value.arrivalTime,
    meetupTime: event.value.meetupTime,
    guestTime: event.value.guestTime,
    endTime: event.value.endTime,
    readyTime: event.value.readyTime,
    soundCheckTime: event.value.soundCheckTime,
    startTime: event.value.startTime,
    comments: event.value.comments,
    title: event.value.title
  }

  try {
    const res = await fetch(`${apiBase}/admin/events/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${userToken}` },
      body: JSON.stringify(requestBody)
    })

    const text = await res.text()
    if (!res.ok) {
      let msg = 'Failed to update event information!'
      try { msg = JSON.parse(text).message || msg } catch {}
      throw new Error(msg)
    }

    toast.add({ title: 'Success!', description: 'An event has successfully been updated!', icon: 'qlementine-icons:success-12' })
    router.push(`/events/${id}`)
  } catch (e: any) {
    console.error(e)
    errorMessages.value.push(e?.message || 'An error occurred while updating event information.')
  }
}

const findEvent = async () => {
  try {
    const res = await fetch(`${apiBase}/admin/events/${id}`, { headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) throw new Error('Failed to fetch event!')
    const eventData = await res.json()
    event.value = {
      ...eventData,
      staffList: eventData.staffList || [],
      id: eventData.id,
      title: eventData.title || '',
      address: eventData.address || '',
      date: eventData.date || '',
      meetupTime: eventData.meetupTime || '',
      arrivalTime: eventData.arrivalTime || '',
      readyTime: eventData.readyTime || '',
      soundCheckTime: eventData.soundCheckTime || '',
      guestTime: eventData.guestTime || '',
      startTime: eventData.startTime || '',
      endTime: eventData.endTime || '',
      comments: eventData.comments || ''
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchAvailableEmployees = async () => {
  try {
    const res = await fetch(`${apiBase}/admin/employees`, { headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) throw new Error('Failed to fetch available employees!')
    availableEmployees.value = await res.json()
  } catch (e) {
    console.error(e)
  }
}

const fetchAvailableItems = async () => {
  try {
    const res = await fetch(`${apiBase}/admin/items`, { headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) throw new Error('Failed to fetch available items!')
    availableItems.value = await res.json()
  } catch (e) {
    console.error(e)
  }
}

const findStaff = async () => {
  isLoading.value = true
  try {
    const res = await fetch(`${apiBase}/admin/events/${id}/employees`, { headers: { Authorization: `Bearer ${userToken}` } })
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
    const res = await fetch(`${apiBase}/admin/events/${id}/items`, { headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) throw new Error('Failed to fetch items!')
    itemList.value = await res.json()
  } catch (e) {
    console.error(e)
  } finally {
    isLoading.value = false
  }
}

const addEmployee = async () => {
  if (!selectedEmployeeId.value) return alert('Select an employee first!')
  try {
    const url = new URL(`${apiBase}/admin/events/${id}/employees`)
    url.searchParams.append('employeeId', selectedEmployeeId.value.toString())
    url.searchParams.append('removeEmployees', 'false')

    const res = await fetch(url.toString(), { method: 'PUT', headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) return alert('Failed to add employee!')

    await findStaff()
    showEmployeeDropdown.value = false
    selectedEmployeeId.value = ''
  } catch (e) {
    console.error(e)
  }
}

const removeEmployee = async (employeeId: number) => {
  if (!confirm('Remove this employee?')) return
  try {
    const url = new URL(`${apiBase}/admin/events/${id}/employees`)
    url.searchParams.append('employeeId', employeeId.toString())
    url.searchParams.append('removeEmployees', 'true')

    const res = await fetch(url.toString(), { method: 'PUT', headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) return alert('Failed to remove employee!')

    await findStaff()
  } catch (e) {
    console.error(e)
  }
}

const addItem = async () => {
  if (!selectedItemId.value || selectedItemQuantity.value < 1) {
    return alert('Select an item and enter a valid quantity!')
  }

  try {
    const url = new URL(`${apiBase}/admin/events/${id}/items`)
    url.searchParams.append('itemId', selectedItemId.value.toString())
    url.searchParams.append('amount', selectedItemQuantity.value.toString())
    url.searchParams.append('removeItems', 'false')

    const res = await fetch(url.toString(), { method: 'PUT', headers: { Authorization: `Bearer ${userToken}` } })
    if (!res.ok) return alert('Failed to add item!')

    await findItems()
    await applyRentalTags()

    showItemDropdown.value = false
    selectedItemId.value = ''
    selectedItemQuantity.value = 1
  } catch (e) {
    console.error(e)
  }
}

const removeSpecificItems = async (storedItemIds: number[]) => {
  if (!storedItemIds.length) return alert('No items selected.')
  if (!confirm(`Remove ${storedItemIds.length} of this item?`)) return

  try {
    const res = await fetch(`${apiBase}/admin/events/${id}/items/remove-specific`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${userToken}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({ storedItemIds })
    })
    if (!res.ok) return alert('Failed to remove items!')

    await findItems()
    await applyRentalTags()
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  await Promise.all([
    findEvent(),
    fetchAvailableEmployees(),
    fetchAvailableItems(),
    findStaff(),
    findItems()
  ])

  await fetchWarehouseStock()
  await applyRentalTags()

  isAdmin.value = data?.value?.user?.roleList?.some((r: any) => r.name === 'Admin')
})

watch(
    () => event.value.date,
    async (newDate, oldDate) => {
      if (!newDate || newDate === oldDate) return
      await applyRentalTags()
    }
)
</script>