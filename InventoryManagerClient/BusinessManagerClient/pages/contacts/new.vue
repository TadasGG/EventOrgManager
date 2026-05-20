<template>
  <div class="flex flex-col min-h-screen">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">
          {{ t('titles.newContact') }}
        </h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <UForm class="flex flex-col items-center gap-4" :validate="validate" :state="contact" @error="onError" @submit="onSubmit">
        <!-- Company Name -->
        <UFormField class="flex flex-col items-start" :label="t('contacts.companyName')" required size="xl" name="companyName">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="contact.companyName" />
        </UFormField>

        <!-- Company Address -->
        <UFormField class="flex flex-col items-start" :label="t('contacts.companyAddress')" required size="xl" name="companyAddress">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="contact.companyAddress" />
        </UFormField>

        <!-- Company City -->
        <UFormField class="flex flex-col items-start" :label="t('contacts.companyCity')" required size="xl" name="companyCity">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="contact.companyCity" />
        </UFormField>

        <!-- Company Country -->
        <UFormField class="flex flex-col items-start" :label="t('contacts.companyCountry')" required size="xl" name="companyCountry">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="contact.companyCountry" />
        </UFormField>

        <!-- Company Number -->
        <UFormField class="flex flex-col items-start" :label="t('contacts.companyNumber')" required size="xl" name="companyNumber">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="contact.companyNumber" />
        </UFormField>

        <!-- Company VAT Number -->
        <UFormField class="flex flex-col items-start" :label="t('contacts.companyVatNumber')" size="xl">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="contact.companyVatNumber" />
        </UFormField>

        <!-- Submit button -->
        <div class="flex w-full justify-between mt-4">
          <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/contacts`">{{ t('misc.back') }}</UButton>
          <UButton type="submit" class="px-4 py-2 rounded-lg">{{ t('misc.submit') }}</UButton>
        </div>
      </UForm>
    </main>
  </div>
</template>

<script setup lang="ts">
import type {FormError, FormErrorEvent, FormSubmitEvent} from "@nuxt/ui";

definePageMeta({
  middleware: 'auth-admin',
});

import {computed, ref} from 'vue';
import { useAuth } from '#imports';

const route = useRoute()
const returnTo = computed(() => route.query.callback as string | undefined)

const { t } = useI18n()
const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token
const { public: { apiBase } } = useRuntimeConfig()

const state = reactive({
  companyName: undefined,
  companyAddress: undefined,
  companyCity: undefined,
  companyCountry: undefined,
  companyNumber: undefined,
  companyVatNumber: undefined
})

const validate = (state: any): FormError[] => {
  const errors = []
  if (!state.companyName) errors.push({ name: 'companyName', message: t('errors.required') })
  if (!state.companyAddress) errors.push({ name: 'companyAddress', message: t('errors.required') })
  if (!state.companyCity) errors.push({ name: 'companyCity', message: t('errors.required') })
  if (!state.companyCountry) errors.push({ name: 'companyCountry', message: t('errors.required') })
  if (!state.companyNumber) errors.push({ name: 'companyNumber', message: t('errors.required') })
  return errors
}
async function onError(event: FormErrorEvent) {
  if (event?.errors?.[0]?.id) {
    const element = document.getElementById(event.errors[0].id)
    element?.focus()
    element?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

const contact = ref({
  companyName: '',
  companyAddress: '',
  companyCity: '',
  companyCountry: '',
  companyNumber: '',
  companyVatNumber: ''
});

const toast = useToast()
const router = useRouter()

async function onSubmit(event: FormSubmitEvent<typeof state>) {
  const requestBody = {
    companyName: contact.value.companyName,
    companyAddress: contact.value.companyAddress,
    companyCity: contact.value.companyCity,
    companyCountry: contact.value.companyCountry,
    companyNumber: contact.value.companyNumber,
    companyVatNumber: contact.value.companyVatNumber,
  };

  try {
    const response = await fetch(`${apiBase}/admin/contacts`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    });

    if (!response.ok) {
      throw new Error('Failed to create contact information!');
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.newContact'),
      icon: 'qlementine-icons:success-12'
    })

    router.push(returnTo.value || "/contacts")

  } catch (error) {
    console.error(error);
  }
}
</script>