import { useState, useRef } from 'react';
import Layout from '../components/Layout';
import {
  Plus, Search, FileText, Trash2, Upload, X, Save, Edit
} from 'lucide-react';

interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
}

export default function Notes() {
  const [notes, setNotes] = useState<Note[]>([
    {
      id: '1',
      title: 'Cardiovascular System Notes',
      content: 'Heart anatomy, blood circulation, cardiac cycle...',
      createdAt: new Date().toISOString(),
    },
    {
      id: '2',
      title: 'Pharmacology Quick Revision',
      content: 'Drug classifications, mechanisms of action...',
      createdAt: new Date().toISOString(),
    },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [showEditor, setShowEditor] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);
  const [formData, setFormData] = useState({ title: '', content: '' });

  const handleNew = () => {
    setEditingNote(null);
    setFormData({ title: '', content: '' });
    setShowEditor(true);
  };

  const handleEdit = (note: Note) => {
    setEditingNote(note);
    setFormData({ title: note.title, content: note.content });
    setShowEditor(true);
  };

  const handleSave = () => {
    if (!formData.title.trim()) return;

    if (editingNote) {
      setNotes(notes.map(n =>
        n.id === editingNote.id
          ? { ...n, title: formData.title, content: formData.content }
          : n
      ));
    } else {
      const newNote: Note = {
        id: Date.now().toString(),
        title: formData.title,
        content: formData.content,
        createdAt: new Date().toISOString(),
      };
      setNotes([newNote, ...notes]);
    }

    setShowEditor(false);
    setEditingNote(null);
    setFormData({ title: '', content: '' });
  };

  const handleDelete = (id: string) => {
    if (!window.confirm('Delete this note?')) return;
    setNotes(notes.filter(n => n.id !== id));
  };

  const handleUploadPDF = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf';
    input.onchange = (e: any) => {
      const file = e.target.files?.[0];
      if (file) {
        const newNote: Note = {
          id: Date.now().toString(),
          title: file.name,
          content: 'PDF uploaded — view in PDF Viewer',
          createdAt: new Date().toISOString(),
        };
        setNotes([newNote, ...notes]);
        alert('✅ PDF uploaded!');
      }
    };
    input.click();
  };

  const filteredNotes = notes.filter(n =>
    n.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    n.content.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Layout title="Notes" showBack backTo="/classroom">
      <div className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex-1 max-w-md relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <input
              type="text"
              placeholder="Search notes..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleUploadPDF}
              className="bg-purple-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-purple-700"
            >
              <Upload size={18} />
              Upload PDF
            </button>
            <button
              onClick={handleNew}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700"
            >
              <Plus size={18} />
              New Note
            </button>
          </div>
        </div>

        {/* Editor Modal */}
        {showEditor && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
              <div className="p-6">
                <h2 className="text-xl font-bold mb-4">
                  {editingNote ? 'Edit Note' : 'New Note'}
                </h2>

                <input
                  type="text"
                  placeholder="Note title..."
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg mb-3 focus:outline-none focus:ring-2 focus:ring-blue-500 font-semibold"
                  autoFocus
                />

                <textarea
                  placeholder="Write your notes here... (markdown supported)"
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 font-mono text-sm"
                  rows={15}
                />

                <div className="flex gap-3 pt-4">
                  <button
                    onClick={handleSave}
                    className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 flex items-center justify-center gap-2"
                  >
                    <Save size={18} />
                    Save Note
                  </button>
                  <button
                    onClick={() => setShowEditor(false)}
                    className="flex-1 bg-gray-200 text-gray-700 py-2 rounded-lg hover:bg-gray-300"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Notes Grid */}
        {filteredNotes.length === 0 ? (
          <div className="text-center py-16">
            <FileText size={64} className="mx-auto text-gray-300 mb-3" />
            <p className="text-gray-500">No notes yet</p>
            <button
              onClick={handleNew}
              className="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg"
            >
              Create Your First Note
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredNotes.map((note) => (
              <div
                key={note.id}
                className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 hover:shadow-md transition-shadow"
              >
                <div className="flex items-start justify-between mb-2">
                  <h3 className="font-semibold text-gray-800 text-sm flex-1">{note.title}</h3>
                  <div className="flex gap-1">
                    <button
                      onClick={() => handleEdit(note)}
                      className="p-1 text-blue-600 hover:bg-blue-50 rounded"
                    >
                      <Edit size={14} />
                    </button>
                    <button
                      onClick={() => handleDelete(note.id)}
                      className="p-1 text-red-600 hover:bg-red-50 rounded"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
                <p className="text-xs text-gray-600 line-clamp-4">
                  {note.content.substring(0, 150)}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
}