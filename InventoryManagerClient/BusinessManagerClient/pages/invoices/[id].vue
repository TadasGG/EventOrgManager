<template>
  <div>
    <div class="flex flex-col">
      <!-- Header -->
      <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
        <div class="container mx-auto">
          <h1 class="text-center" style="font-family: 'Arial'">
            {{ t('titles.editInvoice') || t('titles.newInvoice') }}
          </h1>
        </div>
      </header>

      <!-- Main content -->
      <main class="flex-1 container mx-auto">
        <section class="secondary-color shadow-lg rounded-xl p-8">
          <UForm class="flex-col gap-4" :validate="validateInfo" :state="form" @error="onError" @submit="onSubmitInfo">
            <!-- Invoice / contact info -->
            <div>
              <h2 class="text-xl font-semibold mb-4">
                {{ t('invoices.invoiceInformation') }}
              </h2>

              <div class="flex justify-center gap-6">
                <!-- Column 1.1 -->
                <div class="flex flex-col gap-4">
                  <!-- Payment Status -->
                  <UFormField class="flex flex-col items-start mb-8" :label="t('invoices.paymentStatus')" name="status">
                    <USelect
                        class="w-[350px]"
                        size="xl"
                        :items="paymentStatus || []"
                        v-model="form.invoiceStatus"
                        option-attribute="label"
                        value-attribute="value"
                    />
                  </UFormField>

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
                  <!-- Select Company -->
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

                  <!-- Invoice Date -->
                  <UFormField class="flex flex-col items-start" :label="t('invoices.invoiceDate')" name="invoiceDate" required>
                    <UInput type="date" class="w-[350px]" variant="input" size="xl" v-model="form.invoiceDate" />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyAddress')" name="companyAddress">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyAddress" />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyCity')" name="companyCity">
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyCity" />
                  </UFormField>

                  <UFormField required class="flex flex-col items-start" :label="t('invoices.companyCountry')" name="companyCountry" >
                    <UInput class="w-[350px]" variant="input" size="xl" v-model="form.contactsBook.companyCountry" />
                  </UFormField>
                </div>
              </div>
            </div>

            <USeparator orientation="horizontal" size="xs" class="hidden md:block mt-5" />

            <!-- Cost items -->
            <div>
              <h2 class="text-xl font-semibold mb-4">
                {{ t('invoices.costItems') }}
              </h2>

              <div class="flex flex-col gap-4 items-start w-full">
                <UButton class="mb-4 self-end" @click="openNewCostModal">
                  {{ t('invoices.addCostItem') }}
                </UButton>

                <UModal
                    v-model:open="modalOpen"
                    :title="isEditingCost ? (t('invoices.editCostItem') || 'Edit cost item') : (t('invoices.addCostItem') || 'Add cost item')"
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

                      <p>
                        {{ t('invoices.total') }}:
                        {{ (Number(invoiceCosts.piece) || 0) * (Number(invoiceCosts.cost) || 0) }}
                      </p>

                      <UButton
                          class="px-4 py-2 mt-4 button-item cursor-pointer self-center"
                          type="button"
                          @click="onAddOrUpdateCostItem"
                      >
                        {{ t('misc.submit') }}
                      </UButton>
                    </div>
                  </template>
                </UModal>

                <UTable
                    class="rounded-md w-full"
                    ref="table"
                    :data="filteredCostsList"
                    :columns="columns"
                    :loading="isLoading"
                />
              </div>
            </div>

            <div class="col-span-full flex justify-between mt-4">
              <UButton class="px-4 py-2 rounded-lg" :to="returnTo || '/invoices'">
                {{ t('misc.back') || 'Back' }}
              </UButton>

              <div class="flex gap-3">
                <UButton class="px-4 py-2" type="button" :loading="isGeneratingPdf" @click="downloadPdf">
                  {{ t('invoices.generatePDF') || 'Download PDF' }}
                </UButton>

                <UButton class="px-4 py-2 button-item cursor-pointer" type="submit" :loading="isSaving">
                  {{ t('misc.submit') }}
                </UButton>
              </div>
            </div>
          </UForm>
        </section>
      </main>

      <div class="fixed left-[-10000px] top-0">
        <InvoicePdf
            ref="pdfComp"
            :invoice-number="String(form.invoiceNumber)"
            :invoice-date="String(form.invoiceDate)"
            :invoice-status="String(form.invoiceStatus)"
            :seller="sellerInfo"
            :customer="{
            companyName: form.contactsBook.companyName || '',
            companyAddress: form.contactsBook.companyAddress || '',
            companyCity: form.contactsBook.companyCity || '',
            companyCountry: form.contactsBook.companyCountry || '',
            companyNumber: form.contactsBook.companyNumber || '',
            companyVatNumber: form.contactsBook.companyVatNumber || ''
          }"
            :items="costsList"
            :vat-rate="0"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FormError, FormErrorEvent, FormSubmitEvent, TableColumn } from '@nuxt/ui'
import { ref, reactive, computed, watch, h, resolveComponent, onMounted, nextTick } from 'vue'
import { useAuth, useI18n, useRuntimeConfig, useRoute, useRouter, useToast, useFetch } from '#imports'

import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'
import InvoicePdf from '~/components/InvoicePdf.vue'

definePageMeta({ middleware: 'auth-admin' })

const route = useRoute()
const router = useRouter()
const returnTo = computed(() => route.query.callback as string | undefined)

const modalOpen = ref(false)
const isEditingCost = ref(false)
const editingCostId = ref<number | null>(null)
const isSaving = ref(false)

const { t } = useI18n()
const {
  public: { apiBase }
} = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const toast = useToast()
const isLoading = ref<boolean>(false)

const invoiceId = computed(() => Number(route.params.id))

const isGeneratingPdf = ref(false)
const pdfComp = ref<{ root: HTMLElement | null } | null>(null)

const paymentStatus = ref([
  { label: 'Paid', value: 'Paid' },
  { label: 'Unpaid', value: 'Unpaid' }
])

type SellerInfo = {
  companyName: string
  companyAddress: string
  companyCity: string
  companyCountry: string
  companyNumber: string
  companyVatNumber?: string
  bankAccountCode: string
  bankCode: string
}

const sellerInfo = ref<SellerInfo>({
  companyName: '',
  companyAddress: '',
  companyCity: '',
  companyCountry: '',
  companyNumber: '',
  companyVatNumber: '',
  bankAccountCode: '',
  bankCode: ''
})

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

type ContactsBookApi = {
  id: number
  companyName: string
  companyAddress: string
  companyCity: string
  companyCountry: string
  companyNumber: string
  companyVatNumber: string
}

type InvoiceItemApi = {
  id: number
  service: string
  measure: string
  pieces: number
  cost: number
}

type InvoiceApi = {
  id: number
  invoiceNumber: number
  date: string
  invoiceStatus: string
  contactsBook: ContactsBookApi | null
  invoiceItemList?: InvoiceItemApi[]
  invoiceItem?: InvoiceItemApi[]
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
  contactId: null as ContactOption | null,
  invoiceNumber: '',
  invoiceDate: today,
  invoiceStatus: 'Unpaid',
  contactsBook
})

const errorMessage = ref<string | null>(null)

const { data: availableContacts } = await useFetch<ContactOption[]>(
    `${apiBase}/admin/contacts`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${userToken}`
      },
      transform: (data: {
        id: number
        companyName: string
        companyNumber: string
        companyVatNumber: string
        companyAddress: string
        companyCity: string
        companyCountry: string
      }[]) => {
        return data?.map((contact) => ({
          label: `${contact.companyName} | ${contact.companyNumber}`,
          value: String(contact.id),
          companyName: contact.companyName,
          companyAddress: contact.companyAddress,
          companyCity: contact.companyCity,
          companyCountry: contact.companyCountry,
          companyNumber: contact.companyNumber,
          companyVatNumber: contact.companyVatNumber
        }))
      },
      lazy: true
    }
)

const contactIdFromInvoice = ref<number | null>(null)

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

watch(
    () => [availableContacts.value, contactIdFromInvoice.value],
    ([contacts, contactId]) => {
      if (!contacts || contactId == null) return
      const idStr = String(contactId)
      const match = contacts.find((c) => c.value === idStr)
      if (match) form.contactId = match
    },
    { immediate: true }
)

const validateInfo = (state: any): FormError[] => {
  const errors: FormError[] = []
  if (!state.contactsBook.companyName) errors.push({ name: 'companyName', message: t('errors.required') })
  if (!state.contactsBook.companyNumber) errors.push({ name: 'companyNumber', message: t('errors.required') })
  if (!state.contactsBook.companyAddress) errors.push({ name: 'companyAddress', message: t('errors.required') })
  if (!state.contactsBook.companyCity) errors.push({ name: 'companyCity', message: t('errors.required') })
  if (!state.contactsBook.companyCountry) errors.push({ name: 'companyCountry', message: t('errors.required') })
  if (!state.invoiceDate) errors.push({ name: 'invoiceDate', message: t('errors.required') })
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
const originalItemIds = ref<number[]>([])

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
        costsList.value.length > 0
            ? Math.max(...costsList.value.map((c) => c.id)) + 1
            : 1

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

onMounted(async () => {
  try {
    const res = await fetch(`${apiBase}/admin/company-info`, {
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!res.ok) throw new Error('Failed to fetch company info')
    const data = await res.json()
    sellerInfo.value = {
      companyName: data.companyName ?? '',
      companyAddress: data.companyAddress ?? '',
      companyCity: data.companyCity ?? '',
      companyCountry: data.companyCountry ?? '',
      companyNumber: data.companyNumber ?? '',
      companyVatNumber: data.companyVatNumber ?? '',
      bankAccountCode: data.bankAccountCode ?? '',
      bankCode: data.bankCode ?? ''
    }
  } catch (e) {
    console.error(e)
    toast.add({
      title: t('errors.error'),
      description: 'Failed to load seller company info'
    })
  }

  if (Number.isNaN(invoiceId.value)) {
    console.error('Invalid invoice id:', route.params.id)
    return
  }

  try {
    isLoading.value = true

    const res = await fetch(`${apiBase}/admin/invoices/${invoiceId.value}`, {
      headers: { Authorization: `Bearer ${userToken}` }
    })

    if (!res.ok) throw new Error('Failed to fetch invoice')
    const invoiceData = (await res.json()) as InvoiceApi

    form.invoiceNumber = String(invoiceData.invoiceNumber)
    form.invoiceDate = invoiceData.date || form.invoiceDate
    form.invoiceStatus = invoiceData.invoiceStatus || 'Unpaid'

    if (invoiceData.contactsBook) {
      const cb = invoiceData.contactsBook
      contactIdFromInvoice.value = cb.id

      contactsBook.id = String(cb.id)
      contactsBook.companyName = cb.companyName
      contactsBook.companyNumber = cb.companyNumber
      contactsBook.companyVatNumber = cb.companyVatNumber
      contactsBook.companyAddress = cb.companyAddress
      contactsBook.companyCity = cb.companyCity
      contactsBook.companyCountry = cb.companyCountry
    }

    const items =
        invoiceData.invoiceItemList && invoiceData.invoiceItemList.length
            ? invoiceData.invoiceItemList
            : invoiceData.invoiceItem || []

    costsList.value = items.map((item) => ({
      id: item.id,
      service: item.service,
      measure: item.measure,
      pieces: item.pieces,
      cost: item.cost
    }))

    originalItemIds.value = items.map((it) => it.id)
  } catch (e) {
    console.error(e)
    toast.add({
      title: t('errors.error'),
      description: t('errors.loadInvoiceFailed') || 'Failed to load invoice data'
    })
  } finally {
    isLoading.value = false
  }
})

async function downloadPdf() {
  try {
    isGeneratingPdf.value = true
    await nextTick()

    const el = pdfComp.value?.root
    if (!el) throw new Error('PDF template not available')

    const canvas = await html2canvas(el, {
      scale: 2,
      backgroundColor: '#ffffff',
      useCORS: true,

      onclone: (doc) => {
        const root = doc.body

        root.querySelectorAll('*').forEach((node) => {
          const el = node as HTMLElement
          el.style.color = '#000000'
          el.style.backgroundColor = 'transparent'
          el.style.borderColor = '#000000'
          el.style.boxShadow = 'none'
        })

        doc.body.style.backgroundColor = '#ffffff'
      }
    })

    const imgData = canvas.toDataURL('image/png')

    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()

    const imgProps = pdf.getImageProperties(imgData)
    const imgWidth = pageWidth
    const imgHeight = (imgProps.height * imgWidth) / imgProps.width

    if (imgHeight <= pageHeight) {
      pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight)
    } else {
      let position = 0
      let remaining = imgHeight

      while (remaining > 0) {
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
        remaining -= pageHeight
        position -= pageHeight
        if (remaining > 0) pdf.addPage()
      }
    }

    const filename = `invoice-${form.invoiceNumber || invoiceId.value}.pdf`
    pdf.save(filename)
  } catch (e: any) {
    console.error(e)
    toast.add({
      title: t('errors.error'),
      description: e?.message || 'Failed to generate PDF'
    })
  } finally {
    isGeneratingPdf.value = false
  }
}

async function onSubmitInfo(event: FormSubmitEvent<typeof form>) {
  if (Number.isNaN(invoiceId.value)) return

  try {
    isSaving.value = true

    const updateInvoiceResponse = await fetch(`${apiBase}/admin/invoices/${invoiceId.value}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${userToken}`
      },
      body: JSON.stringify({
        invoiceNumber: Number(form.invoiceNumber),
        date: form.invoiceDate,
        invoiceStatus: form.invoiceStatus,
        contactsBook: form.contactId ? { id: Number(form.contactId.value) } : null
      })
    })

    const headerText = await updateInvoiceResponse.text()

    if (!updateInvoiceResponse.ok) {
      let msg
      try {
        const errJson = JSON.parse(headerText)
        msg = errJson.message || t('errors.updateInvoiceFailed') || 'Failed to update invoice!'
      } catch {
        msg = t('errors.updateInvoiceFailed') || 'Failed to update invoice!'
      }
      throw new Error(msg)
    }

    const currentIds = costsList.value.filter((c) => typeof c.id === 'number').map((c) => c.id)
    const idsToDelete = originalItemIds.value.filter((id) => !currentIds.includes(id))

    const updateOrAddPromises = costsList.value.map((item) => {
      const isExisting = originalItemIds.value.includes(item.id)

      if (isExisting) {
        return fetch(`${apiBase}/admin/invoices/${invoiceId.value}/records/${item.id}`, {
          method: 'PUT',
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
      } else {
        return fetch(`${apiBase}/admin/invoices/${invoiceId.value}/records`, {
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
      }
    })

    const deletePromises = idsToDelete.map((id) =>
        fetch(`${apiBase}/admin/invoices/${invoiceId.value}/records/${id}`, {
          method: 'DELETE',
          headers: { Authorization: `Bearer ${userToken}` }
        })
    )

    const allResponses = await Promise.all([...updateOrAddPromises, ...deletePromises])

    const failed = allResponses.find((res) => !res.ok)
    if (failed) {
      throw new Error(t('errors.updateInvoiceItemsFailed') || 'Failed to update some invoice items')
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.invoiceUpdated'),
      icon: 'qlementine-icons:success-12'
    })

    router.push(returnTo.value || '/invoices')
  } catch (err: any) {
    console.error(err)
    errorMessage.value = err.message || 'An error occurred while updating invoice.'
  } finally {
    isSaving.value = false
  }
}
</script>