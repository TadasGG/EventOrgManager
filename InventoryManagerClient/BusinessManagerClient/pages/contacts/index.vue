<template>
  <div>
    <header>
      <div class="primary-color rounded-xl shadow-lg p-2">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.contacts') }}</h1>
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

      <!-- Table -->
      <div class="secondary-color rounded-xl shadow-lg p-4 w-full">
        <div class="flex justify-end">
          <UButton
              class="rounded-xl"
              :to="{ path: '/contacts/new', query: { callback: route.fullPath } }"

              icon="gg:add"
          >
            {{ t('misc.newContact') }}
          </UButton>
        </div>

        <div v-if="isLoading" class="grid gap-4 mt-4">
          <p>Loading...</p>
        </div>

        <div v-else class="mt-4">
          <UTable
              ref="table"
              v-model:pagination="pagination"
              :data="filteredContactsList"
              :columns="columns"
              :loading="isLoading"
              :pagination-options="{ getPaginationRowModel: getPaginationRowModel() }"
              class="rounded-md"
          />

          <div class="flex justify-center pt-4">
            <UPagination
                :page="(table?.tableApi?.getState().pagination.pageIndex || 0) + 1"
                :items-per-page="table?.tableApi?.getState().pagination.pageSize || pagination.pageSize"
                :total="table?.tableApi?.getFilteredRowModel().rows.length || 0"
                @update:page="onPageChange"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import {getPaginationRowModel} from "@tanstack/vue-table";

definePageMeta({
  middleware: 'auth-admin',
});

import {ref, onMounted, computed, h, resolveComponent, watch} from 'vue';
import {useAuth, useTemplateRef} from '#imports';
import type {TableColumn} from "@nuxt/ui";

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig();
const route = useRoute()
const router = useRouter()
const toast = useToast();

type Contacts = {
  id: number
  companyName: string
  companyAddress: string
  companyCity: string
  companyCountry: string
  companyNumber: string
  companyVatNumber: string
}

type ContactsRow = Contacts & { actions?: null }

const contactList = ref<any[]>([]);
const isLoading = ref<boolean>(false);

const searchQueryPending = ref(route.query.q ? String(route.query.q) : '')
const searchQuery = ref(route.query.q ? String(route.query.q) : '')

const table = useTemplateRef('table')

const pagination = ref({
  pageIndex: route.query.page ? Math.max(Number(route.query.page) - 1, 0) : 0,
  pageSize: 10
})

const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token;

async function findContacts() {
  isLoading.value = true;
  try {
    const response = await fetch(`${apiBase}/admin/contacts`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${userToken}`
      }
    });
    if (!response.ok) {
      throw new Error('Failed to fetch contacts!');
    }
    contactList.value  = await response.json();

  } catch (error) {
    console.error(error);
    alert(error.message);
  } finally {
    isLoading.value = false;
  }
}

async function deleteContact(contactId: number) {
  const confirmDelete = confirm("Are you sure you want to delete this contact?");
  if (!confirmDelete) return;

  try {
    const deleteResponse = await fetch(`${apiBase}/admin/contacts/${contactId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${userToken}`
      }
    });

    if (!deleteResponse.ok) {
      throw new Error('Failed to delete contact!');
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.contactDeleted'),
      icon: 'qlementine-icons:success-12'
    })

    findContacts()
  } catch (error) {
    console.error('Error deleting contact:', error);
    alert('Error deleting contact: ' + error.message);
  }
}

const UButton = resolveComponent('UButton')

const columns: TableColumn<ContactsRow>[] = [
  {
    accessorKey: 'id',
    header: '#',
    cell: ({ row }) => h('span', null, row.original.id)
  },
  {
    accessorKey: 'companyName',
    header: () => t('contacts.companyName'),
    cell: ({ row }) => h('span', null, row.original.companyName)
  },
  {
    accessorKey: 'companyAddress',
    header: () => t('contacts.companyAddress'),
    cell: ({ row }) => h('span', null,
        row.original.companyAddress + ', ' +
                row.original.companyCity + ', ' +
                row.original.companyCountry)
  },
  {
    accessorKey: 'companyNumber',
    header: () => t('contacts.companyNumber'),
    cell: ({ row }) => h('span', null, row.original.companyNumber)
  },
  {
    accessorKey: 'companyVatNumber',
    header: () => t('contacts.companyVatNumber'),
    cell: ({ row }) => h('span', null, row.original.companyVatNumber || "—")
  },
  {
    accessorKey: 'actions',
    header: '',
    cell: ({ row }) =>
        h('div', { class: 'flex justify-end gap-2' }, [
          h(UButton, {
            icon: 'i-heroicons-pencil-square',
            to: {
              path: `/contacts/${row.original.id}`,
              query: {callback: route.fullPath}
            },
            class: 'hover:scale-[110%] border-1',
            onClick: (e: MouseEvent) => e.stopPropagation()
          }),
          h(UButton, {
            icon: 'mynaui:trash',
            class: 'hover:scale-[110%] border-1',
            onClick: () => deleteContact(row.original.id)
          })
        ])
  }
]

function runSearch() {
  const q = searchQueryPending.value.trim()
  searchQuery.value = q
  pagination.value.pageIndex = 0
}

function onPageChange(p: number) {
  table.value?.tableApi?.setPageIndex(p - 1)
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
      if (table.value?.tableApi && page !== table.value.tableApi.getState().pagination.pageIndex) {
        table.value.tableApi.setPageIndex(page)
      }
    },
    { immediate: true }
)

watch(
    [searchQuery, () => table.value?.tableApi?.getState().pagination.pageIndex],
    ([q, page]) => {
      const pageIndex = typeof page === 'number' ? page : 0
      router.replace({
        query: {
          q: q || undefined,
          page: pageIndex > 0 ? String(pageIndex + 1) : undefined
        }
      })
    }
)

onMounted(() => {
  findContacts();
});

const filteredContactsList = computed(() => {
  if (!searchQuery.value) {
    return contactList.value;
  }
  const lowerSearchQuery = searchQuery.value.toLowerCase();

  return contactList.value.filter(contacts =>
      (contacts.companyName.toLowerCase().startsWith(lowerSearchQuery) ||
      contacts.companyNumber.toLowerCase().startsWith(lowerSearchQuery))
  );
});
</script>

