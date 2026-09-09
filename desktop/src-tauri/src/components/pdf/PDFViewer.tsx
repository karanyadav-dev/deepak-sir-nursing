import { useState } from 'react';
import { Document, Page } from 'react-pdf';
import 'react-pdf/dist/esm/Page/AnnotationLayer.css';
import 'react-pdf/dist/esm/Page/TextLayer.css';

interface PDFViewerProps {
  page: number;
  filePath?: string;
}

export default function PDFViewer({ page, filePath = '/pdfs/example.pdf' }: PDFViewerProps) {
  const [numPages, setNumPages] = useState<number>(0);
  const [scale, setScale] = useState(1.0);

  function onDocumentLoadSuccess({ numPages }: { numPages: number }) {
    setNumPages(numPages);
  }

  return (
    <div className="flex flex-col items-center h-full">
      <Document file={filePath} onLoadSuccess={onDocumentLoadSuccess}>
        <Page pageNumber={page + 1} scale={scale} />
      </Document>
      <div className="flex gap-2 mt-2">
        <button onClick={() => setScale((s) => Math.max(0.5, s - 0.1))} className="text-white">-</button>
        <span className="text-white">{Math.round(scale * 100)}%</span>
        <button onClick={() => setScale((s) => Math.min(2, s + 0.1))} className="text-white">+</button>
      </div>
    </div>
  );
}