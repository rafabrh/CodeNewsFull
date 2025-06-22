import { motion } from 'framer-motion'

interface OverviewCardsProps {
    totalSubscribers: number
    totalUnsubs: number
    emailsSentToday: number
}

export function OverviewCards({ totalSubscribers, totalUnsubs, emailsSentToday }: OverviewCardsProps) {
    const cards = [
        { title: 'Total Subscribers', value: totalSubscribers },
        { title: 'Total Unsubs', value: totalUnsubs },
        { title: 'Emails Sent Today', value: emailsSentToday },
    ]

    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {cards.map((card, index) => (
                <motion.div
                    key={index}
                    whileHover={{ scale: 1.05 }}
                    className="bg-gray-800 p-6 rounded shadow"
                >
                    <h3 className="text-lg font-semibold">{card.title}</h3>
                    <p className="text-2xl font-bold mt-2">{card.value}</p>
                </motion.div>
            ))}
        </div>
    )
}
