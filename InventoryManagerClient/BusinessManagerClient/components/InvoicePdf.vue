<template>
  <div
      ref="root"
      class="w-[794px] p-10"
      style="color:#000; background:#fff; font-family: Arial, Helvetica, sans-serif;"
  >
    <!-- A4-ish width at ~96DPI: 794px -->
    <div
        ref="root"
        style="all: revert; color:#000; background:#fff; font-family: Arial;"
        class="w-[794px] p-10"
    >
      <!-- Header -->
      <div class="flex justify-between items-start">
        <div class="text-sm text-left leading-6 max-w-[360px]">
          <div class="font-bold mb-2">Service Provider</div>
          <div class="font-bold">{{ seller.companyName }}</div>
          <div>{{ seller.companyAddress }}</div>
          <div>{{ seller.companyCity }}, {{ seller.companyCountry }}</div>
          <div>Company No: {{ seller.companyNumber }}</div>
          <div v-if="seller.companyVatNumber">VAT: {{ seller.companyVatNumber }}</div>
          <br>
          <div>Bank code: {{ seller.bankCode }}</div>
          <div>Bank account no.: {{ seller.bankAccountCode }}</div>
        </div>

        <div class="text-sm text-right leading-6 max-w-[360px]">
          <div class="font-bold mb-2">Customer</div>
          <div class="font-bold">{{ customer.companyName }}</div>
          <div>{{ customer.companyAddress }}</div>
          <div>{{ customer.companyCity }}, {{ customer.companyCountry }}</div>
          <div>Company No: {{ customer.companyNumber }}</div>
          <div v-if="customer.companyVatNumber">VAT: {{ customer.companyVatNumber }}</div>
        </div>
      </div>

      <div class="my-10">
        <div class="text-sm mt-3 space-y-1">
          <div><span class="font-semibold">Invoice no.:</span> {{ invoiceNumber }}</div>
          <div><span class="font-semibold">Document date:</span> {{ invoiceDate }}</div>
        </div>
      </div>

      <div class="border-t my-6"></div>

      <!-- Items table -->
      <table class="w-full text-sm border-collapse">
        <thead>
        <tr class="border-b">
          <th class="text-left py-2 w-[40px]">#</th>
          <th class="text-left py-2">Service</th>
          <th class="text-left py-2 w-[70px]">Qty</th>
          <th class="text-left py-2 w-[80px]">Unit</th>
          <th class="text-right py-2 w-[90px]">Price</th>
          <th class="text-right py-2 w-[100px]">Total</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="(it, idx) in items" :key="it.id" class="border-b align-top">
          <td class="py-2">{{ idx + 1 }}</td>
          <td class="py-2 whitespace-pre-wrap break-words">{{ it.service }}</td>
          <td class="py-2">{{ it.pieces }}</td>
          <td class="py-2">{{ it.measure }}</td>
          <td class="py-2 text-right">{{ fmt(it.cost) }}</td>
          <td class="py-2 text-right">{{ fmt(it.pieces * it.cost) }}</td>
        </tr>

        <tr v-if="!items.length">
          <td colspan="6" class="py-6 text-center text-gray-600">
            No items
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Totals -->
      <div class="flex justify-end mt-6">
        <div class="w-[300px] text-sm">
          <div class="flex justify-between py-3 border-t text-base font-bold">
            <span>Total</span>
            <span>{{ fmt(total) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = defineProps<{
  invoiceNumber: string
  invoiceDate: string
  invoiceStatus: string
  seller: {
    companyName: string
    companyAddress: string
    companyCity: string
    companyCountry: string
    companyNumber: string
    companyVatNumber?: string
    bankAccountCode: string
    bankCode: string
  }
  customer: {
    companyName: string
    companyAddress: string
    companyCity: string
    companyCountry: string
    companyNumber: string
    companyVatNumber?: string
  }
  items: { id: number; service: string; measure: string; pieces: number; cost: number }[]
}>()

const total = computed(() =>
    props.items.reduce((sum, i) => sum + (Number(i.pieces) || 0) * (Number(i.cost) || 0), 0)
)

function fmt(n: number) {
  return new Intl.NumberFormat('lt-LT', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(Number.isFinite(n) ? n : 0)
}

const root = ref<HTMLElement | null>(null)
defineExpose({ root })
</script>