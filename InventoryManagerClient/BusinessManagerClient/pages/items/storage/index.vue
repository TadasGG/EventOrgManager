<template>
  <div>
    <header>
      <div class="primary-color rounded-xl shadow-lg p-2">
        <h1 class="text-center" style="font-family: 'Arial'">
          {{ t('titles.storedItems') }}
        </h1>
      </div>
    </header>

    <main class="grid gap-6 my-10">
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
              :to="{ path: '/items/storage/new', query: { callback: route.fullPath } }"
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

import { ref, onMounted, computed, onBeforeUnmount, watch, resolveComponent, h } from 'vue'
import type { TableColumn } from '@nuxt/ui'
import { useAuth, useI18n, useRuntimeConfig, useRoute } from '#imports'

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()

const route = useRoute()
const router = useRouter()

const itemsList = ref<any[]>([])
const isLoading = ref(false)

const searchQueryPending = ref(route.query.q ? String(route.query.q) : '')
const searchQuery = ref(route.query.q ? String(route.query.q) : '')

const pagination = ref({
  pageIndex: route.query.page ? Math.max(Number(route.query.page) - 1, 0) : 0,
  pageSize: 3,
})

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

type StoredItem = {
  id: number
  item?: Item
  availability: boolean
  rentPrice: number
  externalRental: boolean
}

type ItemRow = {
  id: string | number
  itemId?: number
  name: string
  rentPrice?: number
  totalCount?: number
  children?: ItemRow[]
}

const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token

const expanded = ref(true)
const UButton = resolveComponent('UButton')
const UInputNumber = resolveComponent('UInputNumber')

const groupedItemsByCategory = computed(() => {
  const filteredItems = itemsList.value.filter((itemData: StoredItem) => {
    const itemName = itemData.item?.name?.toLowerCase() || ''
    const query = searchQuery.value.toLowerCase()
    const isExternal = itemData.externalRental === true

    return itemName.includes(query) && !isExternal
  })

  const groupedByCategory = filteredItems.reduce((acc: any, itemData: any) => {
    const categoryName = itemData.item?.itemCategory?.name || 'No Category'
    const itemId = itemData.item?.id
    const itemName = itemData.item?.name || ''

    if (!categoryName || !itemId) return acc

    if (!acc[categoryName]) acc[categoryName] = {}
    if (!acc[categoryName][itemId]) {
      acc[categoryName][itemId] = {
        categoryName,
        itemId,
        itemName,
        rentPrice: itemData.rentPrice,
        items: [] as StoredItem[],
        availableCount: 0,
        totalCount: 0,
      }
    }

    acc[categoryName][itemId].items.push(itemData)
    acc[categoryName][itemId].totalCount += 1
    if (itemData.availability) acc[categoryName][itemId].availableCount += 1

    return acc
  }, {})

  const result: Record<string, any[]> = {}
  for (const category in groupedByCategory) {
    result[category] = Object.values(groupedByCategory[category])
  }
  return result
})

const groupedItemsList = computed<ItemRow[]>(() => {
  const out: ItemRow[] = []

  for (const [categoryName, groups] of Object.entries(groupedItemsByCategory.value)) {
    const children: ItemRow[] = (groups as any[]).map((g) => ({
      id: g.itemId,
      itemId: g.itemId,
      name: g.itemName,
      rentPrice: g.rentPrice,
      availableCount: g.availableCount,
      totalCount: g.totalCount,
    }))

    out.push({
      id: categoryName,
      name: '',
      children,
    })
  }

  return out
})

const
    paginatedData = computed<ItemRow[]>(() => {
  const start = pagination.value.pageIndex * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return groupedItemsList.value.slice(start, end)
})

const activeItemDropdowns = ref<Record<string, boolean>>({})
const removeQuantities = ref<Record<string, number>>({})

const toggleItemDropdown = (key: string) => {
  activeItemDropdowns.value[key] = !activeItemDropdowns.value[key]
}

const closeAllDropdowns = () => {
  for (const key in activeItemDropdowns.value) {
    activeItemDropdowns.value[key] = false
  }
}

const handleClickOutside = (event: MouseEvent) => {
  const dropdowns = document.querySelectorAll('.dropdown-toggle, .dropdown-panel')
  const clickedInside = Array.from(dropdowns).some(el => el.contains(event.target as Node))
  if (!clickedInside) closeAllDropdowns()
}

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
    cell: ({ row }) => {
      const isCategory = !!row.original.children
      if (isCategory) return h('p', null, '')
      return h('p', {class: 'text-start'}, row.original.name)
    },
  },
  {
    accessorKey: 'rentPrice',
    header: () => t('storage.itemPrice') || 'Price',
    cell: ({ row }) => {
      const isCategory = !!row.original.children
      if (isCategory) return h('p', null, '')
      const price = row.original.rentPrice
      return h('p', null, price != null ? `${price} € / vnt.` : 'N/A')
    },
  },
  {
    accessorKey: 'availability',
    header: () => t('storage.totalInStorage') || 'Total in storage.',
    cell: ({ row }) => {
      const isCategory = !!row.original.children
      if (isCategory) return h('p', null, '')
      const totalStored = row.original.totalCount ?? 0
      return h('p', null, `${totalStored} ${t('storage.pcs')}.`)
    },
  },
  {
    accessorKey: 'actions',
    header: '',
    cell: ({ row }) => {
      const isItem = !row.original.children && row.original.itemId
      if (!isItem) return null

      const key = String(row.original.itemId)

      return h('div', { class: 'relative flex justify-end gap-2' }, [
        h(UButton, {
          icon: 'i-heroicons-pencil-square',
          to: {
            path: `/items/storage/${row.original.itemId}`,
            query: { callback: route.fullPath },
          },
          class: 'hover:scale-[110%] border-1',
          onClick: (e: MouseEvent) => e.stopPropagation(),
        }),
        h(UButton, {
          icon: 'mynaui:trash',
          class: 'hover:scale-[110%] border-1 dropdown-toggle',
          onClick: (e: MouseEvent) => {
            e.stopPropagation()
            toggleItemDropdown(key)
          },
        }),
        activeItemDropdowns.value[key]
            ? h(
                'div',
                {
                  class:
                      'absolute z-50 sub-item-color border shadow-md rounded-lg p-3 w-[250px] mt-2 bottom-0 dropdown-panel',
                },
                [
                  h(UInputNumber as any, {
                    min: 1,
                    max: row.original.totalCount ?? 1,
                    modelValue: removeQuantities.value[key],
                    'onUpdate:modelValue': (v: number) => {
                      removeQuantities.value[key] = v
                    },
                    orientation: 'horizontal',
                    class: 'w-full rounded mb-2 object-color text-black',
                    placeholder: t('misc.removeAmount'),
                  }),
                  h('div', { class: 'flex gap-2 justify-between' }, [
                    h(
                        UButton,
                        {
                          class: 'flex bg-red-400',
                          onClick: (e: MouseEvent) => {
                            e.preventDefault()
                            removeSpecificItems(
                                row.original.itemId!,
                                removeQuantities.value[key] || 1,
                            )
                          },
                        },
                        () => t('misc.remove'),
                    ),
                    h(
                        UButton,
                        {
                          class: 'flex',
                          onClick: (e: MouseEvent) => {
                            e.preventDefault()
                            removeSpecificItems(row.original.itemId!, 'all')
                          },
                        },
                        () => t('misc.removeAll'),
                    ),
                  ]),
                ],
            )
            : null,
      ])
    },
  },
]

const removeSpecificItems = async (itemId: number, count: number | 'all') => {
  if (!itemId) return
  if (count !== 'all' && count <= 0) return

  const confirmMessage =
      count === 'all'
          ? `Remove ALL available items?`
          : `Remove ${count} item(s)?`

  if (!confirm(confirmMessage)) return

  try {
    const url =
        count === 'all'
            ? `${apiBase}/admin/stored-items/remove-all`
            : `${apiBase}/admin/stored-items/remove-specific`

    const body =
        count === 'all'
            ? { itemId }
            : { itemId, count }

    const res = await fetch(url, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    })

    if (!res.ok) {
      alert('Failed to remove items!')
      return
    }

    await findItems()
  } catch (error) {
    console.error(error)
    alert('An error occurred while removing items.')
  }
}

async function findItems() {
  isLoading.value = true
  try {
    const response = await fetch(`${apiBase}/admin/stored-items`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${userToken}`,
      },
    })

    if (!response.ok) {
      throw new Error('Failed to fetch items!')
    }

    const data = await response.json()

    itemsList.value = (data as StoredItem[]).sort((a, b) => {
      const categoryA = a.item?.itemCategory?.name?.toLowerCase() || 'zzzz'
      const categoryB = b.item?.itemCategory?.name?.toLowerCase() || 'zzzz'

      if (categoryA !== categoryB) {
        return categoryA.localeCompare(categoryB)
      }

      const nameA = a.item?.name?.toLowerCase() || ''
      const nameB = b.item?.name?.toLowerCase() || ''
      return nameA.localeCompare(nameB)
    })
  } catch (error: any) {
    console.error(error)
    alert(error.message)
  } finally {
    isLoading.value = false
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
  document.addEventListener('click', handleClickOutside)
  findItems()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
