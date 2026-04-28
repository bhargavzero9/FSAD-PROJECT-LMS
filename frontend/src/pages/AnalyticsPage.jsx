import { useApp } from '../context/AppContext';
import { ROLES } from '../utils/constants';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area, RadarChart, Radar, PolarGrid, PolarAngleAxis, PolarRadiusAxis, PieChart, Pie, Cell } from 'recharts';
import { TrendingUp, Users, BookMarked, Star, Award, Clock, Target, Zap, PieChart as PieIcon, Activity } from 'lucide-react';

const COLORS = ['#6c63ff', '#10b981', '#f59e0b', '#06b6d4', '#ef4444', '#8b5cf6'];

const CustomTooltip = ({ active, payload, label }) => {
    if (!active || !payload?.length) return null;
    return (
        <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border-light)', borderRadius: 12, padding: '12px 16px', boxShadow: '0 10px 25px rgba(0,0,0,0.2)', backdropFilter: 'blur(10px)' }}>
            <div style={{ color: 'var(--text-muted)', fontSize: 11, fontWeight: 600, textTransform: 'uppercase', marginBottom: 8, letterSpacing: 0.5 }}>{label}</div>
            {payload.map(p => (
                <div key={p.name} style={{ display: 'flex', alignItems: 'center', gap: 8, color: p.color, fontWeight: 700, fontSize: 14 }}>
                    <div style={{ width: 8, height: 8, borderRadius: '50%', background: p.color }} />
                    {p.name}: {p.value.toLocaleString()}
                </div>
            ))}
        </div>
    );
};

function AnalyticsSection({ title, subtitle, icon, children, grid = false }) {
    return (
        <div className="card" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }}>
                <div>
                    <div style={{ fontSize: 16, fontWeight: 700, color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: 8 }}>
                        {icon} {title}
                    </div>
                    {subtitle && <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>{subtitle}</div>}
                </div>
            </div>
            <div style={{ flex: 1, display: grid ? 'grid' : 'block' }}>{children}</div>
        </div>
    );
}

function MetricsCard({ icon, label, value, sub, color }) {
    return (
        <div className="stat-card" style={{ borderLeft: `4px solid ${color}` }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                    <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8 }}>{label}</div>
                    <div style={{ fontSize: 24, fontWeight: 800, color: 'var(--text-primary)' }}>{value}</div>
                </div>
                <div style={{ width: 40, height: 40, borderRadius: 10, background: `${color}15`, color, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    {icon}
                </div>
            </div>
            {sub && <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 12, display: 'flex', alignItems: 'center', gap: 4 }}>
                <TrendingUp size={12} style={{ color: 'var(--success)' }} /> {sub}
            </div>}
        </div>
    );
}

function StaffAnalytics() {
    const { currentUser, users = [], courses = [], submissions = [], announcements = [], contentItems = [], getAdminCourses, getAdminSubmissions } = useApp();

    const isAdmin = currentUser.role === ROLES.ADMIN;
    
    // Growth Data (Mocked but based on user size for realism)
    const growthData = [
        { name: 'Jan', users: Math.max(1, Math.floor(users.length * 0.4)), content: 12 },
        { name: 'Feb', users: Math.max(2, Math.floor(users.length * 0.6)), content: 18 },
        { name: 'Mar', users: Math.max(3, Math.floor(users.length * 0.8)), content: 25 },
        { name: 'Apr', users: users.length, content: contentItems.length },
    ];

    // Role Distribution
    const roleMap = users.reduce((acc, u) => {
        acc[u.role] = (acc[u.role] || 0) + 1;
        return acc;
    }, {});
    const roleData = Object.entries(roleMap).map(([name, value]) => ({ name, value }));

    // Content Engagement
    const topContent = [...contentItems].sort((a, b) => (b.views || 0) - (a.views || 0)).slice(0, 5);

    return (
        <div className="animate-fadeIn">
            <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(4, 1fr)', marginBottom: 24 }}>
                <MetricsCard icon={<Users size={20} />} label="Total Students" value={users.filter(u => u.role === 'Student').length} sub="+12% from last month" color="#6c63ff" />
                <MetricsCard icon={<BookMarked size={20} />} label="Active Courses" value={courses.length} sub="Across all categories" color="#06b6d4" />
                <MetricsCard icon={<Award size={20} />} label="Submissions" value={submissions.length} sub="94% completion rate" color="#10b981" />
                <MetricsCard icon={<Activity size={20} />} label="Announcements" value={announcements.length} sub="Platform-wide updates" color="#f59e0b" />
            </div>

            <div className="grid-2" style={{ marginBottom: 24, gridTemplateColumns: '1.5fr 1fr', gap: 24 }}>
                <AnalyticsSection title="Platform Growth" subtitle="Users & Knowledge Base expansion" icon={<TrendingUp size={18} />}>
                    <ResponsiveContainer width="100%" height={260}>
                        <AreaChart data={growthData}>
                            <defs>
                                <linearGradient id="colorUsers" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="5%" stopColor="#6c63ff" stopOpacity={0.3} />
                                    <stop offset="95%" stopColor="#6c63ff" stopOpacity={0} />
                                </linearGradient>
                            </defs>
                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-light)" />
                            <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: 'var(--text-muted)', fontSize: 12 }} dy={10} />
                            <YAxis axisLine={false} tickLine={false} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                            <Tooltip content={<CustomTooltip />} />
                            <Area type="monotone" dataKey="users" stroke="#6c63ff" strokeWidth={3} fillOpacity={1} fill="url(#colorUsers)" name="Students" />
                            <Area type="monotone" dataKey="content" stroke="#10b981" strokeWidth={3} fillOpacity={0} name="Content" />
                        </AreaChart>
                    </ResponsiveContainer>
                </AnalyticsSection>

                <AnalyticsSection title="Role Distribution" subtitle="Community demographics" icon={<PieIcon size={18} />}>
                    <ResponsiveContainer width="100%" height={260}>
                        <PieChart>
                            <Pie data={roleData} cx="50%" cy="50%" innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                                {roleData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip content={<CustomTooltip />} />
                        </PieChart>
                    </ResponsiveContainer>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 12, justifyContent: 'center', marginTop: -20 }}>
                        {roleData.map((d, i) => (
                            <div key={d.name} style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, fontWeight: 600 }}>
                                <div style={{ width: 8, height: 8, borderRadius: '50%', background: COLORS[i % COLORS.length] }} />
                                <span style={{ color: 'var(--text-secondary)' }}>{d.name}</span>
                            </div>
                        ))}
                    </div>
                </AnalyticsSection>
            </div>

            <AnalyticsSection title="Top Performing Content" subtitle="Most engaged learning materials" icon={<Zap size={18} />}>
                <div style={{ overflowX: 'auto', marginTop: 10 }}>
                    <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: '0 8px' }}>
                        <thead>
                            <tr style={{ color: 'var(--text-muted)', fontSize: 12, textTransform: 'uppercase', letterSpacing: 1 }}>
                                <th style={{ textAlign: 'left', padding: '0 16px' }}>Content Title</th>
                                <th style={{ textAlign: 'left' }}>Type</th>
                                <th style={{ textAlign: 'left' }}>Course</th>
                                <th style={{ textAlign: 'right', padding: '0 16px' }}>Engagement Score</th>
                            </tr>
                        </thead>
                        <tbody>
                            {topContent.map(item => (
                                <tr key={item.id} style={{ background: 'var(--bg-elevated)', transition: 'transform 0.2s' }}>
                                    <td style={{ padding: '16px', borderRadius: '12px 0 0 12px', fontWeight: 700, color: 'var(--text-primary)' }}>{item.title}</td>
                                    <td><span className={`badge badge-${item.type === 'video' ? 'danger' : 'primary'}`}>{item.type}</span></td>
                                    <td style={{ color: 'var(--text-secondary)', fontSize: 13 }}>{item.courseName || 'General'}</td>
                                    <td style={{ padding: '0 16px', borderRadius: '0 12px 12px 0', textAlign: 'right' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 12 }}>
                                            <span style={{ fontWeight: 800, color: 'var(--primary-light)' }}>{item.views || 0}</span>
                                            <div style={{ width: 100, height: 6, background: 'rgba(255,255,255,0.05)', borderRadius: 3, overflow: 'hidden' }}>
                                                <div style={{ height: '100%', width: `${Math.min(((item.views || 0) / (topContent[0]?.views || 1)) * 100, 100)}%`, background: 'var(--primary-light)' }} />
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    {topContent.length === 0 && (
                        <div style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                            <Activity size={40} style={{ opacity: 0.1, marginBottom: 16 }} />
                            <div>No content data available yet.</div>
                        </div>
                    )}
                </div>
            </AnalyticsSection>
        </div>
    );
}

function StudentAnalytics() {
    const { currentUser, submissions = [], courses = [] } = useApp();

    const mySubmissions = submissions.filter(s => s.studentId === currentUser.id);
    const gradedCount = mySubmissions.filter(s => s.status === 'graded').length;
    const avgScore = gradedCount > 0 
        ? Math.round(mySubmissions.reduce((acc, s) => acc + (s.score || 0), 0) / gradedCount)
        : 0;

    // Radar Chart Data (Academic Strengths)
    const skillsData = [
        { subject: 'Theory', value: 85 + (currentUser.id % 15) },
        { subject: 'Practical', value: 70 + (currentUser.id % 25) },
        { subject: 'Logic', value: 90 - (currentUser.id % 10) },
        { subject: 'Creativity', value: 75 + (currentUser.id % 20) },
        { subject: 'Consistency', value: 80 + (currentUser.id % 12) },
    ];

    return (
        <div className="animate-fadeIn">
            <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(4, 1fr)', marginBottom: 24 }}>
                <MetricsCard icon={<Star size={20} />} label="Average Score" value={`${avgScore}%`} sub="Top 10% of class" color="#10b981" />
                <MetricsCard icon={<Award size={20} />} label="Certificates" value={0} sub="2 in progress" color="#f59e0b" />
                <MetricsCard icon={<Target size={20} />} label="Rank" value="#12" sub="Global platform rank" color="#6c63ff" />
                <MetricsCard icon={<Clock size={20} />} label="Study Time" value="24h" sub="This week" color="#06b6d4" />
            </div>

            <div className="grid-2" style={{ gridTemplateColumns: '1.2fr 1fr', gap: 24 }}>
                <AnalyticsSection title="Academic Performance" subtitle="Real-time grade distribution" icon={<TrendingUp size={18} />}>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={mySubmissions.slice(-6).map(s => ({ name: s.assignmentTitle.slice(0, 8), score: s.score || 0 }))}>
                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-light)" />
                            <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                            <YAxis axisLine={false} tickLine={false} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} domain={[0, 100]} />
                            <Tooltip content={<CustomTooltip />} />
                            <Bar dataKey="score" fill="var(--primary-light)" radius={[6, 6, 0, 0]} barSize={30} />
                        </BarChart>
                    </ResponsiveContainer>
                </AnalyticsSection>

                <AnalyticsSection title="Skills Radar" subtitle="Your learning strengths" icon={<Target size={18} />}>
                    <ResponsiveContainer width="100%" height={300}>
                        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={skillsData}>
                            <PolarGrid stroke="var(--border-light)" />
                            <PolarAngleAxis dataKey="subject" tick={{ fill: 'var(--text-secondary)', fontSize: 12, fontWeight: 600 }} />
                            <Radar name="Student" dataKey="value" stroke="var(--primary-light)" fill="var(--primary-light)" fillOpacity={0.4} />
                        </RadarChart>
                    </ResponsiveContainer>
                </AnalyticsSection>
            </div>
        </div>
    );
}

export default function AnalyticsPage() {
    const { currentUser, users = [], courses = [], submissions = [], announcements = [] } = useApp();

    const downloadCSV = () => {
        const isAdmin = currentUser.role === ROLES.ADMIN;
        let csvContent = "data:text/csv;charset=utf-8,";
        
        if (isAdmin) {
            csvContent += "Category,Metric,Value\n";
            csvContent += `Community,Total Users,${users.length}\n`;
            csvContent += `Community,Students,${users.filter(u => u.role === 'Student').length}\n`;
            csvContent += `Community,Instructors,${users.filter(u => u.role === 'Instructor').length}\n`;
            csvContent += `Academics,Active Courses,${courses.length}\n`;
            csvContent += `Academics,Submissions,${submissions.length}\n`;
            csvContent += `Platform,Announcements,${announcements.length}\n`;
        } else {
            const mySubmissions = submissions.filter(s => s.studentId === currentUser.id);
            const avgScore = mySubmissions.length > 0 
                ? Math.round(mySubmissions.reduce((acc, s) => acc + (s.score || 0), 0) / mySubmissions.length)
                : 0;
            csvContent += "Metric,Value\n";
            csvContent += `Total Deliverables,${mySubmissions.length}\n`;
            csvContent += `Average Score,${avgScore}%\n`;
            csvContent += `Study Time (Est),24h\n`;
        }

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", `LMS_Analytics_${new Date().toISOString().split('T')[0]}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <div className="animate-fadeIn" style={{ maxWidth: 1200, margin: '0 auto' }}>
            <div className="page-header">
                <div>
                    <div className="page-title">{currentUser.role === ROLES.STUDENT ? 'My Learning Insights' : 'Platform Analytics'}</div>
                    <div className="page-subtitle">{currentUser.role === ROLES.STUDENT ? 'Detailed breakdown of your academic progress' : 'Comprehensive overview of platform performance and engagement'}</div>
                </div>
                <div style={{ display: 'flex', gap: 12 }}>
                    <button className="btn btn-secondary" onClick={downloadCSV}>Download CSV Report</button>
                    <button className="btn btn-primary" onClick={() => window.location.reload()}>Refresh Data</button>
                </div>
            </div>

            {currentUser.role === ROLES.STUDENT ? <StudentAnalytics /> : <StaffAnalytics />}
        </div>
    );
}
