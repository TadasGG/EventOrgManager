<template>
  <div>
    <header>
      <div class="primary-color rounded-xl shadow-lg p-2">
        <h1 class="text-center" style="font-family: 'Arial'">
          {{ t('titles.itemsList') }}
        </h1>
      </div>
    </header>

    <main class="grid gap-6 my-10">
      <!-- Search -->
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
        />
      </div>

      <div class="secondary-color rounded-xl shadow-lg p-4 w-full">
        <div class="flex justify-end">
          <UButton
              class="rounded-xl"
              :to="{ path: '/items/new', query: { callback: route.fullPath } }"
              icon="gg:add"
          >
            {{ t('misc.storeNewItem') }}
          </UButton>
        </div>

        <div v-if="isLoading" class="grid gap-4 mt-4">
          <p>Loading...</p>
        </div>

        <div v-else class="grid gap-2 mt-4">
          <div id="itemsList" class="flex-col w-full">
            <UTable
                v-model:expanded="expanded"
                :data="paginatedData"
                :columns="columns"
                :loading="isLoading"
                :get-sub-rows="row => row.children"
                class="rounded"
                :ui="{ td: 'empty:p-0 p-1' }"
            />

            <!-- Pagination footer -->
            <div class="flex justify-center pt-4">
              <UPagination
                  :page="pagination.pageIndex + 1"
                  :items-per-page="pagination.pageSize"
                  :total="groupedItemsList.length"
                  @update:page="onPageChange"
              />
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: 'auth-admin',
})

import { ref, onMounted, computed, h, resolveComponent, watch } from 'vue'
import type { TableColumn } from '@nuxt/ui'
import { useAuth, useI18n, useRuntimeConfig, useRoute } from '#imports'

const route = useRoute()
const router = useRouter()
const toast = useToast();

const { t } = useI18n()
const {
  public: { apiBase },
} = useRuntimeConfig()

type ItemCategory = {
  id: number
  name: string
  description?: string
}

type Item = {
  id: number
  name: string
  description: string
  itemCategory?: ItemCategory | null
}

type ItemRow = {
  id: string | number
  name: string
  description?: string
  children?: ItemRow[]
}

const itemsList = ref<Item[]>([])
const isLoading = ref(false)

const searchQueryPending = ref(route.query.q ? String(route.query.q) : '')
const searchQuery = ref(route.query.q ? String(route.query.q) : '')

const pagination = ref({
  pageIndex: route.query.page ? Math.max(Number(route.query.page) - 1, 0) : 0,
  pageSize: 3,
})

const groupedItemsList = computed<ItemRow[]>(() => {
  const q = searchQuery.value.trim().toLowerCase()

  const filtered = q
      ? itemsList.value.filter(item =>
          item.name.toLowerCase().includes(q),
      )
      : itemsList.value

  const map = new Map<string, ItemRow[]>()

  for (const item of filtered) {
    const categoryName = item.itemCategory?.name || 'No Category'
    if (!map.has(categoryName)) {
      map.set(categoryName, [])
    }
    map.get(categoryName)!.push({
      id: item.id,
      name: item.name,
      description: item.description,
    })
  }

  const result: ItemRow[] = []
  for (const [categoryName, children] of map.entries()) {
    result.push({
      id: categoryName,
      name: '',
      description: '',
      children,
    })
  }

  return result
})

const paginatedData = computed(() => {
  const start = pagination.value.pageIndex * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return groupedItemsList.value.slice(start, end)
})

const expanded = ref(true)
const UButton = resolveComponent('UButton')

const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token

const columns: TableColumn<ItemRow>[] = [
  {
    accessorKey: 'id',
    header: '#',
    cell: ({ row }) => {
      const isCategory = !!row.original.children
      return h(
          'p',
          {
            class: isCategory ? 'font-bold text-lg text-start' : 'p-2',
            style: isCategory ? {} : { paddingLeft: '2rem' },
          },
          row.original.id,
      )
    },
  },
  {
    accessorKey: 'name',
    header: () => t('items.itemTitle' as any) || 'Name',
    cell: ({ row }) => h('p', null, row.original.name),
  },
  {
    accessorKey: 'description',
    header: () => t('items.itemDesc' as any) || 'Description',
    cell: ({ row }) => h('p', null, row.original.description ?? ''),
  },
  {
    accessorKey: 'actions',
    header: '',
    cell: ({ row }) => {
      const isItem = !row.original.children
      if (!isItem) return null

      return h('div', { class: 'flex justify-end gap-2' }, [
        h(UButton, {
          icon: 'i-heroicons-pencil-square',
          to: {
            path: `/items/${row.original.id}`,
            query: { callback: route.fullPath },
          },
          class: 'hover:scale-[110%] border-1',
          onClick: (e: MouseEvent) => e.stopPropagation(),
        }),
        h(UButton, {
          icon: 'mynaui:trash',
          class: 'hover:scale-[110%] border-1',
          onClick: () => deleteItem(Number(row.original.id)),
        }),
      ])
    },
  },
]

async function findItems() {
  isLoading.value = true
  try {
    const response = await fetch(`${apiBase}/admin/items`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${userToken}`,
      },
    })

    if (!response.ok) {
      throw new Error('Failed to fetch items!')
    }

    const data = await response.json()

    itemsList.value = (data as Item[]).sort((a, b) => {
      const categoryA = a.itemCategory?.name?.toLowerCase() || 'zzzz'
      const categoryB = b.itemCategory?.name?.toLowerCase() || 'zzzz'

      if (categoryA !== categoryB) {
        return categoryA.localeCompare(categoryB)
      }

      return a.name.toLowerCase().localeCompare(b.name.toLowerCase())
    })
  } catch (error: any) {
    console.error(error)
    alert(error.message || 'Failed to fetch items!')
  } finally {
    isLoading.value = false
  }
}

async function deleteItem(itemId: number) {
  const confirmDelete = confirm('Are you sure you want to delete this item?')
  if (!confirmDelete) return

  try {
    const deleteResponse = await fetch(`${apiBase}/admin/items/${itemId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${userToken}`,
      },
    })

    if (!deleteResponse.ok) {
      throw new Error('Failed to delete item!')
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.itemDeleted'),
      icon: 'qlementine-icons:success-12'
    })

    findItems()
  } catch (error: any) {
    console.error('Error deleting item:', error)
    alert('Error deleting item: ' + (error.message || 'Unknown error'))
  }
}

function runSearch() {
  const q = searchQueryPending.value.trim()
  searchQuery.value = q
  pagination.value.pageIndex = 0
}

function onPageChange(p: number) {
  pagination.value.pageIndex = p - 1
}

watch(
    () => route.query,
    (query) => {
      const q = query.q ? String(query.q) : ''
      if (q !== searchQuery.value) {
        searchQuery.value = q
        searchQueryPending.value = q
      }

      const page = query.page ? Math.max(Number(query.page) - 1, 0) : 0
      if (page !== pagination.value.pageIndex) {
        pagination.value.pageIndex = page
      }
    },
    { immediate: true },
)

watch(
    [searchQuery, () => pagination.value.pageIndex],
    ([q, pageIndex]) => {
      router.replace({
        query: {
          q: q || undefined,
          page: pageIndex > 0 ? String(pageIndex + 1) : undefined,
        },
      })
    },
)

onMounted(() => {
  findItems()
})
</script>
