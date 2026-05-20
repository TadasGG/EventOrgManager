<template>
  <div class="primary-color p-3 shadow-[0px_0_10px_1px_rgba(0,0,0,0.4)]">
    <div class="flex flex-col justify-between h-full">
      <!-- Header -->
      <ULink to="/" class="text-highlighted">
        <header class="flex items-center gap-2 py-4 transition rounded">
          <div class="overflow-hidden rounded-lg w-[80px]" >
            <Logo />
          </div>
          <p style="font-size: 24px" class="font-bold whitespace-normal break-words">{{ styleInfo.websiteName }}</p>
        </header>
      </ULink>

      <!-- Menu Items -->
      <div class="px-2 grow">
        <div class="grid gap-2">
          <UNavigationMenu v-if="!isAdmin" orientation="vertical" :items="userItems" />
          <UNavigationMenu v-if="isAdmin" orientation="vertical" :items="adminItems" />
        </div>
      </div>

      <!-- User avatar -->
      <div>
        <UPopover>
          <!-- Trigger -->
          <div class="flex shadow-md w-full justify-start items-center gap-1 secondary-color rounded-lg p-2 cursor-pointer hover:scale-[105%]">
            <UAvatar size="lg" src="" :alt="userName.firstName + ' ' + userName.lastName" />
            <div>
              <p class="truncate leading-none text-base font-bold">{{ userName.firstName + ' ' + userName.lastName }}</p>
              <p v-if="isAdmin" class="text-xs">(Admin)</p>
            </div>
          </div>

          <!-- Dropdown content -->
          <template #content>
            <div class="flex flex-col object-item rounded-md p-2">
              <UButton v-if="isAdmin" to="/admin" icon="ri:settings-5-fill" variant="object" size="md" class="justify-center cursor-pointer hover:scale-[105%] w-[207px]">{{ t('misc.adminPanel') }}</UButton>
              <UButton to="/account" icon="pajamas:profile" variant="object" size="md" class="justify-center cursor-pointer hover:scale-[105%]">{{ t('misc.myAccount') }}</UButton>
              <UButton @click="handleSignOut" icon="ic:round-logout" variant="object" size="md" class="justify-center cursor-pointer hover:scale-[105%]">{{ t('misc.logout') }}</UButton>
            </div>
          </template>
        </UPopover>
      </div>

      <!-- Theme Toggle -->
      <div class="flex mt-2 justify-between">
        <ThemeToggle />

        <select v-model="picked" @change="onLocaleChange" class="border-1 border-white rounded-lg p-1">
          <option value="en">EN</option>
          <option value="lt">LT</option>
        </select>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'
import {ref, computed, onMounted} from 'vue'
import { useAuth } from '#imports'
import {use} from "h3";

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

const { data, signOut } = useAuth()

const { public: { apiBase } } = useRuntimeConfig();

const userToken = data.value.user.token;

const isAdmin = ref(false);
const userName = ref({
  firstName: '',
  lastName: ''
});

const styleInfo = ref({
  id: "",
  websiteName: "",
  websiteLogo: "",
  primaryColor: "",
  secondaryColor: ""
});

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
    userName.value = { ...employeeData, id: employeeData.id };
  } catch (error) {
    console.error(error);
  }
};

const findWebsiteStyle = async () => {
  try {
    const response = await fetch(`${apiBase}/public/styles`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${userToken}`,
        "Content-Type": "application/json",
      },
    });
    if (!response.ok) {
      throw new Error("Failed to fetch style!");
    }
    const styleData = await response.json();
    styleInfo.value = { ...styleData, id: styleData.id };

    console.log("Styles:", styleInfo.value);
  } catch (error) {
    console.error(error);
  }
};

onMounted(async () => {
  if (!data.value?.user) return; // safety check

  try {
    const response = await fetch(`${apiBase}/admin/employees/me/roles`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userToken}`
      }
    });

    if (!response.ok) {
      const callbackUrl = route.fullPath
      signOut({
        callbackUrl: `/login?callbackUrl=${encodeURIComponent(callbackUrl)}`,
      })
    }

    const roles = await response.json();
    console.log('Roles fetched:', roles);

    isAdmin.value = roles.some(role => role.name === 'Admin');

  } catch (error) {
    console.error('Error fetching roles:', error);
  }

  findWebsiteStyle()
  findEmployee()
});


const handleSignOut = () => {
  const callbackUrl = route.fullPath
  signOut({
    callbackUrl: `/login?callbackUrl=${encodeURIComponent(callbackUrl)}`,
  })
}

const userItems = ref<NavigationMenuItem[][]>([
  [
    {
      label: 'Events',
      icon: 'i-lucide-book-open',
      to: '/events'
    }
  ]
])

const adminItems = computed<NavigationMenuItem[][]>(() => [
  [
    {
      label: t('misc.events'),
      icon: 'material-symbols:event',
      to: '/events'
    },
    {
      label: t('misc.storage'),
      icon: 'ic:round-warehouse',
      children: [
        {
          label: t('misc.viewStorage'),
          icon: 'ic:round-storage',
          to: '/items/storage'
        },
        {
          label: t('misc.viewItems'),
          icon: 'ic:round-storage',
          to: '/items'
        },
        {
          label: t('misc.viewCategories'),
          icon: 'ic:round-storage',
          to: '/items/categories'
        }
      ]
    },
    {
      label: t('misc.employees'),
      icon: 'clarity:employee-group-solid',
      children: [
        {
          label: t('misc.viewEmployees'),
          icon: 'clarity:employee-group-line',
          to: '/employees'
        },
        {
          label: t('misc.viewRoles'),
          icon: 'eos-icons:role-binding',
          to: '/roles'
        }
      ]
    },
    {
      label: t('misc.invoices'),
      icon: 'ph:invoice-fill',
      children: [
        {
          label: t('misc.invoices'),
          icon: 'fa6-solid:file-invoice-dollar',
          to: '/invoices'
        },
        {
          label: t('misc.contacts'),
          icon: 'material-symbols:contacts-rounded',
          to: '/contacts'
        }
      ]
    }
  ]
])
</script>