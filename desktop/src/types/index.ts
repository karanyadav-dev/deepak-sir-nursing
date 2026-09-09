export interface User {
  id: string;
  fullName: string;
  email: string;
  phone?: string;
  role: 'SUPER_ADMIN' | 'ADMIN' | 'TEACHER' | 'STUDENT';
  token: string;
  refreshToken?: string;
}

export interface Course {
  id: string;
  title: string;
  description: string;
  thumbnailUrl?: string;
  price: number;
  isPremium: boolean;
  published: boolean;
  examId?: string;
  examName?: string;
  chapterCount?: number;
  lessonCount?: number;
}

export interface Subject {
  id: string;
  name: string;
  description?: string;
  icon?: string;
}

export interface Chapter {
  id: string;
  title: string;
  description?: string;
  orderIndex: number;
  courseId: string;
  lessons?: Lesson[];
}

export interface Lesson {
  id: string;
  title: string;
  description?: string;
  videoUrl?: string;
  pdfUrl?: string;
  notesUrl?: string;
  orderIndex: number;
  durationMinutes: number;
  isPreview: boolean;
  completed?: boolean;
}

export interface OfflineContent {
  id: string;
  type: 'ppt' | 'pdf' | 'video' | '3d' | 'image' | 'notes';
  path: string;
  metadata?: Record<string, any>;
  downloadedAt: string;
}

export interface ClassSession {
  id: string;
  course: string;
  subject: string;
  chapter: string;
  date: string;
  teacherId: string;
  teacherName: string;
  duration: number;
  notes: string;
  slidesCovered: number;
}

export interface ThreeDModelPart {
  id: string;
  name: string;
  color: string;
  description?: string;
}

export interface ThreeDModel {
  id: string;
  name: string;
  path: string;
  parts?: ThreeDModelPart[];
  labels?: { position: [number, number, number]; text: string }[];
}