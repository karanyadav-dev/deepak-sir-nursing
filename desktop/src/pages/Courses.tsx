import { useState } from 'react';
import Layout from '../components/Layout';
import {
  Plus, Search, Edit, Trash2, BookOpen, Users,
  Clock, DollarSign, CheckCircle, XCircle
} from 'lucide-react';

interface Course {
  id: string;
  title: string;
  description: string;
  price: number;
  duration: string;
  enrolled: number;
  published: boolean;
  isPremium: boolean;
  thumbnail?: string;
}

export default function Courses() {
  const [courses, setCourses] = useState<Course[]>([
    {
      id: '1',
      title: 'GNM 1st Year Complete Course',
      description: 'Complete GNM 1st year syllabus with all subjects',
      price: 999,
      duration: '12 months',
      enrolled: 45,
      published: true,
      isPremium: true,
    },
    {
      id: '2',
      title: 'B.Sc Nursing Foundation',
      description: 'B.Sc Nursing 1st year foundation course',
      price: 1499,
      duration: '12 months',
      enrolled: 32,
      published: true,
      isPremium: true,
    },
    {
      id: '3',
      title: 'NORCET Preparation',
      description: 'Complete NORCET exam preparation',
      price: 0,
      duration: '6 months',
      enrolled: 128,
      published: true,
      isPremium: false,
    },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    price: 0,
    duration: '',
    published: true,
    isPremium: false,
  });

  const handleAddNew = () => {
    setEditingCourse(null);
    setFormData({
      title: '',
      description: '',
      price: 0,
      duration: '',
      published: true,
      isPremium: false,
    });
    setShowForm(true);
  };

  const handleEdit = (course: Course) => {
    setEditingCourse(course);
    setFormData({
      title: course.title,
      description: course.description,
      price: course.price,
      duration: course.duration,
      published: course.published,
      isPremium: course.isPremium,
    });
    setShowForm(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (editingCourse) {
      // Update existing
      setCourses(courses.map(c =>
        c.id === editingCourse.id
          ? { ...c, ...formData }
          : c
      ));
      alert('✅ Course updated successfully!');
    } else {
      // Add new
      const newCourse: Course = {
        id: Date.now().toString(),
        ...formData,
        enrolled: 0,
      };
      setCourses([newCourse, ...courses]);
      alert('✅ Course created successfully!');
    }

    setShowForm(false);
    setEditingCourse(null);
  };

  const handleDelete = (id: string) => {
    if (!window.confirm('Delete this course?')) return;
    setCourses(courses.filter(c => c.id !== id));
    alert('✅ Course deleted!');
  };

  const togglePublish = (id: string) => {
    setCourses(courses.map(c =>
      c.id === id ? { ...c, published: !c.published } : c
    ));
  };

  const filteredCourses = courses.filter(c =>
    c.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Layout title="Courses" showBack>
      <div className="p-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div className="flex-1 max-w-md relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <input
              type="text"
              placeholder="Search courses..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <button
            onClick={handleAddNew}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"
          >
            <Plus size={18} />
            New Course
          </button>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500">Total Courses</p>
            <p className="text-2xl font-bold text-gray-800">{courses.length}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500">Published</p>
            <p className="text-2xl font-bold text-green-600">
              {courses.filter(c => c.published).length}
            </p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500">Total Enrolled</p>
            <p className="text-2xl font-bold text-blue-600">
              {courses.reduce((sum, c) => sum + c.enrolled, 0)}
            </p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <p className="text-sm text-gray-500">Revenue</p>
            <p className="text-2xl font-bold text-purple-600">
              ₹{courses.reduce((sum, c) => sum + (c.price * c.enrolled), 0).toLocaleString()}
            </p>
          </div>
        </div>

        {/* Form Modal */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
              <div className="p-6">
                <h2 className="text-xl font-bold mb-4">
                  {editingCourse ? 'Edit Course' : 'Create New Course'}
                </h2>

                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Course Title *
                    </label>
                    <input
                      type="text"
                      value={formData.title}
                      onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="e.g., GNM 1st Year Complete Course"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Description
                    </label>
                    <textarea
                      value={formData.description}
                      onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      rows={3}
                      placeholder="Course description..."
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Price (₹)
                      </label>
                      <input
                        type="number"
                        value={formData.price}
                        onChange={(e) => setFormData({ ...formData, price: Number(e.target.value) })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        min="0"
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Duration
                      </label>
                      <input
                        type="text"
                        value={formData.duration}
                        onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="e.g., 12 months"
                      />
                    </div>
                  </div>

                  <div className="flex gap-4">
                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={formData.published}
                        onChange={(e) => setFormData({ ...formData, published: e.target.checked })}
                        className="w-4 h-4"
                      />
                      <span className="text-sm text-gray-700">Publish immediately</span>
                    </label>

                    <label className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={formData.isPremium}
                        onChange={(e) => setFormData({ ...formData, isPremium: e.target.checked })}
                        className="w-4 h-4"
                      />
                      <span className="text-sm text-gray-700">Premium course</span>
                    </label>
                  </div>

                  <div className="flex gap-3 pt-4">
                    <button
                      type="submit"
                      className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 font-medium"
                    >
                      {editingCourse ? 'Update Course' : 'Create Course'}
                    </button>
                    <button
                      type="button"
                      onClick={() => setShowForm(false)}
                      className="flex-1 bg-gray-200 text-gray-700 py-2 rounded-lg hover:bg-gray-300 font-medium"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}

        {/* Courses List */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredCourses.map((course) => (
            <div
              key={course.id}
              className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow"
            >
              {/* Thumbnail */}
              <div className="h-32 bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center">
                <BookOpen size={48} className="text-white" />
              </div>

              {/* Content */}
              <div className="p-4">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="font-semibold text-gray-800 text-sm flex-1">
                    {course.title}
                  </h3>
                  {course.isPremium && (
                    <span className="text-xs bg-yellow-100 text-yellow-700 px-2 py-0.5 rounded ml-2">
                      Premium
                    </span>
                  )}
                </div>

                <p className="text-xs text-gray-500 mb-3 line-clamp-2">
                  {course.description}
                </p>

                <div className="flex items-center gap-3 text-xs text-gray-600 mb-3">
                  <span className="flex items-center gap-1">
                    <Users size={12} /> {course.enrolled}
                  </span>
                  <span className="flex items-center gap-1">
                    <Clock size={12} /> {course.duration}
                  </span>
                  <span className="flex items-center gap-1">
                    <DollarSign size={12} /> {course.price === 0 ? 'Free' : `₹${course.price}`}
                  </span>
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-gray-100">
                  <button
                    onClick={() => togglePublish(course.id)}
                    className={`text-xs px-2 py-1 rounded flex items-center gap-1 ${
                      course.published
                        ? 'bg-green-100 text-green-700'
                        : 'bg-gray-100 text-gray-600'
                    }`}
                  >
                    {course.published ? (
                      <><CheckCircle size={12} /> Published</>
                    ) : (
                      <><XCircle size={12} /> Draft</>
                    )}
                  </button>
                  <div className="flex gap-1">
                    <button
                      onClick={() => handleEdit(course)}
                      className="p-1.5 text-blue-600 hover:bg-blue-50 rounded"
                      title="Edit"
                    >
                      <Edit size={14} />
                    </button>
                    <button
                      onClick={() => handleDelete(course.id)}
                      className="p-1.5 text-red-600 hover:bg-red-50 rounded"
                      title="Delete"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {filteredCourses.length === 0 && (
          <div className="text-center py-16">
            <BookOpen size={64} className="mx-auto text-gray-300 mb-3" />
            <p className="text-gray-500">No courses found</p>
            <button
              onClick={handleAddNew}
              className="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
            >
              Create Your First Course
            </button>
          </div>
        )}
      </div>
    </Layout>
  );
}