import { useApp } from '../context/AppContext';
import { Info } from 'lucide-react';

export default function AboutPage() {
    const { platformSettings } = useApp();

    return (
        <div className="animate-fadeIn" style={{ maxWidth: 1000, margin: '0 auto' }}>
            <div className="page-header">
                <div>
                    <div className="page-title">About Us</div>
                    <div className="page-subtitle">Pioneering modern learning management for a digital age</div>
                </div>
            </div>

            <div className="card" style={{ padding: '40px 48px', marginTop: 24 }}>
                <div style={{ marginBottom: 48 }}>
                    <div style={{ fontSize: 22, fontWeight: 800, color: 'var(--text-primary)', marginBottom: 20, letterSpacing: '-0.5px' }}>Our Vision</div>
                    <div style={{ fontSize: 16, lineHeight: 1.8, color: 'var(--text-secondary)', whiteSpace: 'pre-wrap' }}>
                        {platformSettings.aboutText}
                    </div>
                </div>

                <div style={{ marginBottom: 40, paddingTop: 32, borderTop: '1px solid var(--border-light)' }}>
                    <div style={{ fontSize: 20, fontWeight: 800, color: 'var(--text-primary)', marginBottom: 24, letterSpacing: '-0.5px' }}>Developed By</div>
                    <div className="grid-3">
                        {[
                            { name: 'KANTAMANI NALIN KUMAR', id: '2400030332' },
                            { name: 'GHANTA PRASANTH BABU', id: '2400030196' },
                            { name: 'KARANAM BHARGAV', id: '2400031923' }
                        ].map(dev => (
                            <div key={dev.id} style={{ padding: '20px', background: 'var(--bg-card)', borderRadius: 12, border: '1px solid var(--border-light)', textAlign: 'center' }}>
                                <div style={{ fontWeight: 700, color: 'var(--text-primary)', marginBottom: 8, fontSize: 13 }}>{dev.name}</div>
                                <div style={{ fontSize: 12, color: 'var(--primary-light)', fontWeight: 800, fontFamily: 'monospace' }}>{dev.id}</div>
                            </div>
                        ))}
                    </div>
                </div>

                <div style={{ paddingTop: 32, borderTop: '1px solid var(--border-light)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <div>
                        <div style={{ fontSize: 18, fontWeight: 800, color: 'var(--text-primary)', marginBottom: 4 }}>Contact Support</div>
                        <div style={{ fontSize: 14, color: 'var(--primary-light)', fontWeight: 600 }}>{platformSettings.contactEmail}</div>
                    </div>
                    <div style={{ opacity: 0.1 }}>
                        <Info size={48} />
                    </div>
                </div>
            </div>
        </div>
    );
}
