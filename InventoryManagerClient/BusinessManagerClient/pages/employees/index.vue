<template>
  <div>
    <header>
      <div class="primary-color  rounded-xl shadow-lg p-2">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.employeeList') }}</h1>
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
        >
        </UButton>
      </div>

      <div class="secondary-color rounded-xl shadow-lg p-4 w-full">
        <div class="flex justify-end">
          <UButton
              variant="button"
              class="rounded-xl"
              :to="{ path: '/employees/new', query: { callback: route.fullPath } }"
              icon="gg:add"
          >
            {{ t('misc.addEmployee') }}
          </UButton>
        </div>

        <div v-if="isLoading" class="grid gap-4 mt-4">
          <p>Loading...</p>
        </div>

        <div v-else class="grid gap-2 mt-4">
          <div id="staffList" class="flex-col w-full">
            <UTable
                ref="table"
                v-model:pagination="pagination"
                :data="filteredStaffList"
                :columns="columns"
                :loading="isLoading"
                :pagination-options="{
                getPaginationRowModel: getPaginationRowModel()
                }"
                class="rounded-md"
            >
              <template #expanded="{ row }">
                <pre>
                  <span>{{ t('employees.mPhone') }}: {{ row.original.mobilePhone }}</span>
                  <span>{{ t('employees.address1') }}: {{ row.original.address1 }}</span>
                  <span>{{ t('employees.address2') }}: {{ row.original.address2 }}</span>
                </pre>
              </template>
            </UTable>

            <!-- Pagination footer -->
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
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import {getPaginationRowModel} from "@tanstack/vue-table";

definePageMeta({
  middleware: 'auth-admin',
});

import {ref, onMounted, computed, resolveComponent, h, watch} from 'vue';
import {useAuth, useTemplateRef} from '#imports';
import type {TableColumn} from "@nuxt/ui";

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig();
const route = useRoute()
const router = useRouter()

const isLoading = ref<boolean>(false);

const searchQueryPending = ref(route.query.q ? String(route.query.q) : '');
const searchQuery = ref<string>(route.query.q ? String(route.query.q) : '');

type Role = {
  id: number;
  name: string;
  description:string;
  color: string
}
type Staff = {
  id: number
  firstName: string
  lastName: string
  address1: string
  address2: string
  email: string
  mobilePhone: string
  roleList: Role[]
}
const UButton = resolveComponent('UButton')
const UBadge = resolveComponent('UBadge')
type StaffTableRow = Staff & { actions?: null }
const staffList = ref<StaffTableRow[]>([])
const table = useTemplateRef('table')
const pagination = ref({
  pageIndex: route.query.page ? Math.max(Number(route.query.page) - 1, 0) : 0,
  pageSize: 10
})

const columns: TableColumn<StaffTableRow>[] = [
  {
    id: 'expand',
    cell: ({ row }) =>
        h(UButton, {
          color: 'neutral',
          variant: 'ghost',
          icon: 'i-lucide-chevron-down',
          square: true,
          'aria-label': 'Expand',
          ui: {
            leadingIcon: [
              'transition-transform',
              row.getIsExpanded() ? 'duration-200 rotate-180' : ''
            ]
          },
          onClick: () => row.toggleExpanded()
        })
  },
  {
    accessorKey: 'id',
    header: () => "#",
    cell: ({row}) =>
        h('p', { class: 'text-wrap' }, row.original.id)
  },
  {
    accessorKey: 'firstName',
    header: () => t('employees.name'),
    cell: ({ row }) =>
        h('p', null, row.original.firstName)
  },
  {
    accessorKey: 'lastName',
    header: () => t('employees.surname'),
    cell: ({row}) =>
        h('p', null, row.original.lastName)
  },
  {
    accessorKey: 'email',
    header: () => t('employees.email'),
    cell: ({row}) =>
        h('p', null, row.original.email)
  },
  {
    accessorKey: 'roleList',
    header: () => t('employees.roles'),
    cell: ({ row }) =>
        h('div', { class: 'flex flex-wrap gap-2' },
            row.original.roleList.map((role: Role) =>
                h(UBadge as any, {
                  size: 'xs',
                  variant: 'solid',
                  class: 'flex items-center gap-1 p-2',
                  style: { backgroundColor: role.color }
                }, () => role.name)
            )
        )
  },
  {
    accessorKey: 'actions',
    header: '',
    cell: ({ row }) =>
        h('div', { class: 'flex justify-end gap-2' }, [
          h(UButton, {
            icon: 'i-heroicons-pencil-square',
            to: {
              path: `/employees/${row.original.id}`,
              query: {callback: route.fullPath}
            },
            class: 'hover:scale-[110%] border-1',
            onClick: (e: MouseEvent) => e.stopPropagation()
          }),
          h(UButton, {
            icon: 'mynaui:trash',
            class: 'hover:scale-[110%] border-1',
            onClick: () => deleteEmployee(row.original.id)
          })
        ])
  }
]

const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token;

async function findStaff() {
  isLoading.value = true;
  try {
    const response = await fetch(`${apiBase}/admin/employees`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userToken}`
      }
    });
    if (!response.ok) {
      throw new Error('Failed to fetch staff!');
    }
    const data = await response.json();

    staffList.value = data as StaffTableRow[]

  } catch (error) {
    console.error(error);
    alert(error.message);
  } finally {
    isLoading.value = false;
  }
}

async function deleteEmployee(employeeId: number) {
  const confirmDelete = confirm("Are you sure you want to delete this employee?");
  if (!confirmDelete) return;

  try {
    const rolesResponse = await fetch(`${apiBase}/admin/employees/${employeeId}/roles`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userToken}`
      }
    });
    if (!rolesResponse.ok) {
      throw new Error('Failed to fetch employee categories!');
    }

    const rolesData = await rolesResponse.json();
    const hasAdminRole = rolesData.some((role: any) => role.name === 'Admin');

    if (hasAdminRole) {
      alert("You cannot delete an employee with an Admin role.");
      return;
    }

    const deleteResponse = await fetch(`${apiBase}/admin/employees/${employeeId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${userToken}`
      }
    });

    if (!deleteResponse.ok) {
      throw new Error('Failed to delete employee!');
    }

    staffList.value = staffList.value.filter(staff => staff.id !== employeeId);
    alert('Employee deleted successfully!');
  } catch (error) {
    console.error('Error deleting employee:', error);
    alert('Error deleting employee: ' + error.message);
  }
}

const filteredStaffList = computed<StaffTableRow[]>(() => {
  if (!searchQuery.value) {
    return staffList.value
  }

  const lowerSearchQuery = searchQuery.value.toLowerCase()

  return staffList.value.filter((staff) =>
      (staff.firstName && staff.firstName.toLowerCase().startsWith(lowerSearchQuery)) ||
      (staff.lastName && staff.lastName.toLowerCase().startsWith(lowerSearchQuery))
  )
})

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
  findStaff();
});
</script>

