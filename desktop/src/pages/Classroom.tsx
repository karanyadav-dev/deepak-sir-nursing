import { useState, useRef, useEffect } from 'react';
import Layout from '../components/Layout';
import ThreeDViewer from '../components/ThreeDViewer';
import {
  Presentation, FileText, Box, Video, PenTool,
  Save, Undo, Redo, Trash2, Download, Upload,
  Highlighter, Eraser, X, StickyNote, Eye, EyeOff,
  Move, RotateCw
} from 'lucide-react';

type BackgroundType = 'none' | 'ppt' | 'pdf' | 'video' | '3d';

interface UploadedFile {
  id: string;
  name: string;
  type: BackgroundType;
  url: string;
}

export default function Classroom() {
  const [backgroundType, setBackgroundType] = useState<BackgroundType>('none');
  const [uploadedFiles, setUploadedFiles] = useState<UploadedFile[]>([]);
  const [activeFile, setActiveFile] = useState<UploadedFile | null>(null);

  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [tool, setTool] = useState<'pen' | 'highlighter' | 'eraser' | 'none'>('pen');
  const [penColor, setPenColor] = useState('#ff0000');
  const [penSize, setPenSize] = useState(3);
  const [history, setHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);

  const [is3DMode, setIs3DMode] = useState(false);
  const [enable3DRotation, setEnable3DRotation] = useState(true);
  const [annotationsVisible, setAnnotationsVisible] = useState(true);
  const [backgroundVisible, setBackgroundVisible] = useState(true);

  const [notes, setNotes] = useState('');
  const [showNotes, setShowNotes] = useState(false);
  const [timerSeconds, setTimerSeconds] = useState(0);
  const [timerRunning, setTimerRunning] = useState(false);
  const [zoom] = useState(100);

  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (timerRunning) {
      interval = setInterval(() => setTimerSeconds(s => s + 1), 1000);
    }
    return () => clearInterval(interval);
  }, [timerRunning]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.clearRect(0, 0, canvas.width, canvas.height);
  }, []);

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  };

  const getFileCategory = (filename: string): BackgroundType => {
    const ext = filename.split('.').pop()?.toLowerCase();
    if (['ppt', 'pptx'].includes(ext || '')) return 'ppt';
    if (ext === 'pdf') return 'pdf';
    if (['glb', 'gltf', 'obj', 'fbx'].includes(ext || '')) return '3d';
    if (['mp4', 'webm', 'mkv'].includes(ext || '')) return 'video';
    return 'none';
  };

  const handleUploadClick = () => fileInputRef.current?.click();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const newFiles: UploadedFile[] = [];
    Array.from(files).forEach((file) => {
      const type = getFileCategory(file.name);
      newFiles.push({
        id: Date.now().toString() + Math.random(),
        name: file.name,
        type,
        url: URL.createObjectURL(file),
      });
    });

    setUploadedFiles([...newFiles, ...uploadedFiles]);
    if (newFiles.length > 0) {
      setActiveFile(newFiles[0]);
      setBackgroundType(newFiles[0].type);
      setIs3DMode(newFiles[0].type === '3d');
    }
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const openFile = (file: UploadedFile) => {
    setActiveFile(file);
    setBackgroundType(file.type);
    setIs3DMode(file.type === '3d');
  };

  const closeBackground = () => {
    setActiveFile(null);
    setBackgroundType('none');
    setIs3DMode(false);
  };

  const startDrawing = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (tool === 'none') return;
    if (is3DMode) setEnable3DRotation(false);

    const canvas = canvasRef.current;
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.beginPath();
    ctx.moveTo(x, y);
    setIsDrawing(true);
  };

  const draw = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!isDrawing || tool === 'none') return;
    const canvas = canvasRef.current;
    if (!canvas) return;

    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    const x = (e.clientX - rect.left) * scaleX;
    const y = (e.clientY - rect.top) * scaleY;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.lineTo(x, y);

    if (tool === 'eraser') {
      ctx.globalCompositeOperation = 'destination-out';
      ctx.lineWidth = penSize * 5;
    } else if (tool === 'highlighter') {
      ctx.globalCompositeOperation = 'source-over';
      ctx.strokeStyle = penColor;
      ctx.lineWidth = penSize * 5;
      ctx.globalAlpha = 0.3;
    } else {
      ctx.globalCompositeOperation = 'source-over';
      ctx.strokeStyle = penColor;
      ctx.lineWidth = penSize;
      ctx.globalAlpha = 1;
    }

    ctx.stroke();
  };

  const stopDrawing = () => {
    if (!isDrawing) return;
    setIsDrawing(false);
    if (is3DMode) setEnable3DRotation(true);

    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (ctx) ctx.globalAlpha = 1;
    setHistory([...history, canvas.toDataURL()]);
    setHistoryIndex(history.length);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx?.clearRect(0, 0, canvas.width, canvas.height);
    setHistory([]);
    setHistoryIndex(-1);
  };

  const undo = () => {
    if (historyIndex <= 0) return;
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;
    const img = new Image();
    img.src = history[historyIndex - 1];
    img.onload = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.drawImage(img, 0, 0);
    };
    setHistoryIndex(historyIndex - 1);
  };

  const redo = () => {
    if (historyIndex >= history.length - 1) return;
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (!canvas || !ctx) return;
    const img = new Image();
    img.src = history[historyIndex + 1];
    img.onload = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.drawImage(img, 0, 0);
    };
    setHistoryIndex(historyIndex + 1);
  };

  const saveBoard = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const link = document.createElement('a');
    link.download = `classroom-${Date.now()}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();
  };

  return (
    <Layout title="Classroom" showBack>
      <div className="h-full flex flex-col bg-gray-900 text-white">
        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept=".ppt,.pptx,.pdf,.glb,.gltf,.obj,.fbx,.mp4,.webm,.mkv"
          onChange={handleFileChange}
          className="hidden"
        />

        <div className="flex items-center justify-between px-4 py-2 bg-gray-800 border-b border-gray-700">
          <div className="flex items-center gap-3">
            <span className="font-bold text-blue-400">🎓 Digital Classroom</span>
            {activeFile && (
              <span className="text-xs text-gray-400 px-2 py-1 bg-gray-700 rounded">
                {activeFile.name}
              </span>
            )}
            {is3DMode && (
              <span className="text-xs text-purple-400 px-2 py-1 bg-purple-900 rounded">
                🧊 3D Mode
              </span>
            )}
          </div>
          <div className="flex items-center gap-2">
            <div className="flex items-center gap-2 px-3 py-1.5 bg-gray-700 rounded-md">
              <span className="font-mono text-sm">{formatTime(timerSeconds)}</span>
              <button onClick={() => setTimerRunning(!timerRunning)} className="text-xs hover:text-blue-400">
                {timerRunning ? 'Pause' : 'Start'}
              </button>
              <button onClick={() => { setTimerSeconds(0); setTimerRunning(false); }} className="text-xs hover:text-red-400">
                Reset
              </button>
            </div>

            {is3DMode && (
              <button
                onClick={() => setEnable3DRotation(!enable3DRotation)}
                className={`px-3 py-1.5 rounded-md text-xs flex items-center gap-1 ${
                  enable3DRotation ? 'bg-purple-600' : 'bg-gray-700'
                }`}
              >
                <RotateCw size={14} />
                {enable3DRotation ? 'Rotate ON' : 'Rotate OFF'}
              </button>
            )}

            <button
              onClick={() => setAnnotationsVisible(!annotationsVisible)}
              className={`p-2 rounded ${annotationsVisible ? 'bg-blue-600' : 'bg-gray-700'}`}
            >
              {annotationsVisible ? <Eye size={16} /> : <EyeOff size={16} />}
            </button>

            <button
              onClick={() => setShowNotes(!showNotes)}
              className="px-3 py-1.5 bg-gray-700 hover:bg-gray-600 rounded-md text-sm flex items-center gap-1"
            >
              <StickyNote size={14} /> Notes
            </button>
          </div>
        </div>

        <div className="flex-1 flex overflow-hidden">
          <div style={{ position: 'relative', flex: 1, overflow: 'hidden', backgroundColor: '#1a1a1a' }}>
            {backgroundVisible && is3DMode && activeFile && (
              <div
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  zIndex: 1,
                  pointerEvents: enable3DRotation ? 'auto' : 'none',
                }}
              >
                <ThreeDViewer modelUrl={activeFile.url} />
              </div>
            )}

            {backgroundVisible && !is3DMode && activeFile && (
              <div
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  pointerEvents: 'none',
                  zIndex: 1,
                }}
              >
                {backgroundType === 'ppt' && (
                  <div
                    style={{
                      width: '95%',
                      height: '95%',
                      backgroundColor: 'white',
                      borderRadius: 8,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: '#333',
                      transform: `scale(${zoom / 100})`,
                    }}
                  >
                    <div style={{ textAlign: 'center', padding: 40 }}>
                      <Presentation size={80} style={{ margin: '0 auto 16px', color: '#f97316' }} />
                      <p style={{ fontSize: 24, fontWeight: 'bold', color: '#1f2937' }}>
                        {activeFile.name}
                      </p>
                    </div>
                  </div>
                )}

                {backgroundType === 'pdf' && (
                  <iframe
                    src={activeFile.url}
                    style={{
                      width: '95%',
                      height: '95%',
                      border: 'none',
                      borderRadius: 8,
                    }}
                    title="PDF"
                  />
                )}

                {backgroundType === 'video' && (
                  <video
                    src={activeFile.url}
                    style={{ maxWidth: '95%', maxHeight: '95%', borderRadius: 8 }}
                    autoPlay
                    loop
                    muted
                  />
                )}
              </div>
            )}

            {annotationsVisible && (
              <canvas
                ref={canvasRef}
                width={1920}
                height={1080}
                onMouseDown={startDrawing}
                onMouseMove={draw}
                onMouseUp={stopDrawing}
                onMouseLeave={stopDrawing}
                style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%',
                  height: '100%',
                  cursor: tool === 'eraser' ? 'cell' : tool === 'none' ? 'default' : 'crosshair',
                  pointerEvents: tool === 'none' ? 'none' : 'auto',
                  zIndex: 10,
                  backgroundColor: 'transparent',
                }}
              />
            )}

            {is3DMode && enable3DRotation && (
              <div
                style={{
                  position: 'absolute',
                  bottom: 16,
                  left: 16,
                  backgroundColor: 'rgba(147, 51, 234, 0.9)',
                  color: 'white',
                  padding: '8px 16px',
                  borderRadius: 8,
                  fontSize: 12,
                  zIndex: 20,
                }}
              >
                🖱️ Drag to rotate 3D • Scroll to zoom
              </div>
            )}
          </div>

          {showNotes && (
            <div className="w-80 bg-gray-800 border-l border-gray-700 flex flex-col">
              <div className="flex items-center justify-between p-3 border-b border-gray-700">
                <span className="font-semibold">📝 Class Notes</span>
                <button onClick={() => setShowNotes(false)} className="text-gray-400 hover:text-white">
                  <X size={16} />
                </button>
              </div>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Type class notes..."
                className="flex-1 p-3 bg-gray-900 text-white resize-none focus:outline-none"
              />
            </div>
          )}
        </div>

        <div className="bg-gray-800 border-t border-gray-700">
          <div className="flex items-center gap-2 px-4 py-2 overflow-x-auto border-b border-gray-700">
            <button
              onClick={() => setTool('none')}
              className={`p-2 rounded ${tool === 'none' ? 'bg-blue-600' : 'bg-gray-700'}`}
            >
              <Move size={16} />
            </button>
            <button
              onClick={() => setTool('pen')}
              className={`p-2 rounded ${tool === 'pen' ? 'bg-blue-600' : 'bg-gray-700'}`}
            >
              <PenTool size={16} />
            </button>
            <button
              onClick={() => setTool('highlighter')}
              className={`p-2 rounded ${tool === 'highlighter' ? 'bg-blue-600' : 'bg-gray-700'}`}
            >
              <Highlighter size={16} />
            </button>
            <button
              onClick={() => setTool('eraser')}
              className={`p-2 rounded ${tool === 'eraser' ? 'bg-blue-600' : 'bg-gray-700'}`}
            >
              <Eraser size={16} />
            </button>

            <div className="border-l border-gray-600 h-6 mx-1" />

            {['#000000', '#ff0000', '#00ff00', '#0000ff', '#ffff00', '#ff00ff', '#ffffff'].map((color) => (
              <button
                key={color}
                onClick={() => setPenColor(color)}
                className={`w-7 h-7 rounded-full border-2 ${penColor === color ? 'border-white' : 'border-gray-600'}`}
                style={{ backgroundColor: color }}
              />
            ))}
            <input
              type="color"
              value={penColor}
              onChange={(e) => setPenColor(e.target.value)}
              className="w-7 h-7 rounded cursor-pointer"
            />

            <div className="border-l border-gray-600 h-6 mx-1" />

            <input
              type="range"
              min="1"
              max="20"
              value={penSize}
              onChange={(e) => setPenSize(Number(e.target.value))}
              className="w-20"
            />
            <span className="text-xs w-8">{penSize}px</span>

            <div className="border-l border-gray-600 h-6 mx-1" />

            <button onClick={undo} className="p-2 rounded bg-gray-700 hover:bg-gray-600">
              <Undo size={16} />
            </button>
            <button onClick={redo} className="p-2 rounded bg-gray-700 hover:bg-gray-600">
              <Redo size={16} />
            </button>
            <button onClick={clearCanvas} className="p-2 rounded bg-red-600 hover:bg-red-700">
              <Trash2 size={16} />
            </button>
            <button onClick={saveBoard} className="p-2 rounded bg-green-600 hover:bg-green-700">
              <Download size={16} />
            </button>
          </div>

          <div className="flex items-center gap-2 px-4 py-2">
            <button
              onClick={handleUploadClick}
              className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 rounded-lg text-sm font-medium"
            >
              <Upload size={16} />
              Upload File
            </button>

            <div className="flex gap-2 overflow-x-auto flex-1">
              {uploadedFiles.map((file) => {
                const Icon = file.type === 'ppt' ? Presentation :
                             file.type === 'pdf' ? FileText :
                             file.type === '3d' ? Box :
                             file.type === 'video' ? Video : FileText;
                return (
                  <button
                    key={file.id}
                    onClick={() => openFile(file)}
                    className={`flex items-center gap-2 px-3 py-1.5 rounded-md text-xs whitespace-nowrap ${
                      activeFile?.id === file.id ? 'bg-blue-600' : 'bg-gray-700 hover:bg-gray-600'
                    }`}
                  >
                    <Icon size={12} />
                    {file.name.substring(0, 25)}
                  </button>
                );
              })}
            </div>

            {activeFile && (
              <button
                onClick={closeBackground}
                className="px-3 py-2 bg-red-600 hover:bg-red-700 rounded-lg text-sm"
              >
                <X size={14} className="inline" /> Close
              </button>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
}