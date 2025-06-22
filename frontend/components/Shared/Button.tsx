'use client'

import { ButtonHTMLAttributes, ReactNode } from 'react'
import classNames from 'classnames'

interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
    loading?: boolean
    children: ReactNode
}

export default function Button({ loading, children, className, ...props }: Props) {
    return (
        <button
            {...props}
            disabled={loading}
            className={classNames(
                // String única, sem quebras de linha problemáticas
                'flex items-center justify-center gap-2 px-6 py-3 font-semibold rounded-full ' +
                'bg-gradient-to-r from-primary-light to-primary-dark text-white shadow-lg ' +
                'hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed',
                className
            )}
        >
            {loading ? 'Carregando...' : children}
        </button>
    )
}
