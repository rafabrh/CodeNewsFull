import { motion } from 'framer-motion'
import { CheckCircleIcon } from '@heroicons/react/24/outline'

const features = [
    'Resumo diário das principais manchetes',
    'Interface leve e responsiva',
    'Estatísticas de envio em tempo real',
    'Cancelamento de inscrição fácil',
]

export default function FeaturesSection() {
    return (
        <section className="bg-gray-800 text-white py-16">
            <div className="max-w-6xl mx-auto px-4 text-center">
                <h2 className="font-heading text-3xl md:text-4xl mb-6">Por que CodeNews?</h2>
                <motion.ul
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ duration: 1 }}
                    className="grid grid-cols-1 md:grid-cols-2 gap-6"
                >
                    {features.map((feat, idx) => (
                        <li key={idx} className="flex items-start gap-3">
                            <CheckCircleIcon className="w-6 h-6 text-primary-light flex-shrink-0" />
                            <p className="text-lg">{feat}</p>
                        </li>
                    ))}
                </motion.ul>
            </div>
        </section>
    )
}
