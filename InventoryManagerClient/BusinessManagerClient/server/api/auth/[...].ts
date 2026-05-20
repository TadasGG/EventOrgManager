import { NuxtAuthHandler } from '#auth';
import CredentialsProvider from 'next-auth/providers/credentials';
import {process} from "std-env";

const { public: { apiBase } } = useRuntimeConfig();

export default NuxtAuthHandler({
    secret: process.env.AUTH_SECRET,
    pages: {
        signIn: '/login',
        signOut: '/login'
    },
    providers: [
        // @ts-expect-error
        CredentialsProvider.default({
            name: 'credentials',
            credentials: {},
            async authorize(credentials: { email: string, password: string }) {
                try {
                    const res = await fetch(`${apiBase}/api/auth/login`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            email: credentials.email,
                            password: credentials.password
                        })
                    });

                    const text = await res.text();
                    let data = null;

                    try {
                        data = text ? JSON.parse(text) : null;
                    } catch (err) {
                        console.error('Failed to parse login response as JSON:', err);
                    }

                    if (res.ok && data?.token) {
                        return {
                            token: data.token,
                            email: credentials.email,
                        };
                    } else {
                        console.warn('Login failed:', data);
                        return null;
                    }

                } catch (error) {
                    console.error('Error during authentication:', error);
                    return null;
                }
            }
        })
    ],

    session: {
        strategy: "jwt",
        maxAge: 60 * 60 * 24,
    },

    callbacks: {
        async jwt({ token, user }) {
            if (user) {
                token = {
                    ...token,
                    ...user,
                };
            }
            return token;
        },

        async session({ session, token }) {
            session.user = {
                ...token,
                ...session.user,
            };

            return session;
        }
    }
});
