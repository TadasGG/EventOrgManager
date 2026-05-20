// middleware/auth-admin.ts
import { useAuth } from '#imports'

export default defineNuxtRouteMiddleware(async (to) => {
    const { data, signOut } = useAuth()
    const { public: { apiBase } } = useRuntimeConfig()
    const user = data.value?.user as any
    const callbackUrl = to.fullPath

    if (!user) {
        return navigateTo({
            path: '/login',
            query: { callbackUrl }
        })
    }

    const userToken = user.token

    try {
        const response = await fetch(`${apiBase}/admin/employees/me/roles`, {
            method: 'GET',
            headers: { Authorization: `Bearer ${userToken}` }
        })

        if (!response.ok) {
            await signOut({
                redirect: true,
                callbackUrl: `/login?callbackUrl=${encodeURIComponent(callbackUrl)}`
            })
            return
        }

        const roles = await response.json()
        const hasAdminRole = roles.some((role: any) => role.name === 'Admin')

        if (!hasAdminRole) {
            return navigateTo('/forbidden')
        }
    } catch (error) {
        console.error('Error fetching roles:', error)
        await signOut({
            redirect: true,
            callbackUrl: `/login?callbackUrl=${encodeURIComponent(callbackUrl)}`
        })
    }
})