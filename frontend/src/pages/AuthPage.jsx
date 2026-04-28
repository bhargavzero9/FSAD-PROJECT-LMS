import { useState } from 'react';
import { useApp } from '../context/AppContext';
import { ROLES } from '../utils/constants';
import {
    BookOpen, Eye, EyeOff, Zap, UserPlus, User, Shield, Star, Palette,
    CheckCircle, ArrowLeft, Sun, Moon,
} from 'lucide-react';

/* ── Password strength ─────────────────────────────────────────────────────── */
function PwStr({ pw }) {
    if (!pw) return null;
    const chks = [
        { l: '8+ chars', ok: pw.length >= 8 },
        { l: 'Uppercase', ok: /[A-Z]/.test(pw) },
        { l: 'Number', ok: /\d/.test(pw) },
    ];
    const p = chks.filter(c => c.ok).length;
    const clr = p <= 1 ? '#ef4444' : p === 2 ? '#f59e0b' : '#10b981';
    return (
        <div style={{ marginTop: 6, display: 'flex', gap: 3, alignItems: 'center' }}>
            <div style={{ height: 4, flex: 1, background: '#e2e8f0', borderRadius: 2, overflow: 'hidden' }}>
                <div style={{ height: '100%', width: `${(p / 3) * 100}%`, background: clr, transition: '0.3s' }} />
            </div>
            <span style={{ fontSize: 10, fontWeight: 700, color: clr, minWidth: 40 }}>{p === 3 ? 'Strong' : 'Weak'}</span>
        </div>
    );
}

const ROLE_INFO = [
    { role: ROLES.STUDENT, icon: <User size={16} />, desc: 'Enroll & track progress', color: '#10b981' },
    { role: ROLES.INSTRUCTOR, icon: <Star size={16} />, desc: 'Create course content', color: '#6366f1' },
    { role: ROLES.CONTENT_CREATOR, icon: <Palette size={16} />, desc: 'Design learning materials', color: '#f59e0b' },
    { role: ROLES.ADMIN, icon: <Shield size={16} />, desc: 'Full platform management', color: '#ef4444' },
];

const CSS = `
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700;800&display=swap');

.AUTH_root {
  min-height: 100vh;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  background: #f8fafc;
  background-image: 
    radial-gradient(at 0% 0%, rgba(59, 130, 246, 0.05) 0, transparent 50%), 
    radial-gradient(at 100% 0%, rgba(99, 102, 241, 0.05) 0, transparent 50%);
  padding: 20px;
  font-family: 'Poppins', sans-serif;
  color: #1e293b;
}

.AUTH_box {
  position: relative;
  width: 100%; max-width: 900px;
  min-height: 560px;
  background: #ffffff;
  border: 1px solid #cbd5e1;
  box-shadow: 0 10px 35px -5px rgba(0, 0, 0, 0.12);
  border-radius: 20px;
  display: flex;
  overflow: hidden;
}

.AUTH_panel {
  flex: 1;
  padding: 48px;
  display: flex; flex-direction: column; justify-content: center;
}

.AUTH_roles { 
  display: grid; 
  grid-template-columns: repeat(2, 1fr); 
  gap: 16px; 
  margin-top: 24px; 
}

.AUTH_role {
  padding: 16px; 
  border-radius: 12px; 
  cursor: pointer;
  text-align: left; 
  border: 1.5px solid #e2e8f0;
  background: #fcfdfe;
  transition: all 0.2s;
  position: relative;
}

.AUTH_role:hover { transform: translateY(-2px); border-color: #cbd5e1; box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
.AUTH_role.on { background: #f0f7ff; border-color: #3b82f6; border-width: 2px; }

.AUTH_role_icon {
  width: 36px; height: 36px; border-radius: 8px; 
  display: flex; align-items: center; justify-content: center; 
  margin-bottom: 10px;
}

.AUTH_role_name { font-weight: 700; color: #0f172a; font-size: 14px; }
.AUTH_role_desc { font-size: 11px; color: #64748b; line-height: 1.4; }

.AUTH_field { margin-top: 20px; }
.AUTH_field label {
  display: block; font-size: 11px; font-weight: 700; 
  color: #64748b; margin-bottom: 6px;
  text-transform: uppercase; letter-spacing: 0.5px;
}

.AUTH_field input {
  width: 100%;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
  font-size: 14px; color: #1e293b;
  padding: 10px 14px;
  transition: 0.2s;
}

.AUTH_field input:focus { border-color: #3b82f6; background: #fff; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1); }

.AUTH_btn {
  width: 100%; height: 48px;
  margin-top: 28px;
  background: #2563eb;
  border: none; border-radius: 8px;
  color: #fff; font-size: 14px; font-weight: 700;
  cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: 0.2s;
}

.AUTH_btn:hover { background: #1d4ed8; transform: translateY(-1px); }
.AUTH_btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }

.AUTH_switch { margin-top: 24px; font-size: 13px; color: #64748b; text-align: center; }
.AUTH_switch button { background: none; border: none; cursor: pointer; color: #2563eb; font-weight: 700; margin-left: 4px; }

.AUTH_welcome {
  width: 35%;
  background: #f1f5f9;
  display: flex; flex-direction: column; justify-content: center;
  padding: 48px; border-left: 1px solid #e2e8f0;
}

.AUTH_welcome h2 { font-size: 32px; font-weight: 800; color: #0f172a; line-height: 1.1; letter-spacing: -1px; }
.AUTH_welcome p { color: #64748b; font-size: 14px; margin-top: 16px; line-height: 1.6; }

.AUTH_brand { display: flex; align-items: center; gap: 12px; margin-bottom: 32px; }
.AUTH_brand_icon { background: #2563eb; width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.AUTH_brand_name { color: #0f172a; font-weight: 800; font-size: 20px; letter-spacing: -0.5px; }

.sl { animation: fadeIn 0.4s ease backwards; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.AUTH_root.dark {
  background: #020617;
  background-image: 
    radial-gradient(at 0% 0%, rgba(30, 64, 175, 0.2) 0, transparent 50%), 
    radial-gradient(at 100% 0%, rgba(139, 92, 246, 0.2) 0, transparent 50%);
  color: #f8fafc;
}

.AUTH_box.dark {
  background: #0f172a;
  border-color: rgba(255, 255, 255, 0.18);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.6);
}

.AUTH_box.dark .AUTH_panel { background: #0f172a; }

.AUTH_box.dark .AUTH_role {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.15);
}
.AUTH_box.dark .AUTH_role:hover { background: rgba(255, 255, 255, 0.05); border-color: rgba(255, 255, 255, 0.2); }
.AUTH_box.dark .AUTH_role.on { background: rgba(59, 130, 246, 0.1); border-color: #3b82f6; }

.AUTH_box.dark .AUTH_field input {
  background: rgba(255, 255, 255, 0.03);
  border-color: rgba(255, 255, 255, 0.2);
  color: #f8fafc;
}
.AUTH_box.dark .AUTH_field input:focus { border-color: #3b82f6; background: rgba(255, 255, 255, 0.05); }

.AUTH_box.dark .AUTH_welcome {
  background: #1e293b;
  border-left-color: rgba(255, 255, 255, 0.05);
}

.AUTH_theme_toggle {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 100;
  transition: 0.2s;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
}

.AUTH_root.dark .AUTH_theme_toggle {
  background: #1e293b;
  border-color: rgba(255, 255, 255, 0.1);
  color: #f8fafc;
}

.AUTH_theme_toggle:hover { transform: scale(1.1); }
.AUTH_theme_toggle:active { transform: scale(0.95); }

.AUTH_spin {
  width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff; border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
`;

function Brand({ dark }) {
    return (
        <div className="AUTH_brand sl">
            <div className="AUTH_brand_icon"><BookOpen size={18} color="#fff" /></div>
            <div className="AUTH_brand_name" style={{ color: dark ? '#f8fafc' : '#0f172a' }}>Digital Black Board</div>
        </div>
    );
}

function Field({ label, type, placeholder, value, onChange, showToggle, show, onToggle, extra }) {
    return (
        <div className="AUTH_field sl">
            <label>{label}</label>
            <div style={{ position: 'relative' }}>
                <input
                    type={show != null ? (show ? 'text' : type) : type}
                    placeholder={placeholder}
                    value={value}
                    onChange={onChange}
                />
                {showToggle && (
                    <button type="button" onClick={onToggle} style={{ position: 'absolute', right: 12, top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: '#94a3b8' }}>
                        {show ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                )}
            </div>
            {extra}
        </div>
    );
}

export default function AuthPage({ onLogin, onSignup }) {
    const { login, register, setCurrentUser } = useApp();
    const [isSignup, setIsSignup] = useState(false);
    const [darkMode, setDarkMode] = useState(false);
    const [step, setStep] = useState(1);
    const [selRole, setSelRole] = useState('');
    const [lEmail, setLEmail] = useState('');
    const [lPass, setLPass] = useState('');
    const [sName, setSName] = useState('');
    const [sEmail, setSEmail] = useState('');
    const [sPass, setSPass] = useState('');
    const [sConf, setSConf] = useState('');
    const [sCode, setSCode] = useState('');
    const [showLP, setShowLP] = useState(false);
    const [showSP, setShowSP] = useState(false);
    const [lLoad, setLLoad] = useState(false);
    const [sLoad, setSLoad] = useState(false);
    const [err, setErr] = useState('');
    const [success, setSuccess] = useState(false);
    const [otp, setOtp] = useState('');

    const doLogin = async (e) => {
        e.preventDefault(); setErr('');
        if (!lEmail || !lPass) { setErr('Please enter email and password'); return; }
        setLLoad(true);
        const res = await login(lEmail, lPass);
        setLLoad(false);
        if (res.error) setErr(res.error); else onLogin();
    };

    const doSignup = async (e) => {
        e.preventDefault(); setErr('');
        if (!sName || !sEmail || !sPass) { setErr('Please fill in all details'); return; }
        if (sPass.length < 8) { setErr('Password must be at least 8 characters'); return; }
        if (!/[A-Z]/.test(sPass)) { setErr('Password needs an uppercase letter'); return; }
        if (!/\d/.test(sPass)) { setErr('Password needs a number'); return; }
        if (sPass !== sConf) { setErr('Passwords do not match'); return; }
        if (selRole !== ROLES.STUDENT && sCode !== 'DBBLMS') { setErr('Invalid Staff Code'); return; }
        setSLoad(true);
        const res = await register({ name: sName, email: sEmail, password: sPass, role: selRole });
        setSLoad(false);
        if (res.error) setErr(res.error); else setSuccess(true);
    };

    if (success) return (
        <div className={`AUTH_root${darkMode ? ' dark' : ''}`}>
            <style>{CSS}</style>
            <div className={`AUTH_box${darkMode ? ' dark' : ''}`} style={{ flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: 60, textAlign: 'center' }}>
                <div style={{ width: 64, height: 64, background: darkMode ? 'rgba(16,185,129,0.1)' : '#f0fdf4', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 24 }}>
                    <CheckCircle size={32} color="#10b981" />
                </div>
                <h2 style={{ fontSize: 28, fontWeight: 800, color: darkMode ? '#f8fafc' : '#0f172a' }}>Check Your Email</h2>
                <p style={{ color: '#64748b', maxWidth: 320, marginTop: 8 }}>We've sent a 4-digit verification code to you. Please enter it below to activate your account.</p>
                
                <input 
                    type="text" 
                    placeholder="0000" 
                    value={otp} 
                    onChange={e => setOtp(e.target.value.replace(/\D/g, '').slice(0,4))}
                    style={{ 
                        marginTop: 32, fontSize: 36, textAlign: 'center', width: 140, letterSpacing: 8, 
                        border: `2px solid ${darkMode ? 'rgba(255,255,255,0.1)' : '#e2e8f0'}`, 
                        borderRadius: 12, padding: 12, outline: 'none', 
                        color: darkMode ? '#f8fafc' : '#1e293b',
                        background: darkMode ? 'rgba(255,255,255,0.03)' : '#f8fafc'
                    }}
                />
                
                <button 
                    className="AUTH_btn" 
                    style={{ maxWidth: 240 }} 
                    disabled={otp.length < 4 || sLoad}
                    onClick={async () => {
                        setSLoad(true);
                        try {
                            const API_BASE = import.meta.env.VITE_API_URL || 'http://127.0.0.1:5000/api';
                            const res = await fetch(`${API_BASE}/auth/verify?token=${otp}&email=${encodeURIComponent(sEmail)}`);
                            const data = await res.json();
                            if (res.ok) {
                                if (data.user) setCurrentUser(data.user);
                                onSignup();
                            } else setErr(data.error || 'Invalid code');
                        } catch { setErr('Verification failed. Try again.'); }
                        setSLoad(false);
                    }}
                >
                    {sLoad ? <span className="AUTH_spin" /> : 'Activate & Enter'}
                </button>
                {err && <p style={{ color: '#ef4444', marginTop: 16, fontSize: 13, fontWeight: 500 }}>{err}</p>}
            </div>
        </div>
    );

    return (
        <div className={`AUTH_root${darkMode ? ' dark' : ''}`}>
            <style>{CSS}</style>
            <button className="AUTH_theme_toggle" onClick={() => setDarkMode(!darkMode)} title={`Switch to ${darkMode ? 'Light' : 'Dark'} Mode`}>
                {darkMode ? <Sun size={20} /> : <Moon size={20} />}
            </button>

            <div className={`AUTH_box${darkMode ? ' dark' : ''}`}>
                <div className="AUTH_panel">
                    {!isSignup ? (
                        <div className="sl">
                            <Brand dark={darkMode} />
                            <h2 style={{ fontSize: 32, color: darkMode ? '#f8fafc' : '#0f172a', fontWeight: 800 }}>Welcome Back 👋</h2>
                            <p style={{ color: '#64748b', fontSize: 14, marginBottom: 8 }}>Log in to access your classroom.</p>
                            <form onSubmit={doLogin}>
                                <Field label="Email Address" type="email" placeholder="you@domain.com" value={lEmail} onChange={e => setLEmail(e.target.value)} />
                                <Field label="Password" type="password" placeholder="••••••••" value={lPass} onChange={e => setLPass(e.target.value)} showToggle show={showLP} onToggle={() => setShowLP(!showLP)} />
                                {err && <p style={{ color: '#ef4444', fontSize: 12, marginTop: 12, fontWeight: 500 }}>{err}</p>}
                                <button type="submit" className="AUTH_btn" disabled={lLoad}>{lLoad ? <span className="AUTH_spin" /> : <><Zap size={14} />Sign In</>}</button>
                            </form>
                            <div className="AUTH_switch">Don't have an account? <button type="button" onClick={() => { setIsSignup(true); setErr(''); }}>Create one</button></div>
                        </div>
                    ) : (
                        <div className="sl">
                            <Brand dark={darkMode} />
                            {step === 1 ? (
                                <>
                                    <h2 style={{ fontSize: 28, color: darkMode ? '#f8fafc' : '#0f172a', fontWeight: 800 }}>Start Your Journey</h2>
                                    <p style={{ color: '#64748b', fontSize: 14 }}>Who are you joining as?</p>
                                    <div className="AUTH_roles">
                                        {ROLE_INFO.map(r => (
                                            <div key={r.role} className={`AUTH_role ${selRole === r.role ? 'on' : ''}`} onClick={() => setSelRole(r.role)}>
                                                <div className="AUTH_role_icon" style={{ background: r.color + '15', color: r.color }}>{r.icon}</div>
                                                <div className="AUTH_role_name" style={{ color: darkMode ? '#f8fafc' : '#0f172a' }}>{r.role}</div>
                                                <div className="AUTH_role_desc">{r.desc}</div>
                                            </div>
                                        ))}
                                    </div>
                                    <button className="AUTH_btn" disabled={!selRole} onClick={() => setStep(2)}>Next Step <ArrowLeft style={{ transform: 'rotate(180deg)' }} size={16} /></button>
                                </>
                            ) : (
                                <>
                                    <button onClick={() => setStep(1)} style={{ background: 'none', border: 'none', color: '#2563eb', cursor: 'pointer', marginBottom: 16, fontSize: 13, display: 'flex', alignItems: 'center', gap: 6 }}>
                                        <ArrowLeft size={14} /> Back to Roles
                                    </button>
                                    <h2 style={{ fontSize: 28, color: darkMode ? '#f8fafc' : '#0f172a', fontWeight: 800 }}>Almost there</h2>
                                    <form onSubmit={doSignup}>
                                        <Field label="Full Name" type="text" placeholder="John Doe" value={sName} onChange={e => setSName(e.target.value)} />
                                        <Field label="Email Address" type="email" placeholder="john@example.com" value={sEmail} onChange={e => setSEmail(e.target.value)} />
                                        <Field label="Password" type="password" placeholder="Create password" value={sPass} onChange={e => setSPass(e.target.value)} showToggle show={showSP} onToggle={() => setShowSP(!showSP)} extra={<PwStr pw={sPass} />} />
                                        <Field label="Confirm Password" type="password" placeholder="Confirm password" value={sConf} onChange={e => setSConf(e.target.value)} />
                                        {selRole !== ROLES.STUDENT && <Field label="Staff Code" type="text" placeholder="Authorized code only" value={sCode} onChange={e => setSCode(e.target.value)} />}
                                        {err && <p style={{ color: '#ef4444', fontSize: 12, marginTop: 12, fontWeight: 500 }}>{err}</p>}
                                        <button type="submit" className="AUTH_btn" disabled={sLoad}>{sLoad ? <span className="AUTH_spin" /> : 'Complete Registration'}</button>
                                    </form>
                                </>
                            )}
                            <div className="AUTH_switch">Already with us? <button type="button" onClick={() => { setIsSignup(false); setStep(1); setErr(''); }}>Sign In</button></div>
                        </div>
                    )}
                </div>
                <div className="AUTH_welcome">
                    <h2 style={{ color: darkMode ? '#f8fafc' : '#0f172a' }}>UNLEASH<br />LEARNING.</h2>
                    <p style={{ color: darkMode ? '#94a3b8' : '#64748b' }}>Experience the next generation of classroom management. Built for schools, optimized for excellence.</p>
                </div>
            </div>
            <footer style={{ marginTop: 'auto', padding: '32px 0 20px', width: '100%', textAlign: 'center' }}>
                <div style={{ 
                    display: 'inline-flex', 
                    alignItems: 'center', 
                    gap: 8, 
                    padding: '8px 20px', 
                    background: darkMode ? 'rgba(255,255,255,0.03)' : 'rgba(0,0,0,0.02)',
                    borderRadius: 30,
                    border: darkMode ? '1px solid rgba(255,255,255,0.05)' : '1px solid rgba(0,0,0,0.05)',
                    fontSize: 12,
                    fontWeight: 600,
                    color: darkMode ? 'rgba(255,255,255,0.4)' : 'rgba(0,0,0,0.4)',
                    letterSpacing: '0.5px'
                }}>
                    <span>© 2026 DIGITAL BLACK BOARD LMS</span>
                    <span style={{ opacity: 0.3 }}>|</span>
                    <span style={{ color: 'var(--primary-light)' }}>POWERED BY TEAM 18</span>
                </div>
            </footer>
        </div>
    );
}
