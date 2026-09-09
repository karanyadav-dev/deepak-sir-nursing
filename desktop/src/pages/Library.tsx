import { useState } from 'react';
import { Search, FileText, Video, Box, Image as ImageIcon, StickyNote, Trash2, Download, Plus } from 'lucide-react';

interface LibraryItem {
  id: string;
  name: string;
  type: 'ppt' | 'pdf' | 'video' | '3d' | 'image' | 'notes';
  size: string;
  offline: boolean;
}

export default function Library() {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState<string>('all');
  const [items] = useState<LibraryItem[]>([
    { id: '1', name: 'Cardiovascular System.pptx', type: 'ppt', size: '2.4 MB', offline: true },
    { id: '2', name: 'Heart Anatomy.pdf', type: 'pdf', size: '5.1 MB', offline: true },
    { id: '3', name: 'Heart Model.glb', type: '3d', size: '8.2 MB', offline: true },
    { id: '4', name: 'Blood Flow Animation.mp4', type: 'video', size: '45 MB', offline: false },
    { id: '5', name: 'Anatomy Diagram.png', type: 'image', size: '1.2 MB', offline: true },
  ]);

  const typeIcons = {
    ppt: FileText,
    pdf: FileText,
    video: Video,
    '3d': Box,
    image: ImageIcon,
    notes: StickyNote,
  };

  const filterItems = items.filter((item) => {
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = filterType === 'all' || item.type === filterType;
    return matchesSearch && matchesType;
  });

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow p-4">
        <h1 className="text-2xl font-bold">Teaching Library</h1>
        <p className="text-sm text-gray-500">Manage your teaching content</p>
      </header>

      <main className="p-6">
        {/* Search and Filter */}
        <div className="flex gap-3 mb-6">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <input
              type="text"
              placeholder="Search content..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-3 py-2 border rounded-md"
            />
          </div>
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="px-3 py-2 border rounded-md"
          >
            <option value="all">All Types</option>
            <option value="ppt">PPT</option>
            <option value="pdf">PDF</option>
            <option value="video">Video</option>
            <option value="3d">3D Model</option>
            <option value="image">Image</option>
            <option value="notes">Notes</option>
          </select>
          <button className="bg-blue-600 text-white px-4 py-2 rounded-md flex items-center gap-2">
            <Plus size={16} />
            Import
          </button>
        </div>

        {/* Content Grid */}
        {filterItems.length === 0 ? (
          <div className="text-center py-16">
            <div className="text-4xl mb-3">📂</div>
            <h3 className="text-lg font-semibold text-gray-600">No content found</h3>
            <p className="text-gray-500">Import your teaching materials to get started</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filterItems.map((item) => {
              const Icon = typeIcons[item.type];
              return (
                <div key={item.id} className="bg-white p-4 rounded-lg shadow hover:shadow-md transition-shadow">
                  <div className="flex items-start gap-3">
                    <div className="bg-blue-100 p-2 rounded-lg">
                      <Icon className="text-blue-600" size={20} />
                    </div>
                    <div className="flex-1">
                      <h3 className="font-medium text-sm">{item.name}</h3>
                      <p className="text-xs text-gray-500 mt-1">{item.size}</p>
                    </div>
                  </div>
                  <div className="flex justify-between items-center mt-3 pt-3 border-t">
                    <span className={`text-xs px-2 py-1 rounded ${item.offline ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                      {item.offline ? '✓ Offline' : 'Online Only'}
                    </span>
                    <div className="flex gap-1">
                      {!item.offline && (
                        <button className="p-1 text-blue-600 hover:bg-blue-50 rounded">
                          <Download size={16} />
                        </button>
                      )}
                      <button className="p-1 text-red-600 hover:bg-red-50 rounded">
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
}