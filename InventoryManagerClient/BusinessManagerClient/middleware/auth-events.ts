import { useAuth } from '#imports'

export default defineNuxtRouteMiddleware(async (to) => {
    const paramId = to.params?.id
    const { data, signOut } = useAuth()
    const user = data.value?.user as any
    if (!user) return navigateTo('/login')

    const { public: { apiBase } } = useRuntimeConfig()

    const token = user.token
    const myEmail = String(user.email || '').toLowerCase()
    const myId = user?.id ?? user?.employeeId ?? null
    const eventId = String(paramId)

    try {
        const roles = await $fetch<any[]>(
            `${apiBase}/admin/employees/roles/find?email=${encodeURIComponent(user.email)}`,
            { headers: { Authorization: `Bearer ${token}` } }
        )
        const isAdmin = Array.isArray(roles) &&
            roles.some(r => String(r?.name).toUpperCase().includes('ADMIN'))
        if (isAdmin) return
    } catch {
    }

    try {
        const ev = await $fetch<any>(`${apiBase}/admin/events/${eventId}`, {
            headers: { Authorization: `Bearer ${token}` }
        })

        const list = Array.isArray(ev?.employeeList) ? ev.employeeList : []
        const isMember = list.some((emp: any) => {
            const empId = emp?.id ?? emp?.employeeId
            const empEmail = String(emp?.email || '').toLowerCase()
            const idMatch = myId != null && empId != null && String(empId) === String(myId)
            const emailMatch = !!myEmail && empEmail && empEmail === myEmail
            return idMatch || emailMatch
        })

        if (isMember) return
    } catch {
    }

    return navigateTo('/forbidden')
})
