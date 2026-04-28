import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { CheckCircle, XCircle, Loader2, BookOpen, Send } from 'lucide-react';

export default function VerifyEmailPage() {
    const [searchParams] = useSearchParams();
    const [otpCode, setOtpCode] = useState('');
    const [status, setStatus] = useState('idle'); // idle, loading, success, error
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    
    // Auto-verify if token is in URL (for backward compatibility if they click a link)
    useEffect(() => {
        const tokenFromUrl = new URLSearchParams(window.location.search).get('token');
        if (tokenFromUrl) {
            handleVerify(tokenFromUrl);
        }
    }, []);

    const handleVerify = async (codeToVerify) => {
        const code = codeToVerify || otpCode;
        if (!code || code.length < 4) {
            setMessage('Please enter a valid verification code.');
            setStatus('error');
            return;
        }

        setStatus('loading');
        try {
            const API_BASE = import.meta.env.VITE_API_URL || 'http://127.0.0.1:5000/api';
            const emailFromUrl = searchParams.get('email');
            const res = await fetch(`${API_BASE}/auth/verify?token=${code}${emailFromUrl ? '&email=' + encodeURIComponent(emailFromUrl) : ''}`);
            const data = await res.json();

            if (res.ok) {
                setStatus('success');
                setMessage(data.message || 'Account verified successfully!');
            } else {
                setStatus('error');
                setMessage(data.error || 'Verification failed. Please check the code.');
            }
        } catch (err) {
            setStatus('error');
            setMessage('An error occurred. Please check your connection.');
        }
    };

    return (
        <div className="login-page">
            <div className="login-bg-orb" style={{ width: 500, height: 500, background: 'rgba(108,99,255,0.1)', top: -150, left: -150 }} />
            
            <div className="login-card" style={{ maxWidth: 400, textAlign: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 12, justifyContent: 'center', marginBottom: 32 }}>
                    <div className="sidebar-logo-icon animate-glow"><BookOpen size={20} color="#fff" /></div>
                    <div>
                        <div style={{ fontFamily: 'Outfit', fontSize: 20, fontWeight: 800, color: 'var(--text-primary)', lineHeight: 1 }}>Digital Black Board</div>
                    </div>
                </div>

                {status === 'idle' || status === 'error' ? (
                    <div className="animate-fadeIn">
                        <h2 style={{ fontSize: 22, fontWeight: 700, marginBottom: 8 }}>Verify Your Account</h2>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>Enter the 4-digit code sent to your email.</p>
                        
                        <div style={{ position: 'relative', marginBottom: 16 }}>
                            <input 
                                type="text"
                                className="login-input"
                                placeholder="Enter 4-digit OTP"
                                value={otpCode}
                                onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, '').slice(0, 4))}
                                style={{ textAlign: 'center', fontSize: 24, letterSpacing: 8, height: 60 }}
                            />
                        </div>

                        {message && status === 'error' && (
                            <p style={{ color: '#f87171', fontSize: 14, marginBottom: 16 }}>{message}</p>
                        )}

                        <button 
                            onClick={() => handleVerify()} 
                            className="btn btn-primary" 
                            style={{ width: '100%', justifyContent: 'center', height: 48 }}
                            disabled={otpCode.length < 4}
                        >
                            <Send size={18} style={{ marginRight: 8 }} />
                            Verify Account
                        </button>

                        <button 
                            onClick={() => navigate('/auth')} 
                            className="btn btn-secondary" 
                            style={{ width: '100%', justifyContent: 'center', marginTop: 12 }}
                        >
                            Back to Sign Up
                        </button>
                    </div>
                ) : null}

                {status === 'loading' && (
                    <div className="animate-fadeIn">
                        <div style={{ margin: '0 auto 20px', color: 'var(--primary)' }}>
                            <Loader2 size={48} className="animate-spin" />
                        </div>
                        <h2 style={{ fontSize: 22, fontWeight: 700, marginBottom: 8 }}>Verifying...</h2>
                        <p style={{ color: 'var(--text-secondary)' }}>Please wait while we confirm your code.</p>
                    </div>
                )}

                {status === 'success' && (
                    <div className="animate-fadeIn">
                        <div style={{ width: 72, height: 72, background: 'rgba(16,185,129,0.15)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }} className="animate-glow">
                            <CheckCircle size={36} style={{ color: '#34d399' }} />
                        </div>
                        <h2 style={{ fontSize: 22, fontWeight: 700, marginBottom: 8 }}>Verification Successful!</h2>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>{message}</p>
                        <button onClick={() => navigate('/auth')} className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }}>
                            Go to Login
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
