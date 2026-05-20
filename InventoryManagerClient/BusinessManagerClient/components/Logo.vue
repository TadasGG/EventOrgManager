<template>
  <div>
    <img :src="apiBase + styleInfo.websiteLogo" alt="Logo">
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import { useAuth } from "#imports";

const { public: { apiBase } } = useRuntimeConfig();

const { data } = useAuth();
const userToken = (data?.value?.user as any)?.token;

const styleInfo = ref({
  id: "",
  websiteName: "",
  websiteLogo: "",
  primaryColor: "",
  secondaryColor: ""
});

const findWebsiteStyle = async () => {
  try {
    const response = await fetch(`${apiBase}/public/styles`);
    if (!response.ok) {
      throw new Error("Failed to fetch style!");
    }
    const styleData = await response.json();
    styleInfo.value = { ...styleData, id: styleData.id };
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  findWebsiteStyle();
});
</script>