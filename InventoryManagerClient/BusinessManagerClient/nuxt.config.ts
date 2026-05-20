import {process} from "std-env";

export default defineNuxtConfig({
    compatibilityDate: '2025-07-15',
    devtools: { enabled: true },
    modules: [
        '@nuxt/image',
        '@nuxt/ui',
        '@sidebase/nuxt-auth',
        'nuxt-color-picker',
        '@nuxt/icon',
        '@nuxtjs/i18n'
    ],
    auth: {
        globalAppMiddleware: true
    },
    css: [
        '~/assets/css/main.css',
        '@/assets/css/text.css',
        '@/assets/css/objects.css'
    ],
    colorMode: {
        preference: 'dark',
        fallback: 'light',
    },
    runtimeConfig: {
        public: {
          apiBase: process.env.NUXT_PUBLIC_API_BASE,
        }
    },
    i18n: {
        langDir: 'locales',
        locales: [
            { code: 'en', iso: 'en-US', file: 'en.json', name: 'English' },
            { code: 'lt', iso: 'lt-LT', file: 'lt.json', name: 'Lietuvių' }
        ],
        defaultLocale: 'en',
        strategy: 'no_prefix',
        detectBrowserLanguage: {
            useCookie: true,
            cookieKey: 'i18n_redirected',
            redirectOn: 'root',
            alwaysRedirect: true
        },
        experimental: { localeDetector: 'request' }
    }
})