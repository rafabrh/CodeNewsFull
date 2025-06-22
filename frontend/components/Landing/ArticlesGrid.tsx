'use client'

import { useEffect, useState } from 'react'
import axios from '@/services/api'
import { motion } from 'framer-motion'

interface NewsItem {
    url: string
    imageUrl: string
    title: string
    publishDate: string
}

export default function ArticlesGrid() {
    const [news, setNews] = useState<NewsItem[]>([])

    useEffect(() => {
        axios.get<NewsItem[]>('/api/news/latest').then(r => setNews(r.data))
    }, [])

    return (
        <section id="noticias" className="py-16">
            <h2 className="text-3xl font-bold text-center mb-8">Últimas Notícias</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto px-4">
                {news.map((n, i) => (
                    <motion.a
                        key={i}
                        href={n.url}
                        target="_blank"
                        rel="noopener"
                        className="card bg-base-100 shadow-lg transform-gpu"
                        style={{ perspective: 1000 }}
                        whileHover={{ rotateY: 10, rotateX: 5, scale: 1.03 }}
                        transition={{ type: 'spring', stiffness: 200, damping: 12 }}
                    >
                        <figure>
                            <img src={n.imageUrl} alt={n.title} className="h-48 object-cover w-full" />
                        </figure>
                        <div className="card-body">
                            <h3 className="card-title line-clamp-2">{n.title}</h3>
                            <p className="text-sm text-gray-500">
                                {new Date(n.publishDate).toLocaleDateString()}
                            </p>

                            {/* Botões de reação */}
                            <div className="mt-4 flex gap-3">
                                {(['😀','🔥','👍'] as const).map(emoji => (
                                    <motion.button
                                        key={emoji}
                                        onClick={() => alert(`Você clicou em ${emoji}`)}
                                        whileTap={{ scale: 0.9 }}
                                        className="text-xl"
                                    >
                                        {emoji}
                                    </motion.button>
                                ))}
                            </div>
                        </div>
                    </motion.a>
                ))}
            </div>
        </section>
    )
}
