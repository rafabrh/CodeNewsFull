'use client'

import { motion } from 'framer-motion'
import SubscribeForm from './SubscribeForm'

export default function HeroSection() {
    return (
        <section
            id="hero"
            className="relative h-screen w-full overflow-hidden font-sans"
        >
            {/* Vídeo de fundo, cobrindo toda a seção */}
            <video
                src="/videos/codenewscity.mp4"
                autoPlay
                muted
                loop
                playsInline
                className="absolute inset-0 w-full h-full object-cover"
            />

            {/* Overlay escuro para contraste */}
            <div className="absolute inset-0 bg-black/60" />

            {/* Conteúdo centralizado */}
            <div className="relative z-10 flex flex-col items-center justify-center h-full px-4 text-center text-white">
                <motion.h1
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.8 }}
                    className="text-4xl sm:text-5xl md:text-6xl font-bold leading-tight"
                >
                    Bem-vindo ao <span className="text-primary">CodeNews</span>
                </motion.h1>
                <motion.p
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.4 }}
                    className="mt-4 max-w-xl text-lg sm:text-xl opacity-90"
                >
                    Receba diariamente as 10 notícias mais quentes de tecnologia
                    diretamente no seu e-mail — sem spam, só conteúdo de qualidade.
                </motion.p>

                {/* Container destacado para o SubscribeForm */}
                <motion.div
                    initial={{ scale: 0.9, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    transition={{ delay: 0.6, type: 'spring', stiffness: 200 }}
                    className="mt-8 w-full max-w-md bg-white/20 backdrop-blur-md rounded-xl p-6"
                >
                    <SubscribeForm />
                </motion.div>
            </div>
        </section>
    )
}
