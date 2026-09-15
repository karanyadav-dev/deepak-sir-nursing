import { Link } from 'react-router-dom';
import Layout from '../components/Layout';
import {
  Presentation, BookOpen, FileText, Video, Box,
  Users, BarChart3, Settings
} from 'lucide-react';

export default function Dashboard() {
  const quickActions = [
    { icon: Presentation, label: 'Start Classroom', to: '/classroom', color: 'bg-blue-500' },
    { icon: BookOpen, label: 'Teaching Library', to: '/library', color: 'bg-green-500' },
    { icon: FileText, label: 'Courses', to: '/courses', color: 'bg-purple-500' },
    { icon: Video, label: 'Video Library', to: '/library?type=video', color: 'bg-red-500' },
    { icon: Box, label: '3D Models', to: '/library?type=3d', color: 'bg-orange-500' },
    { icon: Users, label: 'Students', to: '/students', color: 'bg-teal-500' },
    { icon: BarChart3, label: 'Analytics', to: '/analytics', color: 'bg-pink-500' },
    { icon: Settings, label: 'Settings', to: '/settings', color: 'bg-gray-500' },
  ];

  return (
    <Layout title="Dashboard">
      <div className="p-6">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">
          Welcome back! 👋
        </h1>

        {/* Quick Actions Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {quickActions.map((action) => (
            <Link
              key={action.label}
              to={action.to}
              className={`${action.color} text-white p-6 rounded-xl shadow-md hover:shadow-lg hover:scale-105 transition-all flex flex-col items-center justify-center gap-2 min-h-[120px]`}
            >
              <action.icon size={32} />
              <span className="font-medium text-sm text-center">{action.label}</span>
            </Link>
          ))}
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Total Classes Taken</p>
            <p className="text-3xl font-bold text-blue-600">42</p>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Content Uploaded</p>
            <p className="text-3xl font-bold text-green-600">18</p>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Active Students</p>
            <p className="text-3xl font-bold text-purple-600">156</p>
          </div>
        </div>
      </div>
    </Layout>
  );
}