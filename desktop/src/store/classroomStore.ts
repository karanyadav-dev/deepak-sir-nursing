import { create } from 'zustand';

export type ContentType = 'ppt' | 'pdf' | '3d' | 'video' | 'whiteboard';

interface ClassroomSession {
  id: string;
  course: string;
  subject: string;
  chapter: string;
  date: string;
  startTime: string;
  duration: number;
  notes: string;
}

interface ClassroomState {
  contentType: ContentType;
  currentSlide: number;
  totalSlides: number;
  fullscreen: boolean;
  notesVisible: boolean;
  timerRunning: boolean;
  timerSeconds: number;
  session: ClassroomSession | null;
  setContentType: (type: ContentType) => void;
  setCurrentSlide: (slide: number) => void;
  setTotalSlides: (total: number) => void;
  toggleFullscreen: () => void;
  toggleNotes: () => void;
  setTimerRunning: (running: boolean) => void;
  incrementTimer: () => void;
  resetTimer: () => void;
  startSession: (session: ClassroomSession) => void;
  endSession: () => void;
}

export const useClassroomStore = create<ClassroomState>((set) => ({
  contentType: 'ppt',
  currentSlide: 0,
  totalSlides: 0,
  fullscreen: false,
  notesVisible: false,
  timerRunning: false,
  timerSeconds: 0,
  session: null,
  setContentType: (type) => set({ contentType: type }),
  setCurrentSlide: (slide) => set({ currentSlide: slide }),
  setTotalSlides: (total) => set({ totalSlides: total }),
  toggleFullscreen: () => set((state) => ({ fullscreen: !state.fullscreen })),
  toggleNotes: () => set((state) => ({ notesVisible: !state.notesVisible })),
  setTimerRunning: (running) => set({ timerRunning: running }),
  incrementTimer: () => set((state) => ({ timerSeconds: state.timerSeconds + 1 })),
  resetTimer: () => set({ timerSeconds: 0, timerRunning: false }),
  startSession: (session) => set({ session, timerSeconds: 0, timerRunning: true }),
  endSession: () => set({ session: null, timerRunning: false }),
}));