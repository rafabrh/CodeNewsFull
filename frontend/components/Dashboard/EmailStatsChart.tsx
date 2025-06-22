'use client'

import dynamic from 'next/dynamic'
import { useEffect, useState } from 'react'

interface DailyMetric {
    date: string
    newSubscribers: number
    emailsSent: number
    unsubs: number
}

const ReactApexChart = dynamic(() => import('react-apexcharts'), { ssr: false })

interface EmailStatsChartProps {
    data: DailyMetric[]
}

export function EmailStatsChart({ data }: EmailStatsChartProps) {
    const [series, setSeries] = useState<any[]>([])
    const [categories, setCategories] = useState<string[]>([])

    useEffect(() => {
        setCategories(data.map((item) => item.date))
        setSeries([
            {
                name: 'New Subscribers',
                data: data.map((item) => item.newSubscribers),
            },
            {
                name: 'Emails Sent',
                data: data.map((item) => item.emailsSent),
            },
            {
                name: 'Unsubs',
                data: data.map((item) => item.unsubs),
            },
        ])
    }, [data])

    const options = {
        chart: {
            type: 'bar',
            height: 350,
            toolbar: { show: false },
            background: 'transparent',
        },
        theme: {
            mode: 'dark',
        },
        xaxis: {
            categories,
        },
        colors: ['#10B981', '#3B82F6', '#EF4444'],
        legend: {
            labels: { colors: '#fff' },
        },
    }

    return (
        <div className="bg-gray-800 p-6 rounded shadow">
            <h3 className="text-lg font-semibold mb-4">Daily Email Metrics</h3>
            <ReactApexChart options={options} series={series} type="bar" height={350} />
        </div>
    )
}
