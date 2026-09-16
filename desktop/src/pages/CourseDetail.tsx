import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import {
  ArrowLeft, Plus, Video, FileText, Book, Trash2,
  Upload, Play, X, Save, Edit, ChevronRight, ChevronDown
} from 'lucide-react';
import { api } from '../services/api';

interface Lesson {
  id: string;
  title: string;
  type: 'video' | 'pdf' | 'notes';
  url?: string;
  duration?: string;
}

interface Chapter {
  id: string;
  title: string;
  lessons: Lesson[];
  expanded: boolean;
}

interface Course {
  id: string;
  title: string;
  description: string;
  price: number;
  chapters: Chapter[];
}

export default function CourseDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [course, setCourse] = useState<Course>({
    id: id || '1',
    title: 'GNM 1st Year Complete Course',
    description: 'Complete GNM 1st year syllabus',
    price: 999,
    chapters: [
      {
        id: 'ch1',
        title: 'Chapter 1: Introduction to Nursing',
        expanded: true,
        lessons: [
          { id: 'l1', title: 'What is Nursing?', type: 'video', duration: '45 min' },
          { id: 'l2', title: 'Nursing Ethics', type: 'pdf' },
          { id: 'l3', title: 'Chapter Notes', type: 'notes' },
        ],
      },
      {
        id: 'ch2',
        title: 'Chapter 2: Fundamentals',
        expanded: false,
        lessons: [],
      },
    ],
  });

  const [showAddChapter, setShowAddChapter] = useState(false);
  const [newChapterTitle, setNewChapterTitle] = useState('');
  const [uploadingChapter, setUploadingChapter] = useState<string | null>(null);

  const toggleChapter = (chapterId: string) => {
    setCourse({
      ...course,
      chapters: course.chapters.map(ch =>
        ch.id === chapterId ? { ...ch, expanded: !ch.expanded } : ch
      ),
    });
  };

  const addChapter = () => {
    if (!newChapterTitle.trim()) return;

    const newChapter: Chapter = {
      id: Date.now().toString(),
      title: newChapterTitle,
      lessons: [],
      expanded: true,
    };

    setCourse({ ...course, chapters: [...course.chapters, newChapter] });
    setNewChapterTitle('');
    setShowAddChapter(false);
  };

  const deleteChapter = (chapterId: string) => {
    if (!window.confirm('Delete this chapter?')) return;
    setCourse({
      ...course,
      chapters: course.chapters.filter(ch => ch.id !== chapterId),
    });
  };

  const handleFileUpload = (chapterId: string, type: 'video' | 'pdf' | 'notes') => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = type === 'video' ? 'video/*' : type === 'pdf' ? 'application/pdf' : '.txt,.pdf';
    input.multiple = true;

    input.onchange = (e: any) => {
      const files = e.target.files;
      if (!files || files.length === 0) return;

      const newLessons: Lesson[] = Array.from(files).map((file: any) => ({
        id: Date.now().toString() + Math.random(),
        title: file.name,
        type,
        url: URL.createObjectURL(file),
        duration: type === 'video' ? '-- min' : undefined,
      }));

      setCourse({
        ...course,
        chapters: course.chapters.map(ch =>
          ch.id === chapterId
            ? { ...ch, lessons: [...ch.lessons, ...newLessons] }
            : ch
        ),
      });

      alert(`✅ ${newLessons.length} ${type}(s) uploaded to chapter!`);
    };

    input.click();
  };

  const deleteLesson = (chapterId: string, lessonId: string) => {
    setCourse({
      ...course,
      chapters: course.chapters.map(ch =>
        ch.id === chapterId
          ? { ...ch, lessons: ch.lessons.filter(l => l.id !== lessonId) }
          : ch
      ),
    });
  };

  const handleSave = async () => {
    try {
      // TODO: Call backend API to save course
      alert('✅ Course saved successfully!');
    } catch (error) {
      alert('❌ Failed to save course');
    }
  };

  const getLessonIcon = (type: string) => {
    switch (type) {
      case 'video': return <Video size={16} className="text-blue-500" />;
      case 'pdf': return <FileText size={16} className="text-red-500" />;
      case 'notes': return <Book size={16} className="text-green-500" />;
      default: return <FileText size={16} />;
    }
  };

  return (
    <Layout title={`Course: ${course.title}`} showBack backTo="/courses">
      <div className="p-6 max-w-5xl mx-auto">
        {/* Course Info */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <h1 className="text-2xl font-bold text-gray-800 mb-2">{course.title}</h1>
              <p className="text-gray-600 text-sm mb-3">{course.description}</p>
              <div className="flex gap-4 text-sm text-gray-500">
                <span>₹{course.price}</span>
                <span>•</span>
                <span>{course.chapters.length} chapters</span>
                <span>•</span>
                <span>
                  {course.chapters.reduce((sum, ch) => sum + ch.lessons.length, 0)} lessons
                </span>
              </div>
            </div>
            <button
              onClick={handleSave}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"
            >
              <Save size={16} />
              Save
            </button>
          </div>
        </div>

        {/* Add Chapter Button */}
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-800">Chapters</h2>
          <button
            onClick={() => setShowAddChapter(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"
          >
            <Plus size={16} />
            Add Chapter
          </button>
        </div>

        {/* Add Chapter Modal */}
        {showAddChapter && (
          <div className="bg-white rounded-lg shadow-md border border-gray-200 p-4 mb-4">
            <input
              type="text"
              value={newChapterTitle}
              onChange={(e) => setNewChapterTitle(e.target.value)}
              placeholder="Chapter title..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg mb-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
              autoFocus
            />
            <div className="flex gap-2">
              <button
                onClick={addChapter}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
              >
                Add
              </button>
              <button
                onClick={() => {
                  setShowAddChapter(false);
                  setNewChapterTitle('');
                }}
                className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300"
              >
                Cancel
              </button>
            </div>
          </div>
        )}

        {/* Chapters List */}
        <div className="space-y-3">
          {course.chapters.map((chapter) => (
            <div
              key={chapter.id}
              className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden"
            >
              {/* Chapter Header */}
              <div className="flex items-center justify-between p-4 bg-gray-50 border-b border-gray-200">
                <button
                  onClick={() => toggleChapter(chapter.id)}
                  className="flex items-center gap-2 flex-1 text-left"
                >
                  {chapter.expanded ? (
                    <ChevronDown size={18} className="text-gray-500" />
                  ) : (
                    <ChevronRight size={18} className="text-gray-500" />
                  )}
                  <span className="font-medium text-gray-800">{chapter.title}</span>
                  <span className="text-xs text-gray-500 ml-2">
                    ({chapter.lessons.length} lessons)
                  </span>
                </button>
                <button
                  onClick={() => deleteChapter(chapter.id)}
                  className="p-1.5 text-red-600 hover:bg-red-50 rounded"
                >
                  <Trash2 size={16} />
                </button>
              </div>

              {/* Chapter Content */}
              {chapter.expanded && (
                <div className="p-4">
                  {/* Upload Buttons */}
                  <div className="flex gap-2 mb-4">
                    <button
                      onClick={() => handleFileUpload(chapter.id, 'video')}
                      className="bg-blue-500 text-white px-3 py-1.5 rounded-lg flex items-center gap-1 text-sm hover:bg-blue-600"
                    >
                      <Video size={14} />
                      Upload Video
                    </button>
                    <button
                      onClick={() => handleFileUpload(chapter.id, 'pdf')}
                      className="bg-red-500 text-white px-3 py-1.5 rounded-lg flex items-center gap-1 text-sm hover:bg-red-600"
                    >
                      <FileText size={14} />
                      Upload PDF
                    </button>
                    <button
                      onClick={() => handleFileUpload(chapter.id, 'notes')}
                      className="bg-green-500 text-white px-3 py-1.5 rounded-lg flex items-center gap-1 text-sm hover:bg-green-600"
                    >
                      <Book size={14} />
                      Upload Notes
                    </button>
                  </div>

                  {/* Lessons List */}
                  {chapter.lessons.length === 0 ? (
                    <p className="text-gray-400 text-sm text-center py-4">
                      No lessons yet. Upload video, PDF, or notes.
                    </p>
                  ) : (
                    <div className="space-y-2">
                      {chapter.lessons.map((lesson) => (
                        <div
                          key={lesson.id}
                          className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100"
                        >
                          <div className="flex items-center gap-3 flex-1">
                            {getLessonIcon(lesson.type)}
                            <span className="text-sm text-gray-700">{lesson.title}</span>
                            {lesson.duration && (
                              <span className="text-xs text-gray-500">
                                {lesson.duration}
                              </span>
                            )}
                          </div>
                          <div className="flex gap-2">
                            {lesson.url && (
                              <button
                                onClick={() => window.open(lesson.url, '_blank')}
                                className="p-1.5 text-blue-600 hover:bg-blue-100 rounded"
                                title="Open"
                              >
                                <Play size={14} />
                              </button>
                            )}
                            <button
                              onClick={() => deleteLesson(chapter.id, lesson.id)}
                              className="p-1.5 text-red-600 hover:bg-red-100 rounded"
                              title="Delete"
                            >
                              <X size={14} />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
}