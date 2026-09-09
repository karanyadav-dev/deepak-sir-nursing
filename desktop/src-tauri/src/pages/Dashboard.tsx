import { Link } from 'react-router-dom';
import { useUserStore } from '../store/userStore';
import { BookOpen, Presentation, FileText, Video, Box, Settings } from 'lucide-react';

export default function Dashboard() {
  const { user, logout } = useUserStore();

  const quickActions = [
    { icon: Presentation, label: 'Start Classroom', to: '/classroom', color: 'bg-blue-500' },
    { icon: BookOpen, label: 'Library', to: '/library', color: 'bg-green-500' },
    { icon: FileText, label: 'Courses', to: '/library?type=courses', color: 'bg-purple-500' },
    { icon: Video, label: 'Video Library', to: '/library?type=video', color: 'bg-red-500' },
    { icon: Box, label: '3D Models', to: '/library?type=3d', color: 'bg-orange-500' },
    { icon: Settings, label: 'Settings', to: '/settings', color: 'bg-gray-500' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow p-4 flex justify-between items-center">
        <h1 className="text-xl font-semibold">Deepak Sir Nursing</h1>
        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600">{user?.fullName}</span>
          <button onClick={logout} className="text-sm text-red-600 hover:underline">Logout</button>
        </div>
      </header>
      <main className="p-6">
        <h2 className="text-2xl font-bold mb-6">Dashboard</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {quickActions.map((action) => (
            <Link
              key={action.label}
              to={action.to}
              className={`${action.color} text-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow flex flex-col items-center justify-center gap-2`}
            >
              <action.icon size={32} />
              <span className="font-medium">{action.label}</span>
            </Link>
          ))}
        </div>
      </main>
    </div>
  );
}