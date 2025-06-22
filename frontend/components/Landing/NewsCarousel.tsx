'use client'
import { useEffect, useState } from 'react'
import axios from '@/services/api'
import { toast } from 'react-toastify'

export default function NewsCarousel() {
    const [news, setNews] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        axios.get('/api/news/latest')
            .then(r => setNews(r.data))
            .catch(() => toast.error('Não foi possível carregar notícias.'))
            .finally(() => setLoading(false))
    }, [])

    if (loading)
        return <div className="text-gray-400 mt-8">Carregando notícias...</div>
    if (!news.length)
        return <div className="text-gray-400 mt-8">Nenhuma notícia disponível.</div>

    return (
        <section className="max-w-6xl mx-auto px-4 py-8">
            <div className="flex overflow-x-auto scroll-pl-4 snap-x snap-mandatory gap-4 p-4">
                {news.map((n, i) => (
                    <a
                        key={i}
                        href={n.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="snap-center shrink-0 w-64 bg-base-100 rounded-2xl overflow-hidden shadow-lg
                        hover:scale-105 transition"
                    >
                        <img
                            src={n.imageUrl}
                            alt={n.title}
                            className="w-full h-40 object-cover"
                        />
                        <div className="p-4">
                            <h3 className="font-semibold text-gray-900 line-clamp-2">{n.title}</h3>
                        </div>
                    </a>
                ))}
            </div>
        </section>
    )
}
