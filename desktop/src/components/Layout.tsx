import { ReactNode } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useUserStore } from '../store/userStore';
import {
  LogOut, ChevronLeft, Presentation, StickyNote,
  FileText, Box, Bot, Settings
} from 'lucide-react';

interface LayoutProps {
  children: ReactNode;
  title?: string;
  showBack?: boolean;
  backTo?: string;
}

export default function Layout({ children, title, showBack, backTo = '/classroom' }: LayoutProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useUserStore();

  const navItems = [
    { icon: Presentation, label: 'Classroom', path: '/classroom' },
    { icon: StickyNote, label: 'Notes', path: '/notes' },
    { icon: FileText, label: 'PDF Viewer', path: '/pdfs' },
    { icon: Box, label: '3D Models', path: '/models' },
    { icon: Bot, label: 'AI Assistant', path: '/ai' },
    { icon: Settings, label: 'Settings', path: '/settings' },
  ];

  const handleLogout = () => {
    if (window.confirm('Logout karna chahte ho?')) {
      logout();
      navigate('/login');
    }
  };

  const getInitials = (name: string) => {
    return name?.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) || 'T';
  };

  return (
    <div className="h-screen flex bg-gray-50">
      {/* SIDEBAR */}
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
        {/* Logo */}
        <div className="p-4 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <img
              src="/logo.png"
              alt="Logo"
              className="w-10 h-10 rounded-full"
              onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
            />
            <div>
              <h1 className="text-sm font-bold text-blue-600">Deepak Sir</h1>
              <p className="text-xs text-gray-500">Teaching App</p>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <item.icon size={18} />
                <span className="font-medium">{item.label}</span>
              </button>
            );
          })}
        </nav>

        {/* User Info */}
        <div className="p-3 border-t border-gray-200">
          <div className="flex items-center gap-2 mb-2">
            <div className="w-9 h-9 rounded-full bg-blue-500 flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
              {getInitials(user?.fullName || 'Teacher')}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs font-medium text-gray-800 truncate">
                {user?.fullName || 'Teacher'}
              </p>
              <p className="text-xs text-gray-500 truncate">
                {user?.email || ''}
              </p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-3 py-2 bg-red-50 text-red-600 rounded-lg text-sm hover:bg-red-100"
          >
            <LogOut size={14} />
            Logout
          </button>
        </div>
      </aside>

      {/* MAIN CONTENT */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {title && (
          <header className="bg-white border-b border-gray-200 px-6 py-3 flex items-center gap-4 flex-shrink-0">
            {showBack && (
              <button
                onClick={() => navigate(backTo)}
                className="flex items-center gap-1 px-3 py-1.5 bg-gray-100 hover:bg-gray-200 rounded-md text-sm text-gray-700"
              >
                <ChevronLeft size={16} />
                Back
              </button>
            )}
            <h2 className="text-lg font-semibold text-gray-800">{title}</h2>
          </header>
        )}

        <main className="flex-1 overflow-auto">
          {children}
        </main>
      </div>
    </div>
  );
}