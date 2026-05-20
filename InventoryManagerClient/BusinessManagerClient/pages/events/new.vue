<template>
  <div class="flex flex-col">
    <header class="primary-color rounded-xl p-2 shadow-lg mb-10">
      <div class="container mx-auto flex flex-col items-center gap-2">
        <span v-if="titleError" class="text-red-500 text-sm font-medium6">{{ titleError }}</span>
        <UInput
            v-model="event.title"
            size="xl"
            class="font-bold"
            style="font-family: 'Arial'; font-size: 2rem;"
            :placeholder="t('events.eventTitle')"
        />

        <UInput
            v-model="event.date"
            type="date"
            size="sm"
            class="text-center text-xl w-1/5"
            style="font-family: 'Arial'; text-align: center;"
            :placeholder="t('events.eventDate')"
        />

        <span v-if="addressError" class="text-red-500 text-sm font-medium">{{ addressError }}</span>
        <UInput
            v-model="event.address"
            size="sm"
            class="text-center text-xl w-1/5"
            style="font-family: 'Arial';"
            :placeholder="t('events.eventAddress')"
        />
      </div>
    </header>

    <main class="flex flex-col gap-6">
      <section class="secondary-color shadow-lg rounded-xl p-8">
        <div class="flex flex-col gap-4">
          <div class="flex items-center" v-for="(label, key) in timeFields" :key="key">
            <label class="font-medium w-1/3 text-right pr-4">{{ t(label) }}</label>
            <UInput v-maska="'##:##'" class="w-1/3 p-2 rounded-lg" size="xl" placeholder="00:00" v-model="event[key]" />
          </div>
          <div class="flex">
            <div class="w-1/3 text-right pr-4 mt-3">
              <label class="font-medium">{{ t('events.comments') }}</label>
              <p style="font-size: 12px;">{{ event.comments.length }}/250</p>
            </div>
            <UTextarea
                class="w-1/3 rounded-lg p-2"
                :rows="5"
                size="xl"
                placeholder="..."
                v-model="event.comments"
                :maxlength="250"
            />
          </div>
        </div>
        <div class="flex justify-between mt-10">
          <UButton class="px-4 py-2 rounded-lg" :to="`/events`">Back</UButton>
          <UButton class="px-4 py-2 rounded-lg" @click="submitInfo">Submit</UButton>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: 'auth-admin',
});

import { ref } from 'vue';
import { useAuth } from '#imports';
import { vMaska } from 'maska/vue'

const { t } = useI18n()
const { public: { apiBase } } = useRuntimeConfig();

const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token;
const toast = useToast();
const router = useRouter();

const today = new Date().toISOString().split('T')[0];

const event = ref({
  title: '',
  date: today,
  address: '',
  meetupTime: '',
  arrivalTime: '',
  readyTime: '',
  soundCheckTime: '',
  guestTime: '',
  startTime: '',
  endTime: '',
  comments: '',
  staffList: [],
  id: ''
});

const titleError = ref('');
const addressError = ref('');
const errorMessages = ref<string[]>([]);

const timeFields: Record<string, string> = {
  meetupTime: 'events.atWarehouse',
  arrivalTime: 'events.atLocation',
  soundCheckTime: 'events.soundCheck',
  readyTime: 'events.beReadyBy',
  guestTime: 'events.guestsArrive',
  startTime: 'events.eventStarts',
  endTime: 'events.eventEnds',
};

const submitInfo = async () => {
  errorMessages.value = [];
  titleError.value = '';
  addressError.value = '';

  if (!event.value.title.trim()) {
    titleError.value = 'Title is required.';
  }
  if (!event.value.address.trim()) {
    addressError.value = 'Address is required.';
  }

  if (titleError.value || addressError.value) return;

  const requestBody = {
    address: event.value.address,
    date: event.value.date,
    arrivalTime: event.value.arrivalTime,
    meetupTime: event.value.meetupTime,
    guestTime: event.value.guestTime,
    endTime: event.value.endTime,
    readyTime: event.value.readyTime,
    soundCheckTime: event.value.soundCheckTime,
    startTime: event.value.startTime,
    comments: event.value.comments,
    title: event.value.title
  };

  try {
    const response = await fetch(
        `${apiBase}/admin/events`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${userToken}`,
          },
          body: JSON.stringify(requestBody),
        }
    );

    const responseText = await response.text();

    if (!response.ok) {
      let errorMessage;
      try {
        const errorResponse = JSON.parse(responseText);
        errorMessage = errorResponse.message || 'Failed to update event information!';
      } catch (e) {
        errorMessage = 'Failed to update event information!';
      }
      throw new Error(errorMessage);
    }

    toast.add({
      title: 'Success!',
      description: `A new event has successfully been added!`,
      icon: 'qlementine-icons:success-12'
    })
    router.push("/events")

  } catch (error) {
    console.error(error);
    errorMessages.value.push(error.message || 'An error occurred while updating event information.');
  }
};
</script>