import { useState } from 'react';
import Toolbar from './Toolbar';
import SlideNavigator from './SlideNavigator';
import Timer from './Timer';
import Notes from './Notes';
import PPTViewer from '../ppt/PPTViewer';
import PDFViewer from '../pdf/PDFViewer';
import ThreeDViewer from '../three/ThreeDViewer';
import VideoPlayer from '../video/VideoPlayer';
import Whiteboard from '../whiteboard/Whiteboard';

type ContentType = 'ppt' | 'pdf' | '3d' | 'video' | 'whiteboard';

export default function ClassroomView() {
  const [contentType, setContentType] = useState<ContentType>('ppt');
  const [currentSlide, setCurrentSlide] = useState(0);
  const [fullscreen, setFullscreen] = useState(false);
  const [showNotes, setShowNotes] = useState(false);
  const [timerRunning, setTimerRunning] = useState(false);

  const handleNext = () => setCurrentSlide((prev) => prev + 1);
  const handlePrev = () => setCurrentSlide((prev) => Math.max(0, prev - 1));

  return (
    <div className="flex h-full">
      {/* Main Content */}
      <div className="flex-1 bg-black flex items-center justify-center overflow-hidden">
        {contentType === 'ppt' && <PPTViewer slide={currentSlide} />}
        {contentType === 'pdf' && <PDFViewer page={currentSlide} />}
        {contentType === '3d' && <ThreeDViewer modelPath="/models/heart.glb" />}
        {contentType === 'video' && <VideoPlayer src="/videos/sample.mp4" />}
        {contentType === 'whiteboard' && <Whiteboard />}
      </div>

      {/* Sidebar / Controls */}
      <div className="w-80 bg-white border-l flex flex-col">
        <Toolbar contentType={contentType} setContentType={setContentType} />
        <SlideNavigator current={currentSlide} total={20} onNext={handleNext} onPrev={handlePrev} />
        <Timer running={timerRunning} setRunning={setTimerRunning} />
        <Notes show={showNotes} setShow={setShowNotes} />
      </div>
    </div>
  );
}