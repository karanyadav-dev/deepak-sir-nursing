'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/app/services/api';

interface Payment {
  id: string;
  orderId: string;
  userId: string;
  userName: string;
  amount: number;
  status: string;
  method: string;
  createdAt: string;
}

export default function PaymentsPage() {
  const router = useRouter();
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalPayments: 0,
    totalRevenue: 0,
    successful: 0,
    failed: 0,
  });

  useEffect(() => {
    loadPayments();
  }, []);

  const loadPayments = async () => {
    try {
      const response = await api.get('/payments/history?limit=100');
      const paymentData = response.data?.data || [];
      setPayments(paymentData);

      // Calculate stats
      const total = paymentData.length;
      const successful = paymentData.filter((p: Payment) => p.status === 'SUCCESS').length;
      const totalRevenue = paymentData
        .filter((p: Payment) => p.status === 'SUCCESS')
        .reduce((sum: number, p: Payment) => sum + p.amount, 0);

      setStats({
        totalPayments: total,
        totalRevenue,
        successful,
        failed: total - successful,
      });
    } catch (error) {
      console.error('Failed to load payments:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-semibold">Payments</h1>
          <button
            onClick={() => router.push('/dashboard')}
            className="bg-gray-500 text-white px-4 py-2 rounded-md"
          >
            Back
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-4 mb-6">
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-sm text-gray-500">Total Payments</h3>
            <p className="text-2xl font-bold">{stats.totalPayments}</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-sm text-gray-500">Total Revenue</h3>
            <p className="text-2xl font-bold text-green-600">₹{stats.totalRevenue}</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-sm text-gray-500">Successful</h3>
            <p className="text-2xl font-bold text-blue-600">{stats.successful}</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow">
            <h3 className="text-sm text-gray-500">Failed</h3>
            <p className="text-2xl font-bold text-red-600">{stats.failed}</p>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-12">Loading payments...</div>
        ) : (
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="min-w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Order ID</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">User</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Method</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {payments.map((payment) => (
                  <tr key={payment.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 font-mono text-sm">{payment.orderId}</td>
                    <td className="px-6 py-4">{payment.userName}</td>
                    <td className="px-6 py-4">₹{payment.amount}</td>
                    <td className="px-6 py-4">{payment.method}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded text-xs ${
                        payment.status === 'SUCCESS' 
                          ? 'bg-green-100 text-green-700' 
                          : payment.status === 'FAILED'
                          ? 'bg-red-100 text-red-700'
                          : 'bg-yellow-100 text-yellow-700'
                      }`}>
                        {payment.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      {new Date(payment.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}