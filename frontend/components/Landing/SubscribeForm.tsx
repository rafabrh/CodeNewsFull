'use client'
import { useState, useEffect } from 'react'
import axios from '@/services/api'
import { toast } from 'react-toastify'
import { motion } from 'framer-motion'

export default function SubscribeForm() {
    const [email, setEmail] = useState('')
    const [loading, setLoading] = useState(false)
    const [feedback, setFeedback] = useState<{ type: 'success'|'warning'|'error', text: string }|null>(null)

    const isValidEmail = (e: string) => /^\S+@\S+\.\S+$/.test(e)
    useEffect(() => {
        if (feedback) {
            const t = setTimeout(() => setFeedback(null), 4000)
            return () => clearTimeout(t)
        }
    }, [feedback])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!isValidEmail(email)) {
            setFeedback({ type: 'warning', text: 'E-mail inválido.' })
            return
        }
        setLoading(true)
        try {
            await axios.post('/api/subscribe', { email })
            setFeedback({ type: 'success', text: 'Inscrito com sucesso!' })
            setEmail('')
        } catch (err: any) {
            setFeedback({ type: 'error', text: err?.response?.data || 'Erro ao inscrever.' })
        } finally {
            setLoading(false)
        }
    }

    return (
        <motion.form
            onSubmit={handleSubmit}
            className="
        relative
        w-full max-w-md
        mx-auto p-8
        bg-white/20 backdrop-blur-md
        rounded-2xl shadow-lg
      "
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5, duration: 0.6 }}
        >
            <label className="label">
        <span className="label-text text-lg font-semibold text-neutral-content">
          Inscreva-se agora
        </span>
            </label>

            <div className="input-group">
                <input
                    type="email"
                    placeholder="seu@exemplo.com"
                    className="input input-bordered flex-1 bg-white bg-opacity-80 placeholder-neutral"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    disabled={loading}
                />
                <button
                    type="submit"
                    className={`btn btn-primary btn-lg ${loading ? 'loading' : ''}`}
                    disabled={loading}
                >
                    {loading ? '' : 'Inscrever'}
                </button>
            </div>

            {feedback && (
                <div className={`alert mt-4 shadow-lg
          ${feedback.type === 'success' ? 'alert-success' : ''}
          ${feedback.type === 'warning' ? 'alert-warning' : ''}
          ${feedback.type === 'error'   ? 'alert-error'   : ''}
        `}>
                    <span>{feedback.text}</span>
                </div>
            )}
        </motion.form>
    )
}
