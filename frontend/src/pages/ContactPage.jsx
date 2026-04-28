import { useApp } from '../context/AppContext';
import { Phone, Mail, MapPin } from 'lucide-react';

export default function ContactPage() {
    const { platformSettings } = useApp();

    return (
        <div className="animate-fadeIn" style={{ maxWidth: 800, margin: '0 auto' }}>
            <div className="page-header">
                <div>
                    <div className="page-title">Contact Us</div>
                    <div className="page-subtitle">We would love to hear from you</div>
                </div>
            </div>

            <div className="grid-3" style={{ marginTop: 24 }}>
                <div className="card" style={{ textAlign: 'center', padding: 32 }}>
                    <div style={{ width: 56, height: 56, margin: '0 auto 20px', borderRadius: '50%', background: 'rgba(108,99,255,0.12)', color: 'var(--primary-light)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Phone size={24} />
                    </div>
                    <div style={{ fontSize: 17, fontWeight: 700, color: 'var(--text-primary)', marginBottom: 8 }}>Call Us</div>
                    <div style={{ fontSize: 14, color: 'var(--text-secondary)' }}>{platformSettings.contactPhone}</div>
                </div>

                <div className="card" style={{ textAlign: 'center', padding: 32 }}>
                    <div style={{ width: 56, height: 56, margin: '0 auto 20px', borderRadius: '50%', background: 'rgba(108,99,255,0.12)', color: 'var(--primary-light)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Mail size={24} />
                    </div>
                    <div style={{ fontSize: 17, fontWeight: 700, color: 'var(--text-primary)', marginBottom: 8 }}>Email Us</div>
                    <div style={{ fontSize: 14, color: 'var(--text-secondary)' }}>
                        <a href={`mailto:${platformSettings.contactEmail}`} style={{ color: 'inherit', textDecoration: 'none' }}>
                            {platformSettings.contactEmail}
                        </a>
                    </div>
                </div>

                <div className="card" style={{ textAlign: 'center', padding: 32 }}>
                    <div style={{ width: 56, height: 56, margin: '0 auto 20px', borderRadius: '50%', background: 'rgba(108,99,255,0.12)', color: 'var(--primary-light)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <MapPin size={24} />
                    </div>
                    <div style={{ fontSize: 17, fontWeight: 700, color: 'var(--text-primary)', marginBottom: 8 }}>Headquarters</div>
                    <div style={{ fontSize: 14, color: 'var(--text-secondary)', lineHeight: 1.5 }}>
                        KLEF Vaddeswaram,<br />
                        Guntur, AP
                    </div>
                </div>
            </div>
        </div>
    );
}
