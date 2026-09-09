import { ChevronLeft, ChevronRight } from 'lucide-react';

interface SlideNavigatorProps {
  current: number;
  total: number;
  onNext: () => void;
  onPrev: () => void;
}

export default function SlideNavigator({ current, total, onNext, onPrev }: SlideNavigatorProps) {
  return (
    <div className="flex items-center justify-between p-3 border-b">
      <button onClick={onPrev} className="p-2 hover:bg-gray-100 rounded">
        <ChevronLeft size={20} />
      </button>
      <span className="text-sm font-medium">{current + 1} / {total}</span>
      <button onClick={onNext} className="p-2 hover:bg-gray-100 rounded">
        <ChevronRight size={20} />
      </button>
    </div>
  );
}