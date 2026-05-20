<template>
  <Teleport to="body">
    <Transition name="fade">
      <div
          v-if="errorMessage"
          class="fixed bottom-6 right-6 z-[1000] pointer-events-none"
      >
        <UAlert
            color="error"
            variant="solid"
            :title="t('errors.error')"
            :description="errorMessage"
            icon="i-lucide-alert-triangle"
            class="shadow-2xl rounded-xl pointer-events-auto"
        />
      </div>
    </Transition>
  </Teleport>

  <div class="flex flex-col">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto">
        <h1 class="text-center" style="font-family: 'Arial'">{{ t('misc.employeeInfo') }}</h1>
      </div>
    </header>

    <main class="flex-1 container mx-auto">
      <section class="secondary-color shadow-lg rounded-xl p-8">
        <UForm class="grid grid-cols-1 md:grid-cols-[1fr_auto_1fr] gap-8" :validate="validateInfo" :state="employee" @error="onError" @submit="onSubmitInfo">
          <!-- Left Column -->
          <div>
            <h2 class="text-xl font-semibold mb-4">{{ t('user.personalInfo') }}</h2>
            <div class="flex flex-col gap-4 items-center">
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
            </div>
          </div>

          <USeparator orientation="vertical" size="xs" class="hidden md:block" />

          <!-- Right Column -->
          <div>
            <h2 class="text-xl font-semibold mb-4">{{ t('employees.accountInfo') }}</h2>
            <div class="flex flex-col gap-4 items-center">
              <!-- New Password -->
              <UFormField required class="flex flex-col items-start" :label="t('user.newPassword')" name="password">
                <div class="space-y-2 w-[300px]">
                  <UInput
                      v-model="employee.password"
                      variant="input"
                      size="xl"
                      :type="show ? 'text' : 'password'"
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
                </div>
              </UFormField>
              <!-- Confirm New Password -->
              <UFormField required class="flex flex-col items-start" :label="t('user.confirmPassword')" name="confirmPassword">
                <UInput type="password" class="w-[300px]" variant="input" size="xl" v-model="employee.confirmPassword" />
              </UFormField>

              <!-- Roles -->
              <UFormField class="flex flex-col items-start" :label="t('employees.roles')" name="roles">
                <div class="flex items-center">
                  <!-- Current roles -->
                  <div class="w-[255px] rounded-md secondary-object-item p-2 flex flex-wrap gap-2">
                    <template v-if="employee.roleList.length">
                      <UBadge
                          v-for="role in employee.roleList"
                          :key="role.id"
                          size="lg"
                          variant="solid"
                          class="flex items-center gap-1"
                          :style="{ backgroundColor: role.color }"
                      >
                        <span>{{ role.name }}</span>
                        <UButton
                            icon="typcn:delete-outline"
                            @click="handleRemoveRole(role)"
                            variant="ghost"
                            size="xs"
                            class=""
                        />
                      </UBadge>
                    </template>
                    <span v-else class="text-sm text-muted">{{ t('employees.noRoles') }}</span>
                  </div>

                  <div class="gap-2 pl-2 self-start" ref="dropdownRef">
                    <UButton icon="gg:add" size="lg" class="px-2" @click.stop="showDropdown = !showDropdown" />

                    <div
                        v-if="showDropdown"
                        class="absolute z-50 mt-2 secondary-object-item border shadow-lg rounded-lg p-3"
                        @click.stop
                    >
                      <select v-model="selectedRoleId" class="border rounded-md p-2 object-item">
                        <option disabled value="">-- {{ t('misc.selectRole') }} --</option>
                        <option v-for="r in selectableRoles" :key="r.id" :value="String(r.id)">
                          {{ r.name }}
                        </option>
                      </select>

                      <div class="flex gap-2 mt-2">
                        <UButton class="w-full justify-center mt-2" :label="t('misc.addRole')" :disabled="!selectedRoleId" @click="addRole" />
                      </div>
                    </div>
                  </div>
                </div>
              </UFormField>

            </div>
          </div>
          <!-- Submit button -->
          <div class="col-span-full flex justify-between mt-4">
            <UButton class="px-4 py-2 rounded-lg" :to="returnTo || `/employees`">{{ t('misc.back') || 'Back' }}</UButton>
            <UButton class="px-4 py-2 button-item cursor-pointer" type="submit">{{ t('misc.submit') }}</UButton>
          </div>
        </UForm>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import type {FormError, FormErrorEvent, FormSubmitEvent} from "@nuxt/ui";

definePageMeta({ middleware: 'auth-admin' })

const route = useRoute()
const returnTo = computed(() => route.query.callback as string | undefined)
const selectedEmployee = computed(() => route.query.employee as string | undefined)

import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useAuth } from '#imports'
import { vMaska } from 'maska/vue'

const show = ref(false)

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig()
const { data } = useAuth()
const userToken = (data?.value?.user as any)?.token
const toast = useToast()
const router = useRouter()

const employee = ref({
  firstName: '',
  lastName: '',
  address1: '',
  address2: '',
  email: '',
  mobilePhone: '',
  password: '',
  confirmPassword: '',
  roleList: [] as Array<{ id: number; name: string; color: any }>
})

const state = reactive({
  firsName: undefined,
  lastName: undefined,
  email: undefined,
  mobilePhone: undefined,
  address1: undefined,
  address2: undefined,
  newPassword: undefined,
  confirmPassword: undefined
})

const validateInfo = (state: any): FormError[] => {
  const errors = []
  if (!state.firstName) errors.push({ name: 'firstName', message: t('errors.required') })
  if (!state.lastName) errors.push({ name: 'lastName', message: t('errors.required') })
  if (!state.email) errors.push({ name: 'email', message: t('errors.required') })
  if (!state.mobilePhone) errors.push({ name: 'mobilePhone', message: t('errors.required') })
  if (!state.address1) errors.push({ name: 'address1', message: t('errors.required') })


  if (!state.password) errors.push({ name: 'password',     message: t('errors.required') })
  if (!state.confirmPassword) errors.push({ name: 'confirmPassword', message: t('errors.required') })

  const pwd: string = state.password || ''

  if (state.confirmPassword && pwd && state.confirmPassword !== pwd) {
    errors.push({ name: 'confirmPassword', message: 'Passwords must match' })
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

const errorMessage = ref('');
const availableRoles = ref<Array<{ id: number; name: string; color: string }>>([])
const selectedRoleId = ref('')
const showDropdown = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

const fetchAvailableRoles = async () => {
  try {
    const response = await fetch(`${apiBase}/admin/roles`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${userToken}` }
    })
    if (!response.ok) throw new Error(t('errors.fetchRolesFailed') || 'Failed to fetch available roles!')
    availableRoles.value = await response.json()
  } catch (error) {
    console.error(error)
  }
}

const selectableRoles = computed(() =>
    availableRoles.value.filter(ar => !employee.value.roleList.some(er => er.id === ar.id))
)

const handleClickOutside = (e: MouseEvent) => {
  const target = e.target as Node
  if (showDropdown.value && dropdownRef.value && !dropdownRef.value.contains(target)) {
    showDropdown.value = false
  }
}

const handleRemoveRole = (role: { id: number; name: string }) => {
  if (role.name?.toUpperCase().includes('ADMIN')) {
    const confirmDelete = confirm(t('misc.confirmRemoveAdminRole') || 'Are you sure you want to remove this role?')
    if (!confirmDelete) return
  }
  employee.value.roleList = employee.value.roleList.filter(r => r.id !== role.id)
}

const addRole = () => {
  if (!selectedRoleId.value) return
  const roleIdNum = Number(selectedRoleId.value)
  const roleToAdd = availableRoles.value.find(r => r.id === roleIdNum)
  if (!roleToAdd) return
  if (employee.value.roleList.some(r => r.id === roleToAdd.id)) return
  employee.value.roleList.push({ ...roleToAdd })
  selectedRoleId.value = ''
  showDropdown.value = false
}

async function onSubmitInfo(event: FormSubmitEvent<typeof state>) {
  try {
    const response = await fetch(`${apiBase}/admin/employees`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${userToken}` },
      body: JSON.stringify({
        firstName: employee.value.firstName,
        lastName: employee.value.lastName,
        email: employee.value.email,
        mobilePhone: employee.value.mobilePhone,
        address1: employee.value.address1,
        address2: employee.value.address2,
        password: employee.value.password,
        roleList: employee.value.roleList
      }),
    })

    const responseText = await response.text()
    if (!response.ok) {
      let errorMessage
      try {
        const errorResponse = JSON.parse(responseText)
        errorMessage = errorResponse.message || (t('errors.createEmployeeFailed') || 'Failed to create new employee!')
      } catch {
        errorMessage = t('errors.createEmployeeFailed') || 'Failed to create new employee!'
      }
      throw new Error(errorMessage)
    }

    toast.add({
      title: t('alerts.success'),
      description: t('alerts.employeeAdded'),
      icon: 'qlementine-icons:success-12'
    })

    router.push(returnTo.value || '/employees')

  } catch (error: any) {
    console.error(error)
    errorMessage.value.general = error.message || (t('errors.genericUpdateFailed') || 'An error occurred while updating employee information.')
  }
};

onMounted(() => {
  fetchAvailableRoles()
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>