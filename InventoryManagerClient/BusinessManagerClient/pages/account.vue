<template>
  <div class="flex flex-col min-h-screen">
    <!-- Header Section -->
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">
          {{ t('titles.myAccount') }}
        </h1>
      </div>
    </header>

    <!-- Main Content Section -->
    <main class="mx-auto w-full">
      <section class="secondary-color shadow-lg rounded-xl p-8">
        <!-- Grid Layout for dividing into two columns -->
        <div class="grid grid-cols-1 md:grid-cols-[1fr_auto_1fr] gap-8">
          <!-- Left Column (Personal Info) -->
          <div>
            <h2 class="text-xl font-semibold mb-4">{{ t('user.personalInfo') }}</h2>
            <UForm class="flex flex-col gap-4 items-center" :validate="validateInfo" :state="employee" @error="onError" @submit="onSubmitInfo">
              <!-- First Name -->
              <UFormField required class="flex flex-col items-start" :label="t('user.firstName')" name="firstName">
                <UInput class="w-[300px]" variant="input" size="xl" v-model="employee.firstName" />
              </UFormField>
              <!-- Last Name -->
              <UFormField required class="flex flex-col items-start" :label="t('user.lastName')" name="lastName">
                <UInput class="w-[300px]" variant="input" size="xl" v-model="employee.lastName" />
              </UFormField>
              <!-- Email -->
              <UFormField required class="flex flex-col items-start" :label="t('user.email')" name="email">
                <UInput class="w-[300px]" variant="input" size="xl" v-model="employee.email" />
              </UFormField>
              <!-- Phone -->
              <UFormField required class="flex flex-col items-start" :label="t('user.mobilePhone')" name="mobilePhone">
                <UInput class="w-[300px]" variant="input" size="xl" v-model="employee.mobilePhone" />
              </UFormField>
              <!-- Address 1 -->
              <UFormField required class="flex flex-col items-start" :label="t('user.address1')" name="address1">
                <UInput class="w-[300px]" variant="input" size="xl" v-model="employee.address1" />
              </UFormField>
              <!-- Address 2  -->
              <UFormField class="flex flex-col items-start" :label="t('user.address2')" name="address2">
                <UInput class="w-[300px]" variant="input" size="xl" v-model="employee.address2" />
              </UFormField>
              <!-- Submit button -->
              <div class="flex self-end">
                <UButton class="px-4 py-2 button-item cursor-pointer" type="submit">{{ t('misc.submit') }}</UButton>
              </div>
            </UForm>
          </div>

          <!-- Divider Section -->
          <USeparator orientation="vertical" size="xs" class="hidden md:block" />
          <!-- Right Column (Password Change) -->
          <div>
            <h2 class="text-xl font-semibold mb-4">{{ t('user.changePassword') }}</h2>
            <UForm class="flex flex-col gap-4 items-center" :validate="validatePass" :state="password" @error="onError" @submit="onSubmitPass">
              <!-- Current Password -->
              <UFormField required class="flex flex-col items-start" :label="t('user.currentPassword')" name="currentPassword">
                  <UInput type="password" class="w-[300px]" variant="input" size="xl" v-model="password.currentPassword" />
              </UFormField>
              <!-- New Password -->
              <UFormField required class="flex flex-col items-start" :label="t('user.newPassword')" name="newPassword">
                  <div class="space-y-2 w-[300px]">
                    <UInput
                        v-model="password.newPassword"
                        variant="input"
                        size="xl"
                        :type="show ? 'text' : 'password'"
                        :aria-invalid="score < 4"
                        aria-describedby="password-strength"
                        :ui="{ trailing: 'pe-1' }"
                        class="w-full"
                    >
                      <template #trailing>
                        <UButton
                            variant="icon"
                            size="xl"
                            :icon="show ? 'i-lucide-eye-off' : 'i-lucide-eye'"
                            :aria-label="show ? 'Hide password' : 'Show password'"
                            :aria-pressed="show"
                            aria-controls="password"
                            @click="show = !show"
                        />
                      </template>
                    </UInput>

                    <UProgress
                        :color="color"
                        :indicator="text"
                        :model-value="score"
                        :max="4"
                        size="sm"
                    />

                    <p id="password-strength" class="text-sm font-medium">
                      {{ text }}. Must contain:
                    </p>

                    <ul class="space-y-1" aria-label="Password requirements">
                      <li
                          v-for="(req, index) in strength"
                          :key="index"
                          class="flex items-center gap-0.5"
                          :class="req.met ? 'text-success' : 'text-highlighted'"
                      >
                        <UIcon :name="req.met ? 'i-lucide-circle-check' : 'i-lucide-circle-x'" class="size-4 shrink-0" />

                        <span class="text-xs font-light">
                          {{ req.text }}
                          <span class="sr-only">
                            {{ req.met ? ' - Requirement met' : ' - Requirement not met' }}
                          </span>
                        </span>
                      </li>
                    </ul>
                  </div>
              </UFormField>
              <!-- Confirm New Password -->
              <UFormField class="flex flex-col items-start" :label="t('user.confirmPassword')" name="confirmPassword">
                  <UInput type="password" class="w-[300px]" variant="input" size="xl" v-model="password.confirmPassword" />
              </UFormField>
              <!-- Password Change Button -->
              <div class="flex self-end">
                <UButton class="px-4 py-2 button-item cursor-pointer" type="submit">{{ t('misc.submit') }}</UButton>
              </div>
            </UForm>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import type {FormError, FormErrorEvent, FormSubmitEvent} from "@nuxt/ui";

const show = ref(false)

function checkStrength(str: string) {
  const requirements = [
    { regex: /.{8,}/, text: 'At least 8 characters' },
    { regex: /\d/, text: 'At least 1 number' },
    { regex: /[a-z]/, text: 'At least 1 lowercase letter' },
    { regex: /[A-Z]/, text: 'At least 1 uppercase letter' }
  ]

  return requirements.map(req => ({ met: req.regex.test(str), text: req.text }))
}

const strength = computed(() => checkStrength(password.value.newPassword))
const score = computed(() => strength.value.filter(req => req.met).length)

const color = computed(() => {
  if (score.value === 0) return 'neutral'
  if (score.value <= 1) return 'error'
  if (score.value <= 2) return 'warning'
  if (score.value === 3) return 'warning'
  return 'success'
})

const text = computed(() => {
  if (score.value === 0) return 'Enter a password'
  if (score.value <= 2) return 'Weak password'
  if (score.value === 3) return 'Medium password'
  return 'Strong password'
})

import { ref, onMounted } from 'vue';
import { useAuth } from '#imports';

const { public: { apiBase } } = useRuntimeConfig();

const { t } = useI18n()
const toast = useToast();
const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token;

const employee = ref({
  firstName: '',
  lastName: '',
  email: '',
  mobilePhone: '',
  address1: '',
  address2: '',
  id: ''
});

const password = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const state = reactive({
  firsName: undefined,
  lastName: undefined,
  email: undefined,
  mobilePhone: undefined,
  address1: undefined,
  currentPassword: undefined,
  newPassword: undefined,
  confirmPassword: undefined
})
const errorMessage = ref('');


const validateInfo = (state: any): FormError[] => {
  const errors = []
  if (!state.firstName) errors.push({ name: 'firstName', message: t('errors.required') })
  if (!state.lastName) errors.push({ name: 'lastName', message: t('errors.required') })
  if (!state.email) errors.push({ name: 'email', message: t('errors.required') })
  if (!state.mobilePhone) errors.push({ name: 'mobilePhone', message: t('errors.required') })
  if (!state.address1) errors.push({ name: 'address1', message: t('errors.required') })
  return errors
}
const validatePass = (state: any): FormError[] => {
  const errors: FormError[] = []

  if (!state.currentPassword) errors.push({ name: 'currentPassword', message: t('errors.required') })
  if (!state.newPassword) errors.push({ name: 'newPassword',     message: t('errors.required') })
  if (!state.confirmPassword) errors.push({ name: 'confirmPassword', message: t('errors.required') })

  const pwd: string = state.newPassword || ''

  if (state.confirmPassword && pwd && state.confirmPassword !== pwd) {
    errors.push({ name: 'confirmPassword', message: t('errors.pswMustMatch') })
  }

  if (pwd && score.value < 4) {
    errors.push({ name: 'newPassword', message: t('errors.pswTooWeak') })
  }

  return errors
}

async function onError(event: FormErrorEvent) {
  if (event?.errors?.[0]?.id) {
    const element = document.getElementById(event.errors[0].id)
    element?.focus()
    element?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

const findEmployee = async () => {
  try {
    const userEmail = (data?.value?.user as any)?.email;
    if (!userEmail) {
      throw new Error("User email not found!");
    }
    const response = await fetch(`${apiBase}/admin/employees/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      }
    });
    if (!response.ok) {
      throw new Error("Failed to fetch employee!");
    }
    const employeeData = await response.json();
    employee.value = { ...employeeData, id: employeeData.id };
  } catch (error) {
    console.error(error);
  }
};

async function onSubmitInfo(event: FormSubmitEvent<typeof state>) {
  try {
    const response = await fetch(`${apiBase}/admin/employees/me`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        firstName: employee.value.firstName,
        lastName: employee.value.lastName,
        email: employee.value.email,
        mobilePhone: employee.value.mobilePhone,
        address1: employee.value.address1,
        address2: employee.value.address2,
      }),
    });

    if (!response.ok) {
      throw new Error('Failed to update employee information!');
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.accountInfoChanged'),
      icon: 'qlementine-icons:success-12'
    })

  } catch (error) {
    console.error(error);
  }
  findEmployee();
};

async function onSubmitPass(event: FormSubmitEvent<typeof state>) {
  try {
    const verifyResponse = await fetch(`${apiBase}/admin/employees/me/password`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${userToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        currentPassword: password.value.currentPassword,
        newPassword: password.value.newPassword,
        confirmNewPassword: password.value.confirmPassword
      }),
    });

    if (!verifyResponse.ok) {
      const errorData = await verifyResponse.json();
      throw new Error(errorData.message || 'Failed to change password.');
    }

    password.value.currentPassword = '';
    password.value.newPassword = '';
    password.value.confirmPassword = '';

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.pswChanged'),
      icon: 'qlementine-icons:success-12'
    })

  } catch (error) {
    errorMessage.value = t('errors.passChangeFail');
  }
};

onMounted(() => {
  findEmployee();
});
</script>
