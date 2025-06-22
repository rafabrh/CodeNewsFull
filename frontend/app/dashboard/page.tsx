'use client'

import  HeroSection  from '@/components/Landing/HeroSection'
import  SubscribeForm  from '@/components/Landing/SubscribeForm'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

export default function Page() {
    return (
        <div className="relative w-full h-screen overflow-hidden">
            {/* Vídeo de Fundo */}
            <video
                src="/videos/background.mp4"
                autoPlay
                muted
                loop
                className="absolute inset-0 w-full h-full object-cover"
            />

            {/* Overlay escuro */}
            <div className="absolute inset-0 bg-black/60" />

            {/* Conteúdo acima do vídeo */}
            <main className="relative z-10 flex flex-col items-center justify-center h-full px-4 text-white">
                <HeroSection />
                <SubscribeForm />
                <ToastContainer
                    position="top-right"
                    autoClose={3000}
                    hideProgressBar={false}
                    newestOnTop
                    closeOnClick
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />
            </main>
        </div>
    )
}
