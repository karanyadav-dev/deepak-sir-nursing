'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import { courseAPI, userAPI, testAPI } from '@/app/services/api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Users, BookOpen, FileQuestion, ClipboardList, LogOut } from 'lucide-react';

export default function DashboardPage() {
  const router = useRouter();
  const [stats, setStats] = useState({
    totalStudents: 0,
    totalCourses: 0,
    totalQuestions: 0,
    totalTests: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('admin_token');
    if (!token) {
      router.push('/login');
      return;
    }
    loadDashboardData();
  }, [router]);

  const loadDashboardData = async () => {
    try {
      const [usersRes, coursesRes, testsRes] = await Promise.all([
        userAPI.getAll({ limit: 1 }),
        courseAPI.getAll(),
        testAPI.getAll(),
      ]);

      setStats({
        totalStudents: usersRes.data?.data?.totalElements || 0,
        totalCourses: coursesRes.data?.data?.length || 0,
        totalQuestions: 0,
        totalTests: testsRes.data?.data?.length || 0,
      });
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_user');
    router.push('/login');
  };

  const statCards = [
    { title: 'Total Students', value: stats.totalStudents, icon: Users, color: 'bg-blue-500' },
    { title: 'Courses', value: stats.totalCourses, icon: BookOpen, color: 'bg-purple-500' },
    { title: 'Questions', value: stats.totalQuestions, icon: FileQuestion, color: 'bg-orange-500' },
    { title: 'Tests', value: stats.totalTests, icon: ClipboardList, color: 'bg-pink-500' },
  ];

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-3 flex justify-between items-center">
          <div className="flex items-center gap-3">
            <Image
              src="/logo.png"
              alt="Deepak Sir Nursing"
              width={50}
              height={50}
              className="rounded-full"
            />
            <div>
              <h1 className="text-lg font-bold text-blue-600">Deepak Sir Nursing</h1>
              <p className="text-xs text-gray-500">Admin Panel</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600 flex items-center gap-2"
          >
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className="mx-auto max-w-7xl px-4 py-6">
        {loading ? (
          <div className="text-center py-12">
            <div className="text-gray-500">Loading dashboard...</div>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
              {statCards.map((stat, index) => (
                <Card key={index}>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
                    <div className={`${stat.color} p-2 rounded-lg`}>
                      <stat.icon className="h-4 w-4 text-white" />
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{stat.value}</div>
                  </CardContent>
                </Card>
              ))}
            </div>

            <div className="mt-8">
              <h2 className="text-lg font-semibold mb-4">Quick Actions</h2>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                <button
                  onClick={() => router.push('/courses')}
                  className="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow text-left"
                >
                  <h3 className="font-semibold text-lg">Manage Courses</h3>
                  <p className="text-gray-600 text-sm">Create and edit courses</p>
                </button>
                <button
                  onClick={() => router.push('/questions')}
                  className="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow text-left"
                >
                  <h3 className="font-semibold text-lg">Manage Questions</h3>
                  <p className="text-gray-600 text-sm">Add and edit MCQs</p>
                </button>
                <button
                  onClick={() => router.push('/tests')}
                  className="bg-white p-6 rounded-lg shadow hover:shadow-md transition-shadow text-left"
                >
                  <h3 className="font-semibold text-lg">Manage Tests</h3>
                  <p className="text-gray-600 text-sm">Create mock tests</p>
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}