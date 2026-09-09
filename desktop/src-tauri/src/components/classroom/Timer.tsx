import { useState, useEffect } from 'react';
import { Play, Pause, RotateCcw } from 'lucide-react';

interface TimerProps {
  running: boolean;
  setRunning: (running: boolean) => void;
}

export default function Timer({ running, setRunning }: TimerProps) {
  const [seconds, setSeconds] = useState(0);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (running) {
      interval = setInterval(() => setSeconds((s) => s + 1), 1000);
    }
    return () => clearInterval(interval);
  }, [running]);

  const formatTime = (sec: number) => {
    const mins = Math.floor(sec / 60);
    const secs = sec % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="p-3 border-b flex items-center justify-between">
      <span className="text-lg font-mono">{formatTime(seconds)}</span>
      <div className="flex gap-1">
        <button onClick={() => setRunning(!running)} className="p-2 hover:bg-gray-100 rounded">
          {running ? <Pause size={16} /> : <Play size={16} />}
        </button>
        <button onClick={() => { setSeconds(0); setRunning(false); }} className="p-2 hover:bg-gray-100 rounded">
          <RotateCcw size={16} />
        </button>
      </div>
    </div>
  );
}