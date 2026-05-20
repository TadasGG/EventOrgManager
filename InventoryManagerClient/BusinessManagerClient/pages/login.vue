<template>
  <Teleport to="body">
    <Transition name="fade">
      <div
          v-if="errorMessage"
          class="fixed bottom-6 right-6 z-[1000] pointer-events-none"
      >
        <UAlert
            color="error"
            variant="subtle"
            :title="t('errors.error')"
            :description="errorMessage"
            icon="i-lucide-alert-triangle"
            class="shadow-2xl rounded-xl pointer-events-auto"
        />
      </div>
    </Transition>
  </Teleport>

  <div class="flex flex-col justify-center h-full py-5">
    <div class="flex justify-between mx-5 p-2">
      <ThemeToggle />

      <select v-model="picked" @change="onLocaleChange" class="border rounded-lg p-1">
        <option value="en">EN</option>
        <option value="lt">LT</option>
      </select>

    </div>
    <div class="flex flex-col items-center justify-between h-full">
      <div class="secondary-color flex flex-col rounded-xl items-center justify-between shadow-xl">
        <h1 class="py-5">{{ t('titles.login') }}</h1>

        <form class=" px-[80px] py-[40px] flex flex-col items-center" style="width: 120%" @submit.prevent="handleLogin">
          <div class="flex flex-col w-full">
            <label>{{ t('user.email') }}:</label>
            <UInput class="w-full" v-model="email" size="xl" type="email" placeholder="..." />

            <label for="password" class="mt-5">{{ t('user.password') }}:</label>
            <UInput
                v-model="password"
                variant="input"
                size="xl"
                placeholder="..."
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
          <div class="flex justify-center w-full my-5">
            <UButton type="submit" class="px-4 py-2" style="font-size: 20px">{{ t('misc.login') }}</UButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, ref} from 'vue';
import { useAuth, useRouter } from '#imports';

const show = ref(false)
const { t, locale, setLocale } = useI18n()
const switchLocalePath = useSwitchLocalePath()
const route = useRoute()
const picked = ref(locale.value)

const onLocaleChange = async () => {
  const lang = picked.value

  await setLocale(lang)

  const path = switchLocalePath(lang)
  if (path && path !== route.fullPath) {
    await navigateTo(path)
  }
  localStorage.setItem('locale', lang)
}

const { signIn } = useAuth();
const router = useRouter();

const email = ref('');
const password = ref('');
const errorMessage = ref('');

definePageMeta({
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/',
  },
  hideSidebar: true,
  layout: false
});

const callbackUrl =
    (route.query.callbackUrl as string | undefined) || '/'

const handleLogin = async (form: { email: string; password: string }) => {
  await signIn('credentials', {
    email: email.value,
    password: password.value,
    callbackUrl
  })
}
</script>