import { Presentation, FileText, Box, Video, PenTool, StickyNote, Maximize2, Minimize2 } from 'lucide-react';

export type ContentType = 'ppt' | 'pdf' | '3d' | 'video' | 'whiteboard';

interface ToolbarProps {
  contentType: ContentType;
  setContentType: (type: ContentType) => void;
  fullscreen?: boolean;
  toggleFullscreen?: () => void;
  notesVisible?: boolean;
  toggleNotes?: () => void;
}

export default function Toolbar({ 
  contentType, 
  setContentType, 
  fullscreen = false,
  toggleFullscreen,
  notesVisible = false,
  toggleNotes
}: ToolbarProps) {
  
  const tools = [
    { id: 'ppt' as ContentType, icon: Presentation, label: 'PPT', color: 'text-orange-500' },
    { id: 'pdf' as ContentType, icon: FileText, label: 'PDF', color: 'text-red-500' },
    { id: '3d' as ContentType, icon: Box, label: '3D', color: 'text-purple-500' },
    { id: 'video' as ContentType, icon: Video, label: 'Video', color: 'text-blue-500' },
    { id: 'whiteboard' as ContentType, icon: PenTool, label: 'Board', color: 'text-green-500' },
  ];

  return (
    <div className="flex items-center gap-1 p-3 border-b bg-white">
      {tools.map((tool) => (
        <button
          key={tool.id}
          onClick={() => setContentType(tool.id)}
          className={`flex-1 flex flex-col items-center gap-1 p-2 rounded-md transition-colors ${
            contentType === tool.id 
              ? 'bg-blue-100 ring-2 ring-blue-500' 
              : 'hover:bg-gray-100'
          }`}
        >
          <tool.icon size={20} className={tool.color} />
          <span className="text-xs font-medium">{tool.label}</span>
        </button>
      ))}

      <div className="border-l mx-1 h-8" />

      {toggleNotes && (
        <button
          onClick={toggleNotes}
          className={`flex-1 flex flex-col items-center gap-1 p-2 rounded-md transition-colors ${
            notesVisible ? 'bg-yellow-100 ring-2 ring-yellow-500' : 'hover:bg-gray-100'
          }`}
        >
          <StickyNote size={20} className="text-yellow-500" />
          <span className="text-xs font-medium">Notes</span>
        </button>
      )}

      {toggleFullscreen && (
        <button
          onClick={toggleFullscreen}
          className="flex-1 flex flex-col items-center gap-1 p-2 rounded-md hover:bg-gray-100"
        >
          {fullscreen ? (
            <Minimize2 size={20} className="text-gray-600" />
          ) : (
            <Maximize2 size={20} className="text-gray-600" />
          )}
          <span className="text-xs font-medium">{fullscreen ? 'Exit' : 'Full'}</span>
        </button>
      )}
    </div>
  );
}