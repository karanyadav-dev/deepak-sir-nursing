import { useEffect, useState } from 'react';
import * as pptxjs from 'pptxjsjs';

interface PPTViewerProps {
  slide: number;
  filePath?: string;
}

export default function PPTViewer({ slide, filePath = '/presentations/example.pptx' }: PPTViewerProps) {
  const [slides, setSlides] = useState<string[]>([]);

  useEffect(() => {
    const loadPPT = async () => {
      try {
        const response = await fetch(filePath);
        const arrayBuffer = await response.arrayBuffer();
        const pptx = new pptxjs();
        await pptx.load(arrayBuffer);
        const slideCount = pptx.slides.length;
        const slideImages: string[] = [];
        for (let i = 0; i < slideCount; i++) {
          const slideDiv = pptx.slides[i].content;
          // Convert slide to image/svg - placeholder
          slideImages.push(`data:image/svg+xml;base64,${btoa(slideDiv.outerHTML)}`);
        }
        setSlides(slideImages);
      } catch (error) {
        console.error('Failed to load PPT:', error);
      }
    };
    loadPPT();
  }, [filePath]);

  if (slides.length === 0) return <div className="text-white">Loading presentation...</div>;
  return <img src={slides[slide]} alt={`Slide ${slide + 1}`} className="max-h-full max-w-full" />;
}