import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import {
  Presentation, FileText, Box, Video, PenTool,
  Save, Undo, Redo, Trash2, Download, Maximize,
  Type, Highlighter, Eraser, MousePointer2, Circle, Square,
  Minus, ArrowRight, ZoomIn, ZoomOut, RotateCw, Play, Pause, Volume2
} from 'lucide-react';

type ContentType = 'ppt' | 'pdf' | '3d' | 'video' | 'whiteboard';

export default function Classroom() {
  const navigate = useNavigate();
  const [contentType, setContentType] = useState<ContentType>('whiteboard');
  const [currentSlide, setCurrentSlide] = useState(1);
  const [totalSlides] = useState(20);
  const [zoom, setZoom] = useState(100);
  const [fullscreen, setFullscreen] = useState(false);
  const [notes, setNotes] = useState('');
  const [showNotes, setShowNotes] = useState(false);

  // Whiteboard state
  const [penColor, setPenColor] = useState('#000000');
  const [penSize, setPenSize] = useState(3);
  const [tool, setTool] = useState<'pen' | 'eraser' | 'highlighter'>('pen');
  const [isDrawing, setIsDrawing] = useState(false);
  const [history, setHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);

  // Video state
  const [isPlaying, setIsPlaying] = useState(false);

  // 3D state
  const [rotation, setRotation] = useState(0);

  const canvasRef = useRef<HTMLCanvasElement>(null);

  const handleBack = () => {
    if (window.confirm('Exit classroom?')) {
      navigate('/dashboard');
    }
  };

  const handleNext = () => {
    if (currentSlide < totalSlides) setCurrentSlide(currentSlide + 1);
  };

  const handlePrev = () => {
    if (currentSlide > 1) setCurrentSlide(currentSlide - 1);
  };

  const handleZoomIn = () => setZoom(Math.min(200, zoom + 10));
  const handleZoomOut = () => setZoom(Math.max(50, zoom - 10));

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx?.clearRect(0, 0, canvas.width, canvas.height);
    setHistory([]);
    setHistoryIndex(-1);
  };

  const handleSaveBoard = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const url = canvas.toDataURL('image/png');
    const link = document.createElement('a');
    link.download = `whiteboard-${Date.now()}.png`;
    link.href = url;
    link.click();
  };

  const startDrawing = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (tool !== 'pen' && tool !== 'eraser' && tool !== 'highlighter') return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.beginPath();
    ctx.moveTo(x, y);
    setIsDrawing(true);
  };

  const draw = (e: React.MouseEvent<HTMLCanvasElement>) => {
    if (!isDrawing) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.lineTo(x, y);
    if (tool === 'eraser') {
      ctx.strokeStyle = '#ffffff';
      ctx.lineWidth = 20;
    } else if (tool === 'highlighter') {
      ctx.strokeStyle = '#ffff00';
      ctx.lineWidth = 20;
      ctx.globalAlpha = 0.3;
    } else {
      ctx.strokeStyle = penColor;
      ctx.lineWidth = penSize;
      ctx.globalAlpha = 1;
    }
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.stroke();
  };

  const stopDrawing = () => {
    if (!isDrawing) return;
    setIsDrawing(false);
    const canvas = canvasRef.current;
    if (!canvas) return;
    setHistory([...history, canvas.toDataURL()]);
    setHistoryIndex(history.length);
  };

  const handleUndo = () => {
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

  const handleRedo = () => {
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

  return (
    <Layout title="Classroom" showBack>
      <div className="h-full flex flex-col bg-gray-900 text-white">
        {/* TOP TOOLBAR */}
        <div className="flex items-center justify-between px-4 py-2 bg-gray-800 border-b border-gray-700">
          <div className="flex items-center gap-2">
            <button
              onClick={() => setContentType('ppt')}
              className={`flex items-center gap-1 px-3 py-1.5 rounded-md text-sm ${
                contentType === 'ppt' ? 'bg-blue-600' : 'bg-gray-700 hover:bg-gray-600'
              }`}
            >
              <Presentation size={16} /> PPT
            </button>
            <button
              onClick={() => setContentType('pdf')}
              className={`flex items-center gap-1 px-3 py-1.5 rounded-md text-sm ${
                contentType === 'pdf' ? 'bg-blue-600' : 'bg-gray-700 hover:bg-gray-600'
              }`}
            >
              <FileText size={16} /> PDF
            </button>
            <button
              onClick={() => setContentType('3d')}
              className={`flex items-center gap-1 px-3 py-1.5 rounded-md text-sm ${
                contentType === '3d' ? 'bg-blue-600' : 'bg-gray-700 hover:bg-gray-600'
              }`}
            >
              <Box size={16} /> 3D
            </button>
            <button
              onClick={() => setContentType('video')}
              className={`flex items-center gap-1 px-3 py-1.5 rounded-md text-sm ${
                contentType === 'video' ? 'bg-blue-600' : 'bg-gray-700 hover:bg-gray-600'
              }`}
            >
              <Video size={16} /> Video
            </button>
            <button
              onClick={() => setContentType('whiteboard')}
              className={`flex items-center gap-1 px-3 py-1.5 rounded-md text-sm ${
                contentType === 'whiteboard' ? 'bg-blue-600' : 'bg-gray-700 hover:bg-gray-600'
              }`}
            >
              <PenTool size={16} /> Whiteboard
            </button>
          </div>

          <div className="flex items-center gap-2">
            {contentType !== 'whiteboard' && (
              <span className="text-sm text-gray-400">
                {currentSlide} / {totalSlides}
              </span>
            )}
            <button
              onClick={() => setShowNotes(!showNotes)}
              className="px-3 py-1.5 bg-gray-700 hover:bg-gray-600 rounded-md text-sm"
            >
              📝 Notes
            </button>
          </div>
        </div>

        {/* WHITEBOARD SUB-TOOLBAR */}
        {contentType === 'whiteboard' && (
          <div className="flex items-center gap-2 px-4 py-2 bg-gray-800 border-b border-gray-700 overflow-x-auto">
            <button
              onClick={() => setTool('pen')}
              className={`p-2 rounded ${tool === 'pen' ? 'bg-blue-600' : 'bg-gray-700'}`}
              title="Pen"
            >
              <PenTool size={16} />
            </button>
            <button
              onClick={() => setTool('highlighter')}
              className={`p-2 rounded ${tool === 'highlighter' ? 'bg-blue-600' : 'bg-gray-700'}`}
              title="Highlighter"
            >
              <Highlighter size={16} />
            </button>
            <button
              onClick={() => setTool('eraser')}
              className={`p-2 rounded ${tool === 'eraser' ? 'bg-blue-600' : 'bg-gray-700'}`}
              title="Eraser"
            >
              <Eraser size={16} />
            </button>

            <div className="border-l border-gray-600 h-6 mx-1" />

            <input
              type="color"
              value={penColor}
              onChange={(e) => setPenColor(e.target.value)}
              className="w-8 h-8 rounded cursor-pointer"
              title="Color"
            />

            <input
              type="range"
              min="1"
              max="20"
              value={penSize}
              onChange={(e) => setPenSize(Number(e.target.value))}
              className="w-20"
              title="Size"
            />

            <div className="border-l border-gray-600 h-6 mx-1" />

            <button
              onClick={handleUndo}
              className="p-2 rounded bg-gray-700 hover:bg-gray-600"
              title="Undo"
            >
              <Undo size={16} />
            </button>
            <button
              onClick={handleRedo}
              className="p-2 rounded bg-gray-700 hover:bg-gray-600"
              title="Redo"
            >
              <Redo size={16} />
            </button>
            <button
              onClick={clearCanvas}
              className="p-2 rounded bg-gray-700 hover:bg-gray-600"
              title="Clear"
            >
              <Trash2 size={16} />
            </button>
            <button
              onClick={handleSaveBoard}
              className="p-2 rounded bg-green-600 hover:bg-green-700"
              title="Save"
            >
              <Download size={16} />
            </button>
          </div>
        )}

        {/* MAIN CONTENT */}
        <div className="flex-1 flex overflow-hidden">
          <div className="flex-1 flex items-center justify-center overflow-auto p-4">
            {contentType === 'ppt' && (
              <div className="flex flex-col items-center gap-4">
                <div className="w-[800px] h-[450px] bg-white rounded-lg flex items-center justify-center text-gray-800 shadow-2xl">
                  <div className="text-center">
                    <Presentation size={64} className="mx-auto mb-4 text-blue-600" />
                    <p className="text-2xl font-bold">Slide {currentSlide}</p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <button onClick={handlePrev} disabled={currentSlide === 1} className="px-4 py-2 bg-blue-600 rounded-md disabled:opacity-50">← Prev</button>
                  <span className="text-sm">Slide {currentSlide} of {totalSlides}</span>
                  <button onClick={handleNext} disabled={currentSlide === totalSlides} className="px-4 py-2 bg-blue-600 rounded-md disabled:opacity-50">Next →</button>
                </div>
              </div>
            )}

            {contentType === 'pdf' && (
              <div className="flex flex-col items-center gap-4">
                <div className="w-[800px] h-[1000px] bg-white rounded-lg shadow-2xl flex items-center justify-center text-gray-800" style={{ transform: `scale(${zoom / 100})` }}>
                  <div className="text-center">
                    <FileText size={64} className="mx-auto mb-4 text-red-500" />
                    <p className="text-2xl font-bold">Page {currentSlide}</p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <button onClick={handleZoomOut} className="px-3 py-2 bg-gray-700 rounded-md"><ZoomOut size={16} /></button>
                  <span className="text-sm">{zoom}%</span>
                  <button onClick={handleZoomIn} className="px-3 py-2 bg-gray-700 rounded-md"><ZoomIn size={16} /></button>
                  <button onClick={handlePrev} className="px-4 py-2 bg-blue-600 rounded-md">← Prev</button>
                  <button onClick={handleNext} className="px-4 py-2 bg-blue-600 rounded-md">Next →</button>
                </div>
              </div>
            )}

            {contentType === '3d' && (
              <div className="flex flex-col items-center gap-4">
                <div className="w-[600px] h-[500px] bg-gradient-to-br from-blue-900 to-purple-900 rounded-lg shadow-2xl flex items-center justify-center">
                  <div className="text-center" style={{ transform: `rotateY(${rotation}deg)` }}>
                    <Box size={120} className="mx-auto text-purple-400 mb-4" />
                    <p className="text-xl font-bold">3D Model</p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <button onClick={() => setRotation(rotation - 15)} className="px-4 py-2 bg-purple-600 rounded-md"><RotateCw size={16} className="inline mr-1" /> Left</button>
                  <button onClick={handleZoomOut} className="px-3 py-2 bg-gray-700 rounded-md"><ZoomOut size={16} /></button>
                  <button onClick={handleZoomIn} className="px-3 py-2 bg-gray-700 rounded-md"><ZoomIn size={16} /></button>
                  <button onClick={() => setRotation(rotation + 15)} className="px-4 py-2 bg-purple-600 rounded-md">Right <RotateCw size={16} className="inline ml-1" /></button>
                </div>
              </div>
            )}

            {contentType === 'video' && (
              <div className="flex flex-col items-center gap-4">
                <div className="w-[800px] h-[450px] bg-black rounded-lg shadow-2xl flex items-center justify-center">
                  <div className="text-center">
                    <Video size={64} className="mx-auto mb-4 text-blue-500" />
                    <p className="text-xl font-bold">Video Player</p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <button onClick={() => setIsPlaying(!isPlaying)} className="px-4 py-2 bg-blue-600 rounded-md">
                    {isPlaying ? <Pause size={16} /> : <Play size={16} />}
                  </button>
                  <input type="range" className="w-96" min="0" max="100" defaultValue="0" />
                  <Volume2 size={20} />
                </div>
              </div>
            )}

            {contentType === 'whiteboard' && (
              <canvas
                ref={canvasRef}
                width={1200}
                height={700}
                onMouseDown={startDrawing}
                onMouseMove={draw}
                onMouseUp={stopDrawing}
                onMouseLeave={stopDrawing}
                className="bg-white rounded-lg shadow-2xl"
                style={{ cursor: tool === 'eraser' ? 'cell' : 'crosshair' }}
              />
            )}
          </div>

          {showNotes && (
            <div className="w-80 bg-gray-800 border-l border-gray-700 flex flex-col">
              <div className="flex items-center justify-between p-3 border-b border-gray-700">
                <span className="font-semibold">📝 Class Notes</span>
                <button onClick={() => setShowNotes(false)} className="text-gray-400 hover:text-white">✕</button>
              </div>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Type class notes..."
                className="flex-1 p-3 bg-gray-900 text-white resize-none focus:outline-none"
              />
              <div className="p-3 border-t border-gray-700">
                <button className="w-full px-4 py-2 bg-green-600 rounded-md hover:bg-green-700 flex items-center justify-center gap-2">
                  <Save size={16} /> Save Notes
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}