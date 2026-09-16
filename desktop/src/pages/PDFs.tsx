import { useState } from 'react';
import Layout from '../components/Layout';
import {
  FileText, Upload, Trash2, X, ZoomIn, ZoomOut, ChevronLeft, ChevronRight
} from 'lucide-react';

interface PDFFile {
  id: string;
  name: string;
  url: string;
  size: number;
}

export default function PDFs() {
  const [pdfs, setPdfs] = useState<PDFFile[]>([]);
  const [activePDF, setActivePDF] = useState<PDFFile | null>(null);
  const [zoom, setZoom] = useState(100);

  const handleUpload = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf';
    input.multiple = true;
    input.onchange = (e: any) => {
      const files = e.target.files;
      if (!files) return;

      const newPDFs: PDFFile[] = [];
      Array.from(files).forEach((file: any) => {
        newPDFs.push({
          id: Date.now().toString() + Math.random(),
          name: file.name,
          url: URL.createObjectURL(file),
          size: file.size,
        });
      });

      setPdfs([...newPDFs, ...pdfs]);
      if (newPDFs.length > 0) setActivePDF(newPDFs[0]);
      alert(`✅ ${newPDFs.length} PDF(s) uploaded!`);
    };
    input.click();
  };

  const handleDelete = (id: string) => {
    if (!window.confirm('Delete this PDF?')) return;
    setPdfs(pdfs.filter(p => p.id !== id));
    if (activePDF?.id === id) setActivePDF(null);
  };

  const formatSize = (bytes: number) => {
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <Layout title="PDF Viewer" showBack backTo="/classroom">
      <div className="h-full flex">
        {/* PDF List Sidebar */}
        <div className="w-64 bg-white border-r border-gray-200 p-3 flex flex-col">
          <button
            onClick={handleUpload}
            className="w-full bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center justify-center gap-2 mb-3 hover:bg-blue-700"
          >
            <Upload size={16} />
            Upload PDF
          </button>

          <div className="flex-1 overflow-y-auto space-y-2">
            {pdfs.length === 0 ? (
              <p className="text-xs text-gray-500 text-center py-8">
                No PDFs uploaded
              </p>
            ) : (
              pdfs.map((pdf) => (
                <div
                  key={pdf.id}
                  onClick={() => setActivePDF(pdf)}
                  className={`flex items-center gap-2 p-2 rounded-lg cursor-pointer ${
                    activePDF?.id === pdf.id ? 'bg-blue-100' : 'hover:bg-gray-100'
                  }`}
                >
                  <FileText size={16} className="text-red-500 flex-shrink-0" />
                  <div className="flex-1 min-w-0">
                    <p className="text-xs font-medium text-gray-800 truncate">{pdf.name}</p>
                    <p className="text-xs text-gray-500">{formatSize(pdf.size)}</p>
                  </div>
                  <button
                    onClick={(e) => { e.stopPropagation(); handleDelete(pdf.id); }}
                    className="text-red-500 hover:bg-red-50 p-1 rounded"
                  >
                    <Trash2 size={12} />
                  </button>
                </div>
              ))
            )}
          </div>
        </div>

        {/* PDF Viewer */}
        <div className="flex-1 bg-gray-100 flex flex-col">
          {activePDF ? (
            <>
              {/* Toolbar */}
              <div className="bg-white border-b border-gray-200 p-2 flex items-center justify-between">
                <span className="text-sm font-medium">{activePDF.name}</span>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setZoom(Math.max(50, zoom - 10))}
                    className="p-2 bg-gray-100 rounded hover:bg-gray-200"
                  >
                    <ZoomOut size={16} />
                  </button>
                  <span className="text-sm">{zoom}%</span>
                  <button
                    onClick={() => setZoom(Math.min(200, zoom + 10))}
                    className="p-2 bg-gray-100 rounded hover:bg-gray-200"
                  >
                    <ZoomIn size={16} />
                  </button>
                </div>
              </div>

              {/* PDF Content */}
              <div className="flex-1 overflow-auto p-4">
                <iframe
                  src={activePDF.url}
                  style={{
                    width: '100%',
                    height: '100%',
                    minHeight: '800px',
                    border: 'none',
                    transform: `scale(${zoom / 100})`,
                    transformOrigin: 'top left',
                  }}
                  title={activePDF.name}
                />
              </div>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center">
              <div className="text-center">
                <FileText size={80} className="mx-auto text-gray-300 mb-4" />
                <p className="text-gray-500 mb-4">No PDF selected</p>
                <button
                  onClick={handleUpload}
                  className="bg-blue-600 text-white px-6 py-2 rounded-lg"
                >
                  Upload PDF
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}