'use client'
import { useEffect, useState } from 'react'
import axios from '@/services/api'

interface SubscriberDaily {
    date: string
    newSubscribers: number
    totalSubscribers: number
    totalUnsubscribes: number
}
interface EmailDaily {
    date: string
    emailsSent: number
    failedDeliveries: number
}
interface ArticlesTotalResponse {
    totalArticles: number
}
interface Stats {
    totalArticles: number
    newSubscribersToday: number
    cancellationsToday: number
}

export default function StatsSection() {
    const [stats, setStats] = useState<Stats | null>(null)

    useEffect(() => {
        async function loadStats() {
            try {
                const [subsRes, mailRes, artRes] = await Promise.all([
                    axios.get<SubscriberDaily[]>('/api/metrics/subscribers/daily'),
                    axios.get<EmailDaily[]>('/api/metrics/emails/daily'),
                    axios.get<ArticlesTotalResponse>('/api/news/articles/total'),
                ])
                const subs = subsRes.data.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
                const last = subs[subs.length - 1]
                setStats({
                    totalArticles: artRes.data.totalArticles,
                    newSubscribersToday: last.newSubscribers,
                    cancellationsToday: last.totalUnsubscribes,
                })
            } catch (err) {
                console.error('Erro ao carregar stats:', err)
            }
        }
        loadStats()
    }, [])

    if (!stats) {
        return (
            <section className="max-w-6xl mx-auto px-4 text-center py-8">
                Carregando estatísticas…
            </section>
        )
    }

    return (
        <section id="stats" className="max-w-6xl mx-auto px-4 py-16">
            <div className="stats shadow-xl bg-base-100 w-full">
                <div className="stat">
                    <div className="stat-figure text-primary">
                        <i className="ri-newspaper-line text-4xl"></i>
                    </div>
                    <div className="stat-title">Artigos Publicados</div>
                    <div className="stat-value">{stats.totalArticles.toLocaleString()}</div>
                </div>
                <div className="stat">
                    <div className="stat-figure text-secondary">
                        <i className="ri-user-add-line text-4xl"></i>
                    </div>
                    <div className="stat-title">Novos Inscritos (hoje)</div>
                    <div className="stat-value">{stats.newSubscribersToday}</div>
                </div>
                <div className="stat">
                    <div className="stat-figure text-accent">
                        <i className="ri-user-received-line text-4xl"></i>
                    </div>
                    <div className="stat-title">Cancelamentos (hoje)</div>
                    <div className="stat-value">{stats.cancellationsToday}</div>
                </div>
            </div>
        </section>
    )
}
