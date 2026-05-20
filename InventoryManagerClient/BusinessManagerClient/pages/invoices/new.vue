<template>
  <div>
    <div class="flex flex-col">
      <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
        <div class="container mx-auto">
          <h1 class="text-center" style="font-family: 'Arial'">
            {{ t('titles.newInvoice') }}
          </h1>
        </div>
      </header>

      <main class="flex-1 container mx-auto">
        <section class="secondary-color shadow-lg rounded-xl p-8">
          <UForm
              class="flex-col gap-4"
              :validate="validateInfo"
              :state="form"
              @error="onError"
              @submit="onSubmitInfo"
          >
            <div>
              <h2 class="text-xl font-semibold mb-4">
                {{ t('invoices.invoiceInformation') }}
              </h2>

              <div class="flex justify-center gap-6">
                <UFormField required class="flex flex-col items-start mb-8" :label="t('invoices.selectEvent')" name="eventId">
                  <USelectMenu
                      class="w-[350px]"
                      size="xl"
                      :items="availableEvents || []"
                      v-model="form.eventId"
                      option-attribute="label"
                      value-attribute="value"
                  />
                </UFormField>

                <UFormField class="flex flex-col items-start mb-8" :label="t('invoices.selectCompany')" name="selectCompany">
                  <USelectMenu
                      class="w-[350px]"
                      size="xl"
                      :items="availableContacts || []"
                      v-model="form.contactId"
                      option-attribute="label"
                      value-attribute="value"
                  />
                </UFormField>
              </div>

              <p class="text-center mb-5">{{ t('invoices.fieldInfo') }}</p>

              <div class="flex justify-center gap-6">
                <!-- Column 1.1 -->
                <div class="flex flex-col gap-4">
                  <UFormField class="flex flex-col items-start" :label="t('invoices.invoiceNumber')" name="invoiceNumber">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.invoiceNumber" disabled />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyName')" name="companyName">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyName" />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyNumber')" name="companyNumber">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyNumber" />
                  </UFormField>

                  <UFormField class="flex flex-col items-start" :label="t('invoices.companyVatNumber')" name="companyVatNumber">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyVatNumber" />
                  </UFormField>
                </div>

                <!-- Column 1.2 -->
                <div class="flex flex-col gap-4 self-end">
                  <!-- Invoice Date -->
                  <UFormField class="flex flex-col items-start" :label="t('invoices.invoiceDate')" name="invoiceDate" required>
                    <UInput type="date" class="w-[350px]" variant="input" size="xl" v-model="form.invoiceDate" />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyAddress')" name="companyAddress">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyAddress"/>
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyCity')" name="companyCity">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyCity" />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyCountry')" name="companyCountry">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyCountry" />
                  </UFormField>
                </div>
              </div>
            </div>

            <USeparator orientation="horizontal" size="xs" class="hidden md:block m-5" />

            <div>
              <h2 class="text-xl font-semibold mb-4">{{ t('invoices.costItems') }}</h2>

              <div class="flex flex-col gap-4 items-end w-full">
                <UButton class="mb-4" icon="gg:add" @click="openNewCostModal">{{ t('invoices.addCostItem') }}</UButton>

                <UModal
                    v-model:open="modalOpen"
                    :title="isEditingCost
                    ? (t('invoices.editCostItem'))
                    : (t('invoices.addCostItem'))"
                    close-icon="material-symbols:close-rounded"
                >
                  <template #body>
                    <div class="flex flex-col gap-4 items-center">
                      <UFormField :label="t('invoices.serviceDone')" name="service">
                        <UInput class="w-[300px]" variant="input" size="xl" v-model="invoiceCosts.service" />
                      </UFormField>

                      <UFormField :label="t('invoices.amount')" name="pieces">
                        <UInput class="w-[300px]" variant="input" size="xl" type="number" v-model.number="invoiceCosts.piece" />
                      </UFormField>

                      <UFormField :label="t('invoices.measure')" name="measure">
                        <UInput class="w-[300px]" variant="input" size="xl" v-model="invoiceCosts.measure" />
                      </UFormField>

                      <UFormField :label="t('invoices.cost')" name="cost">
                        <UInput class="w-[300px]" variant="input" size="xl" type="number" v-model.number="invoiceCosts.cost" />
                      </UFormField>

                      <p>{{ t('invoices.total') }}: {{(Number(invoiceCosts.piece) || 0) * (Number(invoiceCosts.cost) || 0) }}</p>

                      <UButton class="px-4 py-2 mt-4 button-item cursor-pointer self-center" type="button" @click="onAddOrUpdateCostItem">
                        {{isEditingCost ? (t('misc.save')) : (t('misc.submit')) }}
                      </UButton>
                    </div>
                  </template>
                </UModal>

                <!-- Items table -->
                <UTable
                    class="rounded-md w-full"
                    ref="table"
                    :data="filteredCostsList"
                    :columns="columns"
                    :loading="isLoading"
                />
              </div>
            </div>

            <!-- Submit / Back -->
            <div class="col-span-full flex justify-between mt-4">
              <UButton class="px-4 py-2 rounded-lg" :to="returnTo || '/invoices'">{{ t('misc.back') }}</UButton>
              <UButton class="px-4 py-2 button-item cursor-pointer" type="submit">{{ t('misc.submit') }}</UButton>
            </div>
          </UForm>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FormError, FormErrorEvent, FormSubmitEvent, TableColumn } from '@nuxt/ui'
import { ref, reactive, computed, watch, h, resolveComponent, onMounted } from 'vue'
import { useAuth, useI18n, useRuntimeConfig, useRoute, useRouter, useToast, useFetch } from '#imports'

definePageMeta({ middleware: 'auth-admin' })

const route = useRoute()
const router = useRouter()
const returnTo = computed(() => route.query.callback as string | undefined)

const modalOpen = ref(false)
const isEditingCost = ref(false)
const editingCostId = ref<number | null>(null)
const hasAutoGenerated = ref(false)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const toast = useToast()
const isLoading = ref<boolean>(false)

type EventOption = {
  label: string
  value: string
}

type ContactOption = {
  label: string
  value: string
  companyName: string
  companyAddress: string
  companyCity: string
  companyCountry: string
  companyNumber: string
  companyVatNumber: string
}

type StoredItemDto = {
  id: number
  rentPrice: number
  externalRental?: boolean
  item: { id: number; name: string }
}

type EmployeeDto = {
  id: number
  firstName: string
  lastName: string
}

const today = new Date().toISOString().split('T')[0]

const contactsBook = reactive({
  id: '',
  companyName: '',
  companyAddress: '',
  companyCity: '',
  companyCountry: '',
  companyNumber: '',
  companyVatNumber: ''
})

const form = reactive({
  eventId: null as EventOption | null,
  contactId: null as ContactOption | null,
  invoiceNumber: '',
  invoiceDate: today,
  contactsBook
})

const nextInvoiceNumber = ref<number | null>(null)

const { data: nextNumberResult } = await useFetch<number>(
    `${apiBase}/admin/invoices/next-number`,
    {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    }
)

nextInvoiceNumber.value = nextNumberResult.value ?? 1
form.invoiceNumber = String(nextInvoiceNumber.value)

const errorMessage = ref<string | null>(null)

const { data: availableEvents } = await useFetch<EventOption[]>(
    `${apiBase}/admin/events`,
    {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` },
      transform: (data: { id: number; title: string; date: string }[]) =>
          data?.map((event) => ({
            label: `${event.title} | ${event.date}`,
            value: String(event.id)
          })),
      lazy: true
    }
)

const { data: availableContacts } = await useFetch<ContactOption[]>(
    `${apiBase}/admin/contacts`,
    {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` },
      transform: (data: {
        id: number
        companyName: string
        companyNumber: string
        companyVatNumber: string
        companyAddress: string
        companyCity: string
        companyCountry: string
      }[]) =>
          data?.map((contact) => ({
            label: `${contact.companyName} | ${contact.companyNumber}`,
            value: String(contact.id),
            companyName: contact.companyName,
            companyAddress: contact.companyAddress,
            companyCity: contact.companyCity,
            companyCountry: contact.companyCountry,
            companyNumber: contact.companyNumber,
            companyVatNumber: contact.companyVatNumber
          })),
      lazy: true
    }
)

watch(
    () => form.contactId,
    (contact) => {
      if (!contact) return
      form.contactsBook.companyName = contact.companyName ?? ''
      form.contactsBook.companyNumber = contact.companyNumber ?? ''
      form.contactsBook.companyVatNumber = contact.companyVatNumber ?? ''
      form.contactsBook.companyAddress = contact.companyAddress ?? ''
      form.contactsBook.companyCity = contact.companyCity ?? ''
      form.contactsBook.companyCountry = contact.companyCountry ?? ''
    }
)

const validateInfo = (state: any): FormError[] => {
  const errors: FormError[] = []

  if (!state.eventId || !state.eventId.value) errors.push({ name: 'eventId', message: t('errors.required') })

  if (!state.contactsBook.companyName) errors.push({ name: 'companyName', message: t('errors.required') })
  if (!state.contactsBook.companyNumber) errors.push({ name: 'companyNumber', message: t('errors.required') })
  if (!state.contactsBook.companyAddress) errors.push({ name: 'companyAddress', message: t('errors.required') })
  if (!state.contactsBook.companyCity) errors.push({ name: 'companyCity', message: t('errors.required') })
  if (!state.contactsBook.companyCountry) errors.push({ name: 'companyCountry', message: t('errors.required') })

  return errors
}

async function onError(event: FormErrorEvent) {
  if (event?.errors?.[0]?.id) {
    const element = document.getElementById(event.errors[0].id)
    element?.focus()
    element?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

type Costs = {
  id: number
  service: string
  measure: string
  pieces: number
  cost: number
}

const costsList = ref<Costs[]>([])

const invoiceCosts = reactive({
  service: '',
  measure: '',
  piece: '' as number | '',
  cost: '' as number | ''
})

function resetInvoiceCosts() {
  invoiceCosts.service = ''
  invoiceCosts.measure = ''
  invoiceCosts.piece = '' as ''
  invoiceCosts.cost = '' as ''
  isEditingCost.value = false
  editingCostId.value = null
}

function openNewCostModal() {
  resetInvoiceCosts()
  isEditingCost.value = false
  modalOpen.value = true
}

function onEditCostItem(row: Costs) {
  isEditingCost.value = true
  editingCostId.value = row.id

  invoiceCosts.service = row.service
  invoiceCosts.measure = row.measure
  invoiceCosts.piece = row.pieces
  invoiceCosts.cost = row.cost

  modalOpen.value = true
}

function onAddOrUpdateCostItem() {
  const pieces = Number(invoiceCosts.piece)
  const cost = Number(invoiceCosts.cost)

  if (Number.isNaN(pieces) || Number.isNaN(cost)) {
    toast.add({
      title: t('errors.error'),
      description: t('invoices.invalidNumber') || 'Invalid number values'
    })
    return
  }

  if (isEditingCost.value && editingCostId.value !== null) {
    const idx = costsList.value.findIndex((c) => c.id === editingCostId.value)
    if (idx !== -1) {
      costsList.value[idx] = {
        ...costsList.value[idx],
        service: invoiceCosts.service,
        measure: invoiceCosts.measure,
        pieces,
        cost
      }
    }
  } else {
    const newId =
        costsList.value.length > 0 ? Math.max(...costsList.value.map((c) => c.id)) + 1 : 1

    costsList.value.push({
      id: newId,
      service: invoiceCosts.service,
      measure: invoiceCosts.measure,
      pieces,
      cost
    })
  }

  resetInvoiceCosts()
  modalOpen.value = false
}

function deleteCostItem(id: number) {
  costsList.value = costsList.value.filter((c) => c.id !== id)
}

type CostsRow = Costs & { actions?: null }
const UButton = resolveComponent('UButton')

const columns: TableColumn<CostsRow>[] = [
  { accessorKey: 'id', header: '#', cell: ({ row }) => h('span', null, row.original.id) },
  { accessorKey: 'serviceDone', header: () => t('invoices.serviceDone'), cell: ({ row }) => h('span', null, row.original.service) },
  { accessorKey: 'pieces', header: () => t('invoices.amount'), cell: ({ row }) => h('span', null, row.original.pieces) },
  { accessorKey: 'measure', header: () => t('invoices.measure'), cell: ({ row }) => h('span', null, row.original.measure) },
  { accessorKey: 'cost', header: () => t('invoices.cost'), cell: ({ row }) => h('span', null, row.original.cost) },
  {
    accessorKey: 'actions',
    header: '',
    cell: ({ row }) =>
        h('div', { class: 'flex justify-end gap-2' }, [
          h(UButton as any, {
            icon: 'i-heroicons-pencil-square',
            class: 'hover:scale-[110%] border-1',
            onClick: (e: MouseEvent) => {
              e.stopPropagation()
              onEditCostItem(row.original)
            }
          }),
          h(UButton as any, {
            icon: 'mynaui:trash',
            class: 'hover:scale-[110%] border-1',
            onClick: () => deleteCostItem(row.original.id)
          })
        ])
  }
]

const filteredCostsList = computed(() => costsList.value)

async function autoGeneration(eventId: string) {
  if (!eventId || hasAutoGenerated.value) return

  const match = availableEvents.value?.find(ev => ev.value === eventId) || null
  if (match) form.eventId = match

  try {
    const [itemsRes, employeesRes] = await Promise.all([
      fetch(`${apiBase}/admin/events/${encodeURIComponent(eventId)}/items`, {
        method: 'GET',
        headers: { Authorization: `Bearer ${userToken}` }
      }),
      fetch(`${apiBase}/admin/events/${encodeURIComponent(eventId)}/employees`, {
        method: 'GET',
        headers: { Authorization: `Bearer ${userToken}` }
      })
    ])

    if (!itemsRes.ok) throw new Error('Failed to fetch event items for auto-generation')
    if (!employeesRes.ok) throw new Error('Failed to fetch event employees for auto-generation')

    const storedItems = (await itemsRes.json()) as StoredItemDto[]
    const employees = (await employeesRes.json()) as EmployeeDto[]

    costsList.value = []
    let nextId = 1

    if (storedItems?.length) {
      const equipmentMap = new Map<string, { name: string; rentPrice: number; count: number; rented: boolean }>()

      for (const si of storedItems) {
        const itemId = si.item?.id ?? si.id
        const name = si.item?.name ?? `Item #${itemId}`
        const rentPrice = Number(si.rentPrice) || 0
        const rented = si.externalRental === true

        const key = `${itemId}-${rentPrice}-${rented ? 'rented' : 'owned'}`

        const existing = equipmentMap.get(key)
        if (existing) existing.count += 1
        else equipmentMap.set(key, { name, rentPrice, count: 1, rented })
      }

      for (const group of equipmentMap.values()) {
        costsList.value.push({
          id: nextId++,
          service: group.rented ? `${group.name} (${t('invoices.rented')})` : group.name,
          measure: 'pcs',
          pieces: group.count,
          cost: group.rentPrice
        })
      }
    }

    const employeeCount = employees?.length ?? 0
    if (employeeCount > 0) {
      costsList.value.push({
        id: nextId++,
        service: 'Employees',
        measure: 'pcs',
        pieces: employeeCount,
        cost: 80
      })
    }

    hasAutoGenerated.value = true
  } catch (e) {
    console.error('autoGeneration failed:', e)
  }
}

onMounted(() => {
  const auto = route.query.autoGenerate === 'true'
  const eventId = route.query.event as string | undefined
  if (!auto || !eventId) return

  if (availableEvents.value?.length) autoGeneration(eventId)

  watch(
      () => availableEvents.value,
      (events) => {
        if (!events?.length) return
        if (hasAutoGenerated.value) return
        autoGeneration(eventId)
      },
      { immediate: false }
  )
})

async function onSubmitInfo(event: FormSubmitEvent<typeof form>) {
  errorMessage.value = null

  try {
    const eventId = form.eventId?.value
    if (!eventId) throw new Error('Event is required')

    const createInvoiceResponse = await fetch(
        `${apiBase}/admin/invoices?eventId=${encodeURIComponent(eventId)}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${userToken}`
          },
          body: JSON.stringify({
            date: form.invoiceDate,
            contactsBook: form.contactId ? { id: Number(form.contactId.value) } : null
          })
        }
    )

    const invoiceJsonText = await createInvoiceResponse.text()

    if (!createInvoiceResponse.ok) {
      let msg
      try {
        const errJson = JSON.parse(invoiceJsonText)
        msg = errJson.message || t('errors.createInvoiceFailed') || 'Failed to create invoice!'
      } catch {
        msg = t('errors.createInvoiceFailed') || 'Failed to create invoice!'
      }
      throw new Error(msg)
    }

    const createdInvoice = JSON.parse(invoiceJsonText) as { id: number }

    if (costsList.value.length > 0) {
      await Promise.all(
          costsList.value.map((item) =>
              fetch(`${apiBase}/admin/invoices/${createdInvoice.id}/records`, {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                  Authorization: `Bearer ${userToken}`
                },
                body: JSON.stringify({
                  service: item.service,
                  measure: item.measure,
                  pieces: item.pieces,
                  cost: item.cost
                })
              })
          )
      )
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.invoiceAdded') || 'Invoice created successfully',
      icon: 'qlementine-icons:success-12'
    })

    router.push(returnTo.value || '/invoices')
  } catch (err: any) {
    console.error(err)
    errorMessage.value = err.message || 'An error occurred while creating invoice.'
  }
}
</script>