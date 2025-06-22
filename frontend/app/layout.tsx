// app/layout.tsx
import './globals.css'
import { ReactNode } from 'react'

export default function RootLayout({ children }: { children: ReactNode }) {
    return (
        <html lang="pt-BR" data-theme="corporate">
        <head>
            <link
                href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap"
                rel="stylesheet"
            />
        </head>
        <body className="antialiased bg-base-100 text-base-content min-h-screen">
        {children}
        </body>
        </html>
    )
}
