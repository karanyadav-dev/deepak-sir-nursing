import { useState } from 'react';
import Layout from '../components/Layout';
import ThreeDViewer from '../components/ThreeDViewer';
import { Box, Upload, Trash2, RotateCw } from 'lucide-react';

interface ModelFile {
  id: string;
  name: string;
  url: string;
}

export default function Models() {
  const [models, setModels] = useState<ModelFile[]>([]);
  const [activeModel, setActiveModel] = useState<ModelFile | null>(null);

  const handleUpload = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.glb,.gltf,.obj,.fbx';
    input.multiple = true;
    input.onchange = (e: any) => {
      const files = e.target.files;
      if (!files) return;

      const newModels: ModelFile[] = [];
      Array.from(files).forEach((file: any) => {
        newModels.push({
          id: Date.now().toString() + Math.random(),
          name: file.name,
          url: URL.createObjectURL(file),
        });
      });

      setModels([...newModels, ...models]);
      if (newModels.length > 0) setActiveModel(newModels[0]);
      alert(`✅ ${newModels.length} 3D model(s) uploaded!`);
    };
    input.click();
  };

  const handleDelete = (id: string) => {
    if (!window.confirm('Delete this model?')) return;
    setModels(models.filter(m => m.id !== id));
    if (activeModel?.id === id) setActiveModel(null);
  };

  return (
    <Layout title="3D Models" showBack backTo="/classroom">
      <div className="h-full flex">
        {/* Models List */}
        <div className="w-64 bg-white border-r border-gray-200 p-3 flex flex-col">
          <button
            onClick={handleUpload}
            className="w-full bg-purple-600 text-white px-4 py-2 rounded-lg flex items-center justify-center gap-2 mb-3 hover:bg-purple-700"
          >
            <Upload size={16} />
            Upload 3D
          </button>

          <div className="flex-1 overflow-y-auto space-y-2">
            {models.length === 0 ? (
              <p className="text-xs text-gray-500 text-center py-8">
                No 3D models uploaded
              </p>
            ) : (
              models.map((model) => (
                <div
                  key={model.id}
                  onClick={() => setActiveModel(model)}
                  className={`flex items-center gap-2 p-2 rounded-lg cursor-pointer ${
                    activeModel?.id === model.id ? 'bg-purple-100' : 'hover:bg-gray-100'
                  }`}
                >
                  <Box size={16} className="text-purple-500 flex-shrink-0" />
                  <p className="text-xs font-medium text-gray-800 truncate flex-1">
                    {model.name}
                  </p>
                  <button
                    onClick={(e) => { e.stopPropagation(); handleDelete(model.id); }}
                    className="text-red-500 hover:bg-red-50 p-1 rounded"
                  >
                    <Trash2 size={12} />
                  </button>
                </div>
              ))
            )}
          </div>
        </div>

        {/* 3D Viewer */}
        <div className="flex-1 bg-gradient-to-br from-blue-900 to-purple-900 relative">
          {activeModel ? (
            <>
              <ThreeDViewer modelUrl={activeModel.url} />
              <div className="absolute bottom-4 left-4 bg-purple-600 bg-opacity-90 text-white px-4 py-2 rounded-lg text-xs">
                🖱️ Drag to rotate • Scroll to zoom
              </div>
              <div className="absolute top-4 right-4 bg-white bg-opacity-90 text-gray-800 px-4 py-2 rounded-lg text-xs font-medium">
                {activeModel.name}
              </div>
            </>
          ) : (
            <div className="h-full flex items-center justify-center">
              <div className="text-center text-white">
                <Box size={80} className="mx-auto mb-4 text-purple-400" />
                <p className="text-gray-300 mb-4">No 3D model selected</p>
                <button
                  onClick={handleUpload}
                  className="bg-purple-600 text-white px-6 py-2 rounded-lg hover:bg-purple-700"
                >
                  Upload 3D Model
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}