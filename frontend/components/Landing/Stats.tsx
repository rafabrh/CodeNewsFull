// src/components/Landing/Stats.tsx
import React, { ReactNode } from 'react'

export interface StatItem {
    /** chave única para React */
    id: string
    /** título da métrica (ex: "Downloads") */
    title: string
    /** valor principal (ex: "31K") */
    value: string
    /** descrição adicional (ex: "Jan 1st - Feb 1st") */
    desc: string
    /** ícone SVG ou qualquer ReactNode */
    icon: ReactNode
}

interface StatsProps {
    items: StatItem[]
}

export function Stats({ items }: StatsProps) {
    return (
        <div className="stats shadow justify-center">
            {items.map(({ id, icon, title, value, desc }) => (
                <div key={id} className="stat">
                    <div className="stat-figure text-secondary">
                        {icon}
                    </div>
                    <div className="stat-title">{title}</div>
                    <div className="stat-value">{value}</div>
                    <div className="stat-desc">{desc}</div>
                </div>
            ))}
        </div>
    )
}
