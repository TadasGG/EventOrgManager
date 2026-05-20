<template>
  <div>
    <header>
      <div class="primary-color rounded-xl shadow-lg p-2 mb-10">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.events') }}</h1>
      </div>
    </header>

    <main class="grid gap-6">
      <div class="flex secondary-color rounded-xl shadow-lg p-4 gap-3 w-full justify-between">
        <UInput
            icon="i-heroicons-magnifying-glass-20-solid"
            size="xl"
            v-model="searchQueryPending"
            type="text"
            :placeholder="t('misc.search') + '...'"
            class="w-full"
            @keyup.enter="runSearch"
        />

        <UButton
            icon="i-heroicons-magnifying-glass-circle"
            size="xl"
            @click="runSearch"
        >
        </UButton>
      </div>

      <div class="secondary-color rounded-xl shadow-lg p-4 w-full">
        <div class="flex justify-end" v-if="isAdmin">
          <UButton
              class="rounded-xl"
              :to="{ path: '/events/new', query: { callback: route.fullPath } }"
              icon="gg:add"
          >
            {{ t('misc.addEvent') }}
          </UButton>
        </div>

        <USeparator color="primarycolor" class="mt-4" />

        <div v-if="isLoading" class="grid gap-4 mt-4">
          <p>Loading...</p>
        </div>

        <div v-else class="grid gap-2 mt-4">
          <div id="eventList" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 w-full">
            <NuxtLink
                v-for="event in filteredEventsList"
                :key="event.id"
                :to="{ path: `/events/${event.id}`, query: { callback: route.fullPath } }"
                class="block hover:scale-[102%] transition-transform"
            >
              <UCard class="rounded-xl shadow-lg h-full object-item border">
                <template #header>
                  <h1 class="text-center">{{ event.title }}</h1>
                  <h3 class="text-center">{{ event.date }}</h3>
                </template>

                <div class="text-center h-[280px]">
                  <h2>{{ t('events.address') }}:</h2>
                  <p>{{ event.address }}</p>

                  <br />

                  <h2>{{ t('events.times') }}:</h2>
                  <p>{{ t('events.atWarehouse') }}: {{ event.meetupTime }}</p>
                  <p>{{ t('events.atLocation') }}: {{ event.arrivalTime }}</p>
                  <p>{{ t('events.beReadyBy') }}: {{ event.readyTime }}</p>
                  <p>{{ t('events.soundCheck') }}: {{ event.soundCheckTime }}</p>
                  <p>{{ t('events.guestsArrive') }}: {{ event.guestTime }}</p>
                  <p>{{ t('events.eventStarts') }}: {{ event.startTime }}</p>
                  <p>{{ t('events.eventEnds') }}: {{ event.endTime }}</p>
                </div>

                <template #footer>
                  <div class="flex justify-between" v-if="isAdmin">
                    <UButton
                        :label="t('misc.delete')"
                        class="button-item"
                        icon="mynaui:trash"
                        @click.prevent="deleteEvent(event.id)"
                    />
                    <UButton
                        :label="t('misc.edit')"
                        class="button-item"
                        icon="i-heroicons-pencil-square"
                        :to="{ path: `/events/${event.id}/edit`, query: { callback: route.fullPath } }"
                        @click.stop
                    />
                  </div>
                </template>
              </UCard>
            </NuxtLink>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuth } from '#imports'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const { public: { apiBase } } = useRuntimeConfig()

const eventList = ref<any[]>([])
const isLoading = ref<boolean>(false)

const searchQueryPending = ref(route.query.q ? String(route.query.q) : '');
const searchQuery = ref<string>(route.query.q ? String(route.query.q) : '');

function runSearch() {
  const q = searchQueryPending.value.trim()
  searchQuery.value = q
}

watch(searchQuery, (val) => {
  const q = val?.trim() || undefined
  router.replace({ query: { ...route.query, q } })
})

watch(() => route.query.q, (q) => {
  const v = q ? String(q) : ''
  if (v !== searchQuery.value) searchQuery.value = v
})

const { data } = useAuth()
const user = data.value?.user as any || {}
const userToken = user?.token
const currentUserEmail = user?.email || ''
const currentUserId = user?.id ?? user?.employeeId ?? null

const isAdmin = ref(false)

async function findEvents () {
  isLoading.value = true
  try {
    const response = await fetch(`${apiBase}/admin/events`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error('Failed to fetch event!')
    const data = await response.json()
    eventList.value = data.sort((a: any, b: any) => {
      if (!a.date) return 1
      if (!b.date) return -1
      return new Date(b.date).getTime() - new Date(a.date).getTime()
    })
  } catch (error: any) {
    console.error(error)
    alert(error.message)
  } finally {
    isLoading.value = false
  }
}

async function deleteEvent (eventId: number) {
  const confirmDelete = confirm('Are you sure you want to delete this event?')
  if (!confirmDelete) return
  try {
    const res = await fetch(`${apiBase}/admin/events/${eventId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to delete event!')
    eventList.value = eventList.value.filter(e => e.id !== eventId)
    alert('Event deleted successfully!')
  } catch (error: any) {
    console.error('Error deleting event:', error)
    alert('Error deleting event: ' + error.message)
  }
}

onMounted(async () => {
  try {
    if (currentUserEmail && userToken) {
      const response = await fetch(`${apiBase}/admin/employees/me/roles`, {
        method: 'GET',
        headers: { Authorization: `Bearer ${userToken}` }
      })
      if (!response.ok) throw new Error('Failed to fetch roles')
      const roles = await response.json()
      isAdmin.value = roles.some((role: any) => role.name === 'Admin')
    }
  } catch (error) {
    console.error('Error fetching roles:', error)
  }

  await findEvents()
})

const filteredEventsList = computed(() => {
  let list = eventList.value

  if (!isAdmin.value) {
    list = list.filter((ev: any) => {
      const employees = Array.isArray(ev.employeeList) ? ev.employeeList : []
      return employees.some((emp: any) => {
        const idMatch = currentUserId != null && (emp?.id === currentUserId || emp?.employeeId === currentUserId)
        const emailMatch = !!currentUserEmail && emp?.email === currentUserEmail
        return idMatch || emailMatch
      })
    })
  }

  if (!searchQuery.value) return list
  const q = searchQuery.value.toLowerCase()
  return list.filter((ev: any) => (ev.title || '').toLowerCase().startsWith(q))
})
</script>