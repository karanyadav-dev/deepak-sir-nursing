import { useState, useRef } from 'react';
import Layout from '../components/Layout';
import {
  Search, FileText, Video, Box, Image as ImageIcon,
  StickyNote, Trash2, Download, Plus, Upload
} from 'lucide-react';

interface LibraryItem {
  id: string;
  name: string;
  type: 'ppt' | 'pdf' | 'video' | '3d' | 'image' | 'notes';
  size: string;
  offline: boolean;
  file?: File;
  url?: string;
}

export default function Library() {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState<string>('all');
  const [items, setItems] = useState<LibraryItem[]>([
    { id: '1', name: 'Cardiovascular System.pptx', type: 'ppt', size: '2.4 MB', offline: true },
    { id: '2', name: 'Heart Anatomy.pdf', type: 'pdf', size: '5.1 MB', offline: true },
    { id: '3', name: 'Heart Model.glb', type: '3d', size: '8.2 MB', offline: true },
    { id: '4', name: 'Blood Flow Animation.mp4', type: 'video', size: '45 MB', offline: false },
    { id: '5', name: 'Anatomy Diagram.png', type: 'image', size: '1.2 MB', offline: true },
  ]);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const typeIcons = {
    ppt: FileText,
    pdf: FileText,
    video: Video,
    '3d': Box,
    image: ImageIcon,
    notes: StickyNote,
  };

  const getFileType = (filename: string): LibraryItem['type'] => {
    const ext = filename.split('.').pop()?.toLowerCase();
    if (['ppt', 'pptx'].includes(ext || '')) return 'ppt';
    if (ext === 'pdf') return 'pdf';
    if (['mp4', 'webm', 'mkv'].includes(ext || '')) return 'video';
    if (['glb', 'gltf'].includes(ext || '')) return '3d';
    if (['png', 'jpg', 'jpeg'].includes(ext || '')) return 'image';
    return 'notes';
  };

  const formatSize = (bytes: number): string => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  const handleUploadClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const newItems: LibraryItem[] = [];
    Array.from(files).forEach((file) => {
      const newItem: LibraryItem = {
        id: Date.now().toString() + Math.random(),
        name: file.name,
        type: getFileType(file.name),
        size: formatSize(file.size),
        offline: true,
        file: file,
        url: URL.createObjectURL(file),
      };
      newItems.push(newItem);
    });

    setItems([...newItems, ...items]);
    alert(`✅ ${newItems.length} file(s) uploaded successfully!`);

    // Reset input
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleDelete = (id: string) => {
    if (!window.confirm('Delete this file?')) return;
    setItems(items.filter((item) => item.id !== id));
  };

  const handleDownload = (item: LibraryItem) => {
    if (item.url) {
      const link = document.createElement('a');
      link.href = item.url;
      link.download = item.name;
      link.click();
    } else {
      alert('Demo file — download not available');
    }
  };

  const filterItems = items.filter((item) => {
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = filterType === 'all' || item.type === filterType;
    return matchesSearch && matchesType;
  });

  return (
    <Layout title="Teaching Library" showBack>
      <div className="p-6">
        {/* Hidden File Input */}
        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept=".ppt,.pptx,.pdf,.mp4,.webm,.glb,.gltf,.png,.jpg,.jpeg"
          onChange={handleFileChange}
          className="hidden"
        />

        {/* Search and Filter */}
        <div className="flex gap-3 mb-6">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <input
              type="text"
              placeholder="Search content..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="all">All Types</option>
            <option value="ppt">PPT</option>
            <option value="pdf">PDF</option>
            <option value="video">Video</option>
            <option value="3d">3D Model</option>
            <option value="image">Image</option>
            <option value="notes">Notes</option>
          </select>
          <button
            onClick={handleUploadClick}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700 transition-colors"
          >
            <Upload size={16} />
            Import
          </button>
        </div>

        {/* Content Grid */}
        {filterItems.length === 0 ? (
          <div className="text-center py-16">
            <div className="text-6xl mb-3">📂</div>
            <h3 className="text-lg font-semibold text-gray-600">No content found</h3>
            <p className="text-gray-500 mt-2">Import your teaching materials to get started</p>
            <button
              onClick={handleUploadClick}
              className="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 inline-flex items-center gap-2"
            >
              <Plus size={16} />
              Import Files
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filterItems.map((item) => {
              const Icon = typeIcons[item.type];
              return (
                <div
                  key={item.id}
                  className="bg-white p-4 rounded-xl shadow-sm hover:shadow-md transition-shadow border border-gray-200"
                >
                  <div className="flex items-start gap-3 mb-3">
                    <div className="bg-blue-100 p-3 rounded-lg">
                      <Icon className="text-blue-600" size={22} />
                    </div>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-medium text-sm text-gray-800 truncate" title={item.name}>
                        {item.name}
                      </h3>
                      <p className="text-xs text-gray-500 mt-1">{item.size}</p>
                    </div>
                  </div>
                  <div className="flex justify-between items-center pt-3 border-t border-gray-100">
                    <span
                      className={`text-xs px-2 py-1 rounded ${
                        item.offline
                          ? 'bg-green-100 text-green-700'
                          : 'bg-yellow-100 text-yellow-700'
                      }`}
                    >
                      {item.offline ? '✓ Offline' : 'Online Only'}
                    </span>
                    <div className="flex gap-1">
                      {item.url && (
                        <button
                          onClick={() => handleDownload(item)}
                          className="p-1.5 text-blue-600 hover:bg-blue-50 rounded"
                          title="Download"
                        >
                          <Download size={16} />
                        </button>
                      )}
                      <button
                        onClick={() => handleDelete(item.id)}
                        className="p-1.5 text-red-600 hover:bg-red-50 rounded"
                        title="Delete"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </Layout>
  );
}