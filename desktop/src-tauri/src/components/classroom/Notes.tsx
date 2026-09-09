import { useState } from 'react';

interface NotesProps {
  show: boolean;
  setShow: (show: boolean) => void;
}

export default function Notes({ show, setShow }: NotesProps) {
  const [notes, setNotes] = useState('');

  if (!show) {
    return (
      <button onClick={() => setShow(true)} className="p-3 text-left hover:bg-gray-50 border-b">
        📝 Show Notes
      </button>
    );
  }

  return (
    <div className="flex-1 flex flex-col">
      <div className="p-2 flex justify-between items-center border-b">
        <span className="text-sm font-medium">Class Notes</span>
        <button onClick={() => setShow(false)} className="text-gray-500 hover:text-gray-700">✕</button>
      </div>
      <textarea
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
        className="flex-1 p-3 outline-none resize-none"
        placeholder="Type class notes here..."
      />
    </div>
  );
}