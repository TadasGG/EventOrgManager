<template>
  <div class="flex flex-col min-h-screen">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('titles.categoryInfo') }}</h1>
      </div>
    </header>

    <main class="secondary-color shadow-lg rounded-xl p-8">
      <UForm class="flex flex-col items-center gap-8" :validate="validate" :state="category" @error="onError" @submit="onSubmit">
        <!-- Name -->
        <UFormField class="flex flex-col items-start" :label="t('categories.title')" required size="xl" name="name">
          <UInput class="w-[300px]" size="xl" placeholder="..." v-model="category.name" />
        </UFormField>

        <!-- Description -->
        <UFormField class="flex flex-col items-start" :label="t('categories.description')" size="xl">
          <UTextarea class="w-[300px]" size="xl" placeholder="..." v-model="category.description" />
        </UFormField>

        <!-- Submit button -->
        <div class="flex w-full justify-between mt-4">
          <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/roles`">{{ t('misc.back') }}</UButton>
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

import {ref, onMounted, computed} from 'vue';
import { useAuth } from '#imports';

const route = useRoute()
const returnTo = computed(() => route.query.callback as string | undefined)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig();

const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token;

const state = reactive({
  name: undefined,
  color: undefined
})

const validate = (state: any): FormError[] => {
  const errors = []
  if (!state.name) errors.push({ name: 'name', message: t('errors.required') })
  return errors
}
async function onError(event: FormErrorEvent) {
  if (event?.errors?.[0]?.id) {
    const element = document.getElementById(event.errors[0].id)
    element?.focus()
    element?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

const category = ref({
  name: '',
  description: '',
  color: ''
});

const errorMessages = ref<string[]>([]);
const { id } = useRoute().params;
const toast = useToast()
const router = useRouter();

const findCategory = async () => {
  try {
    const response = await fetch(`${apiBase}/admin/items-categories/${id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userToken}`
      }
    });

    if (!response.ok) {
      throw new Error("Failed to fetch category!");
    }

    const categoryData = await response.json();
    category.value = { ...categoryData, id: categoryData.id };
  } catch (error) {
    console.error(error);
  }
};

async function onSubmit(event: FormSubmitEvent<typeof state>) {
  const requestBody = {
    name: category.value.name,
    description: category.value.description,
  };

  try {
    const response = await fetch(`${apiBase}/admin/items-categories/${id}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    });

    if (!response.ok) {
      throw new Error('Failed to update category information!');
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.newItemCat'),
      icon: 'qlementine-icons:success-12'
    })

    router.push(returnTo.value || "/items/categories")

  } catch (error) {
    console.error(error);
  }
}

onMounted(() => {
  findCategory();
});
</script>
