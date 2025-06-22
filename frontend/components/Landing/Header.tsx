'use client'
import Link from 'next/link'
import { motion } from 'framer-motion'

export default function Header() {
    return (
        <motion.header
            initial={{ y: -80 }}
            animate={{ y: 0 }}
            transition={{ duration: 0.6 }}
            className="
        navbar
        fixed top-0 left-0 w-full z-50
        bg-base-100/70 backdrop-blur-md
        border-b border-base-300
      "
        >
            <div className="container mx-auto flex items-center justify-between px-4">
                {/* Logo */}
                <Link href="/" className="text-2xl font-heading font-bold text-primary">
                    CodeNews
                </Link>

                {/* Links de navegação */}
                <nav className="hidden md:flex space-x-8 text-base-content">
                    <Link href="#hero" className="hover:text-primary transition">
                        Início
                    </Link>
                    <Link href="#stats" className="hover:text-primary transition">
                        Estatísticas
                    </Link>
                    <Link href="#noticias" className="hover:text-primary transition">
                        Notícias
                    </Link>
                    <Link href="#depoimentos" className="hover:text-primary transition">
                        Depoimentos
                    </Link>
                </nav>

                {/* Botão de ação */}
                <Link
                    href="#subscribe"
                    className="btn btn-primary btn-sm lg:btn-md"
                >
                    Assinar
                </Link>
            </div>
        </motion.header>
    )
}
