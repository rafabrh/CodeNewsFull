export default function Footer() {
    return (
        <footer className="bg-base-300 py-6 mt-16">
            <div className="max-w-6xl mx-auto px-4 text-center text-sm">
                © {new Date().getFullYear()} CodeNews. Todos os direitos reservados.
            </div>
        </footer>
    )
}
