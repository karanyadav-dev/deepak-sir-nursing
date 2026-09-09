'use client'

import { useState, useEffect } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { 
  Users, BookOpen, FileQuestion, ClipboardList, 
  TrendingUp, IndianRupee, Award, Video
} from 'lucide-react'

export default function AdminDashboard() {
  const [mounted, setMounted] = useState(false)
  
  const [stats] = useState({
    totalStudents: 15234,
    newStudents: 128,
    activeStudents: 8921,
    courses: 45,
    lectures: 324,
    questions: 15000,
    tests: 87,
    orders: 2341,
    revenue: 2345000,
    subscriptions: 1200
  })

  // Fix hydration error - only render after mount
  useEffect(() => {
    setMounted(true)
  }, [])

  if (!mounted) {
    return null
  }

  const statCards = [
    { title: 'Total Students', value: stats.totalStudents, icon: Users, color: 'blue' },
    { title: 'Active Students', value: stats.activeStudents, icon: Users, color: 'green' },
    { title: 'Courses', value: stats.courses, icon: BookOpen, color: 'purple' },
    { title: 'Questions', value: stats.questions, icon: FileQuestion, color: 'orange' },
    { title: 'Tests', value: stats.tests, icon: ClipboardList, color: 'pink' },
    { title: 'Revenue', value: `₹${(stats.revenue/100000).toFixed(1)}L`, icon: IndianRupee, color: 'emerald' },
    { title: 'Subscriptions', value: stats.subscriptions, icon: Award, color: 'indigo' },
    { title: 'Video Lectures', value: stats.lectures, icon: Video, color: 'red' },
  ]

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-6">
          <h1 className="text-2xl font-semibold text-gray-900">
            Deepak Sir Admin Dashboard
          </h1>
        </div>
      </header>

      {/* Main Content */}
      <main className="mx-auto max-w-7xl px-4 py-6">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {statCards.map((stat, index) => (
            <Card key={index}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">
                  {stat.title}
                </CardTitle>
                <stat.icon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stat.value}</div>
                <p className="text-xs text-muted-foreground">
                  +{stats.newStudents} from last month
                </p>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Recent Activity */}
        <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Recent Test Attempts</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[1, 2, 3, 4, 5].map((i) => (
                  <div key={i} className="flex items-center justify-between border-b pb-2">
                    <div>
                      <p className="text-sm font-medium">Student {i}</p>
                      <p className="text-xs text-gray-500">AIIMS Nursing Mock Test {i}</p>
                    </div>
                    <span className="text-sm text-green-600">85%</span>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Top Performing Students</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {[1, 2, 3, 4, 5].map((i) => (
                  <div key={i} className="flex items-center justify-between border-b pb-2">
                    <div className="flex items-center">
                      <div className="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center text-white text-sm">
                        {i}
                      </div>
                      <div className="ml-3">
                        <p className="text-sm font-medium">Student Name {i}</p>
                        <p className="text-xs text-gray-500">Rank #{i}</p>
                      </div>
                    </div>
                    <span className="text-sm font-semibold">{100 - i * 2}%</span>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  )
}