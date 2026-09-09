import { useRef, useEffect, useState } from 'react';

export default function Whiteboard() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [color, setColor] = useState('#000000');
  const [lineWidth, setLineWidth] = useState(3);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx!.lineCap = 'round';
  }, []);

  const startDrawing = (e: React.MouseEvent) => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    ctx!.beginPath();
    ctx!.moveTo(e.nativeEvent.offsetX, e.nativeEvent.offsetY);
    setIsDrawing(true);
  };

  const draw = (e: React.MouseEvent) => {
    if (!isDrawing) return;
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    ctx!.lineTo(e.nativeEvent.offsetX, e.nativeEvent.offsetY);
    ctx!.strokeStyle = color;
    ctx!.lineWidth = lineWidth;
    ctx!.stroke();
  };

  const stopDrawing = () => setIsDrawing(false);

  const clearBoard = () => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    ctx!.clearRect(0, 0, canvas!.width, canvas!.height);
  };

  return (
    <div className="flex flex-col h-full w-full">
      <div className="flex gap-2 p-2 bg-gray-100">
        <input type="color" value={color} onChange={(e) => setColor(e.target.value)} />
        <input type="range" min="1" max="10" value={lineWidth} onChange={(e) => setLineWidth(Number(e.target.value))} />
        <button onClick={clearBoard} className="px-3 py-1 bg-red-500 text-white rounded">Clear</button>
      </div>
      <canvas
        ref={canvasRef}
        width={window.innerWidth * 0.8}
        height={window.innerHeight * 0.7}
        onMouseDown={startDrawing}
        onMouseMove={draw}
        onMouseUp={stopDrawing}
        onMouseLeave={stopDrawing}
        className="flex-1 bg-white"
      />
    </div>
  );
}