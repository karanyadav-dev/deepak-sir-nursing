'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { questionAPI, subjectAPI } from '@/app/services/api';

interface Question {
  id: string;
  questionText: string;
  difficulty: string;
  subjectName?: string;
}

interface Subject {
  id: string;
  name: string;
}

export default function QuestionsPage() {
  const router = useRouter();
  const [questions, setQuestions] = useState<Question[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    questionText: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctAnswer: 'A',
    explanation: '',
    difficulty: 'MEDIUM',
    subjectId: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [questionsRes, subjectsRes] = await Promise.all([
        questionAPI.getAll({ limit: 50 }),
        subjectAPI.getAll(),
      ]);
      setQuestions(questionsRes.data?.data?.content || questionsRes.data?.data || []);
      setSubjects(subjectsRes.data?.data || []);
    } catch (error) {
      console.error('Failed to load questions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await questionAPI.create(formData);
      setShowForm(false);
      setFormData({
        questionText: '',
        optionA: '',
        optionB: '',
        optionC: '',
        optionD: '',
        correctAnswer: 'A',
        explanation: '',
        difficulty: 'MEDIUM',
        subjectId: '',
      });
      loadData();
    } catch (error) {
      console.error('Failed to create question:', error);
      alert('Failed to create question');
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Delete this question?')) return;
    try {
      await questionAPI.delete(id);
      loadData();
    } catch (error) {
      alert('Failed to delete question');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-semibold">Questions</h1>
          <div className="flex gap-3">
            <button
              onClick={() => router.push('/dashboard')}
              className="bg-gray-500 text-white px-4 py-2 rounded-md"
            >
              Back
            </button>
            <button
              onClick={() => setShowForm(!showForm)}
              className="bg-blue-600 text-white px-4 py-2 rounded-md"
            >
              {showForm ? 'Cancel' : '+ New Question'}
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6">
        {showForm && (
          <div className="bg-white p-6 rounded-lg shadow mb-6">
            <h2 className="text-lg font-semibold mb-4">Create New Question</h2>
            <form onSubmit={handleCreate}>
              <textarea
                placeholder="Question text"
                value={formData.questionText}
                onChange={(e) => setFormData({ ...formData, questionText: e.target.value })}
                className="w-full px-3 py-2 border rounded-md mb-3"
                rows={3}
                required
              />
              <div className="grid grid-cols-1 gap-3">
                <input type="text" placeholder="Option A" value={formData.optionA}
                  onChange={(e) => setFormData({ ...formData, optionA: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md" required />
                <input type="text" placeholder="Option B" value={formData.optionB}
                  onChange={(e) => setFormData({ ...formData, optionB: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md" required />
                <input type="text" placeholder="Option C" value={formData.optionC}
                  onChange={(e) => setFormData({ ...formData, optionC: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md" required />
                <input type="text" placeholder="Option D" value={formData.optionD}
                  onChange={(e) => setFormData({ ...formData, optionD: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md" required />
              </div>
              <div className="grid grid-cols-2 gap-3 mt-3">
                <select
                  value={formData.correctAnswer}
                  onChange={(e) => setFormData({ ...formData, correctAnswer: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md"
                >
                  <option value="A">Correct: A</option>
                  <option value="B">Correct: B</option>
                  <option value="C">Correct: C</option>
                  <option value="D">Correct: D</option>
                </select>
                <select
                  value={formData.difficulty}
                  onChange={(e) => setFormData({ ...formData, difficulty: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md"
                >
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>
              <select
                value={formData.subjectId}
                onChange={(e) => setFormData({ ...formData, subjectId: e.target.value })}
                className="w-full px-3 py-2 border rounded-md mt-3"
                required
              >
                <option value="">Select Subject</option>
                {subjects.map((subject) => (
                  <option key={subject.id} value={subject.id}>{subject.name}</option>
                ))}
              </select>
              <textarea
                placeholder="Explanation (optional)"
                value={formData.explanation}
                onChange={(e) => setFormData({ ...formData, explanation: e.target.value })}
                className="w-full px-3 py-2 border rounded-md mt-3"
                rows={2}
              />
              <button type="submit" className="bg-green-600 text-white px-4 py-2 rounded-md mt-3">
                Create Question
              </button>
            </form>
          </div>
        )}

        {loading ? (
          <div className="text-center py-12">Loading questions...</div>
        ) : (
          <div className="space-y-3">
            {questions.map((question) => (
              <div key={question.id} className="bg-white p-4 rounded-lg shadow flex justify-between items-center">
                <div className="flex-1">
                  <p className="font-medium">{question.questionText?.substring(0, 100)}...</p>
                  <div className="flex gap-2 mt-2">
                    <span className="text-xs px-2 py-1 bg-blue-100 text-blue-700 rounded">
                      {question.difficulty}
                    </span>
                    {question.subjectName && (
                      <span className="text-xs px-2 py-1 bg-green-100 text-green-700 rounded">
                        {question.subjectName}
                      </span>
                    )}
                  </div>
                </div>
                <button
                  onClick={() => handleDelete(question.id)}
                  className="bg-red-500 text-white px-3 py-1 rounded text-sm ml-4"
                >
                  Delete
                </button>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}