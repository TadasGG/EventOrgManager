<template>
  <div class="flex flex-col min-h-screen">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center font-bold" style="font-family: 'Arial'">{{ t('titles.roleInfo') }}</h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <UForm class="flex flex-col items-center gap-8" :validate="validate" :state="role" @error="onError" @submit="onSubmit">
        <UFormField class="flex flex-col items-start" :label="t('roles.title')" required size="xl" name="name">
          <UInput required class="w-[300px]" size="xl" placeholder="..." v-model="role.name" />
        </UFormField>

        <UFormField class="flex flex-col items-start" :label="t('roles.description')" size="xl">
          <UTextarea class="w-[300px]" size="xl" placeholder="..." v-model="role.description" />
        </UFormField>

        <UFormField class="flex flex-col items-start" :label="t('roles.color')" required size="xl" name="color">
          <color-picker
              class="w-[300px]"
              v-model="role.color"
              withHexInput
              v-slot="{ show }"
          >
            <div
                @click="show"
                class="cursor-pointer rounded-lg flex border">
              <div
                  class="flex-1 h-8 rounded-lg"
                  :style="{ backgroundColor: role.color }">
              </div>
            </div>
          </color-picker>
        </UFormField>
        <!-- Buttons -->
        <div class="flex w-full justify-between mt-10">
          <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/roles`">{{ t('misc.back') }}</UButton>
          <UButton type="submit" class="px-4 py-2 rounded-lg">{{ t('misc.submit') }}</UButton>
        </div>
      </UForm>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth-admin' })

import { ref, onMounted } from 'vue'
import { useAuth } from '#imports'
import type { FormError, FormErrorEvent, FormSubmitEvent } from '@nuxt/ui'

const route = useRoute()
const returnTo = computed(() => route.query.callback as string | undefined)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const toast = useToast()
const router = useRouter()

const state = reactive({
  name: undefined,
  color: undefined
})

const validate = (state: any): FormError[] => {
  const errors = []
  if (!state.name) errors.push({ name: 'name', message: t('errors.required') })
  if (!state.color) errors.push({ name: 'color', message: t('errors.required') })
  return errors
}
async function onError(event: FormErrorEvent) {
  if (event?.errors?.[0]?.id) {
    const element = document.getElementById(event.errors[0].id)
    element?.focus()
    element?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

const role = ref({
  name: '',
  description: '',
  color: '',
  id: ''
})

const { id } = useRoute().params

const findRole = async () => {
  try {
    const response = await fetch(`${apiBase}/admin/roles/${id}`, {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error('Failed to fetch role!')
    const roleData = await response.json()
    role.value = { ...roleData, id: roleData.id }
  } catch (error) {
    console.error(error)
  }
}

async function onSubmit(event: FormSubmitEvent<typeof state>) {
  const requestBody = {
    name: role.value.name,
    description: role.value.description,
    color: role.value.color
  }

  try {
    const response = await fetch(`${apiBase}/admin/roles/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${userToken}`
      },
      body: JSON.stringify(requestBody)
    })

    const responseText = await response.text()
    if (!response.ok) {
      let errorMessage
      try {
        const errorResponse = JSON.parse(responseText)
        errorMessage = errorResponse.message || 'Failed to update role information!'
      } catch {
        errorMessage = 'Failed to update role information!'
      }
      throw new Error(errorMessage)
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.roleUpdated'),
      icon: 'qlementine-icons:success-12'
    })
    router.push(returnTo.value || '/roles')
  } catch (error) {
  }
}

onMounted(() => {
  findRole()
})
</script>