'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { testAPI } from '@/app/services/api';

interface Test {
  id: string;
  name: string;
  durationMinutes: number;
  totalQuestions: number;
  published: boolean;
}

export default function TestsPage() {
  const router = useRouter();
  const [tests, setTests] = useState<Test[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTests();
  }, []);

  const loadTests = async () => {
    try {
      const response = await testAPI.getAll();
      setTests(response.data?.data || []);
    } catch (error) {
      console.error('Failed to load tests:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this test?')) return;
    try {
      await testAPI.delete(id);
      loadTests();
    } catch (error) {
      alert('Failed to delete test');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-semibold">Tests</h1>
          <button
            onClick={() => router.push('/dashboard')}
            className="bg-gray-500 text-white px-4 py-2 rounded-md"
          >
            Back
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6">
        {loading ? (
          <div className="text-center py-12">Loading tests...</div>
        ) : (
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {tests.map((test) => (
              <div key={test.id} className="bg-white p-6 rounded-lg shadow">
                <h3 className="font-semibold text-lg mb-2">{test.name}</h3>
                <div className="text-sm text-gray-600">
                  <p>Duration: {test.durationMinutes} min</p>
                  <p>Questions: {test.totalQuestions}</p>
                </div>
                <div className="mt-4 flex justify-between items-center">
                  <span className={`px-2 py-1 rounded text-xs ${test.published ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'}`}>
                    {test.published ? 'Published' : 'Draft'}
                  </span>
                  <button
                    onClick={() => handleDelete(test.id)}
                    className="bg-red-500 text-white px-3 py-1 rounded text-sm"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}