'use client'

import { useEffect, useState } from 'react'
import Header from '@/components/Landing/Header'
import HeroSection from '@/components/Landing/HeroSection'
import StatsSection  from "@/components/Landing/StatsSection"
import ArticlesGrid from '@/components/Landing/ArticlesGrid'
import SocialProof from '@/components/Landing/SocialProof'
import Footer from '@/components/Landing/Footer'
import { OverviewCards } from '@/components/Dashboard/OverviewCards'
import { EmailStatsChart } from '@/components/Dashboard/EmailStatsChart'
import axios from '@/services/api'

interface SubscriberMetrics {
    date: string
    newSubscribers: number
    totalSubscribers: number
    totalUnsubscribes: number
}

interface EmailDeliveryMetrics {
    date: string
    emailsSent: number
    failedDeliveries: number
}

interface DailyMetric {
    date: string
    newSubscribers: number
    emailsSent: number
    unsubs: number
}

interface OverviewData {
    totalSubscribers: number
    totalUnsubs: number
    emailsSentToday: number
}

export default function HomePage() {
    const [dailyData, setDailyData] = useState<DailyMetric[]>([])
    const [overview, setOverview] = useState<OverviewData>({
        totalSubscribers: 0,
        totalUnsubs: 0,
        emailsSentToday: 0,
    })

    useEffect(() => {
        const todayStr = new Date().toISOString().slice(0, 10)
        Promise.all([
            axios.get<SubscriberMetrics[]>('/api/metrics/subscribers/daily'),
            axios.get<EmailDeliveryMetrics[]>('/api/metrics/emails/daily'),
        ])
            .then(([subsRes, mailRes]) => {
                const subsMetrics = subsRes.data
                const mailMetrics = mailRes.data

                const combined: DailyMetric[] = subsMetrics.map(s => {
                    const mailForDay = mailMetrics.find(m => m.date === s.date)
                    return {
                        date: s.date,
                        newSubscribers: s.newSubscribers,
                        unsubs: s.totalUnsubscribes,
                        emailsSent: mailForDay?.emailsSent ?? 0,
                    }
                })
                setDailyData(combined)
                const todaySubs = subsMetrics.find(s => s.date === todayStr)
                const todayMail = mailMetrics.find(m => m.date === todayStr)

                setOverview({
                    totalSubscribers: todaySubs?.totalSubscribers ?? 0,
                    totalUnsubs: todaySubs?.totalUnsubscribes ?? 0,
                    emailsSentToday: todayMail?.emailsSent ?? 0,
                })
            })
            .catch(err => {
                console.error('Erro ao buscar métricas:', err)
            })
    }, [])

    return (
        <div className="min-h-screen bg-base-100 text-base-content">
            <Header />
            <main className="pt-20">
                {/* Hero ocupa largura total */}
                <section className="mb-12">
                    <HeroSection />
                </section>

                {/* Stats centralizado */}
                <section className="max-w-6xl mx-auto px-4 mb-16">
                    <StatsSection />
                </section>

                {/* Dashboard centralizado */}
                <section className="py-16 bg-base-200">
                    <div className="max-w-6xl mx-auto px-4 space-y-12">
                        <OverviewCards
                            totalSubscribers={overview.totalSubscribers}
                            totalUnsubs={overview.totalUnsubs}
                            emailsSentToday={overview.emailsSentToday}
                        />
                        <EmailStatsChart data={dailyData} />
                    </div>
                </section>

                {/* Notícias centralizado */}
                <section className="max-w-6xl mx-auto px-4 my-16">
                    <ArticlesGrid />
                </section>

                {/* Social Proof */}
                <section className="max-w-3xl mx-auto px-4 mb-20">
                    <SocialProof />
                </section>
            </main>
            <Footer />
        </div>


    )
}
