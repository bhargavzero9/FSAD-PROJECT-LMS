import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useApp, AppProvider } from './context/AppContext';
import Sidebar from './components/Sidebar';
import TopHeader from './components/TopHeader';
import AuthPage from './pages/AuthPage';
import Dashboard from './pages/Dashboard';
import CoursesPage from './pages/CoursesPage';
import UsersPage from './pages/UsersPage';
import AssignmentsPage from './pages/AssignmentsPage';
import AnnouncementsPage from './pages/AnnouncementsPage';
import AnalyticsPage from './pages/AnalyticsPage';
import SettingsPage from './pages/SettingsPage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import ContentPage from './pages/ContentPage';
import GamesPage from './pages/GamesPage';
import CalendarPage from './pages/CalendarPage';
import QuizPage from './pages/QuizPage';
import MessagesPage from './pages/MessagesPage';
import VerifyEmailPage from './pages/VerifyEmailPage';
import './index.css';

function PrivateRoute({ children }) {
  const { currentUser } = useApp();
  return currentUser ? children : <Navigate to="/auth" />;
}

function AppShell() {
  const { currentUser, logout } = useApp();
  const location = useLocation();
  const navigate = useNavigate();
  const activePage = location.pathname.split('/')[1] || 'dashboard';
  
  const [searchQuery, setSearchQuery] = useState('');

  const handleSetPage = (path) => {
    navigate(`/${path}`);
  };

  return (
    <div className="app-layout">
      <Sidebar activePage={activePage} setActivePage={handleSetPage} onLogout={logout} />
      <main className="main-content">
        <TopHeader activePage={activePage} setActivePage={handleSetPage} searchQuery={searchQuery} setSearchQuery={setSearchQuery} />
        <div className="page-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/courses" element={<CoursesPage activePage="courses" searchQuery={searchQuery} />} />
            <Route path="/my-courses" element={<CoursesPage activePage="my-courses" searchQuery={searchQuery} />} />
            <Route path="/users" element={<UsersPage searchQuery={searchQuery} />} />
            <Route path="/assignments" element={<AssignmentsPage />} />
            <Route path="/announcements" element={<AnnouncementsPage />} />
            <Route path="/analytics" element={<AnalyticsPage />} />
            <Route path="/settings" element={<SettingsPage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="/contact" element={<ContactPage />} />
            <Route path="/content" element={<ContentPage />} />
            <Route path="/games" element={<GamesPage />} />
            <Route path="/calendar" element={<CalendarPage />} />
            <Route path="/quiz" element={<QuizPage />} />
            <Route path="/messages" element={<MessagesPage />} />
          </Routes>
        </div>
      </main>
    </div>
  );
}

function AuthRedirect({ children }) {
  const { currentUser } = useApp();
  const navigate = useNavigate();
  useEffect(() => {
    if (currentUser) navigate('/');
  }, [currentUser, navigate]);
  return children;
}

export default function App() {
  return (
    <AppProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/auth" element={
            <AuthRedirect>
              <AuthPage onLogin={() => {}} onSignup={() => {}} />
            </AuthRedirect>
          } />
          <Route path="/verify-email" element={<VerifyEmailPage />} />
          <Route path="*" element={<PrivateRoute><AppShell /></PrivateRoute>} />
        </Routes>
      </BrowserRouter>
    </AppProvider>
  );
}
