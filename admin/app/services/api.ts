import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token');
      localStorage.removeItem('admin_user');
      if (typeof window !== 'undefined' && !window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// ==================== AUTH APIs ====================
export const authAPI = {
  // Admin-only login (checks ADMIN role on backend)
  login: (email: string, password: string) =>
    api.post('/admin/auth/login', { email, password }),

  // Verify admin token
  verifyAdmin: () => api.get('/admin/auth/verify'),

  logout: () => {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_user');
  },

  getProfile: () => api.get('/users/me'),
};

// ==================== COURSE APIs ====================
export const courseAPI = {
  getAll: (params?: any) => api.get('/courses', { params }),
  getById: (id: string) => api.get(`/courses/${id}`),
  create: (data: any) => api.post('/courses', data),
  update: (id: string, data: any) => api.put(`/courses/${id}`, data),
  delete: (id: string) => api.delete(`/courses/${id}`),
  getChapters: (id: string) => api.get(`/courses/${id}/chapters`),
  enroll: (id: string) => api.post(`/courses/${id}/enroll`),
  getMyCourses: () => api.get('/courses/enrollments/my-courses'),
};

// ==================== QUESTION APIs ====================
export const questionAPI = {
  getAll: (params?: any) => api.get('/questions', { params }),
  getById: (id: string) => api.get(`/questions/${id}`),
  create: (data: any) => api.post('/questions', data),
  update: (id: string, data: any) => api.put(`/questions/${id}`, data),
  delete: (id: string) => api.delete(`/questions/${id}`),
  getRandom: (params?: any) => api.get('/questions/random', { params }),
};

// ==================== TEST APIs ====================
export const testAPI = {
  getAll: () => api.get('/tests'),
  getById: (id: string) => api.get(`/tests/${id}`),
  create: (data: any) => api.post('/tests', data),
  update: (id: string, data: any) => api.put(`/tests/${id}`, data),
  delete: (id: string) => api.delete(`/tests/${id}`),
};

// ==================== USER APIs ====================
export const userAPI = {
  getAll: (params?: any) => api.get('/admin/users', { params }),
  getById: (id: string) => api.get(`/admin/users/${id}`),
  update: (id: string, data: any) => api.put(`/admin/users/${id}`, data),
  delete: (id: string) => api.delete(`/admin/users/${id}`),
  suspend: (id: string) => api.post(`/admin/users/${id}/suspend`),
};

// ==================== SUBJECT APIs ====================
export const subjectAPI = {
  getAll: () => api.get('/subjects'),
  getById: (id: string) => api.get(`/subjects/${id}`),
  create: (data: any) => api.post('/subjects', data),
  update: (id: string, data: any) => api.put(`/subjects/${id}`, data),
  delete: (id: string) => api.delete(`/subjects/${id}`),
};

// ==================== NOTE APIs ====================
export const noteAPI = {
  getAll: () => api.get('/notes/all'),
  getPublished: () => api.get('/notes'),
  getById: (id: string) => api.get(`/notes/${id}`),
  create: (data: any) => api.post('/notes', data),
  update: (id: string, data: any) => api.put(`/notes/${id}`, data),
  delete: (id: string) => api.delete(`/notes/${id}`),
  getBySubject: (subjectId: string) => api.get(`/notes/subject/${subjectId}`),
};

// ==================== PRESENTATION APIs ====================
export const presentationAPI = {
  getAll: () => api.get('/presentations'),
  getById: (id: string) => api.get(`/presentations/${id}`),
  upload: (formData: FormData) =>
    api.post('/presentations/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  delete: (id: string) => api.delete(`/presentations/${id}`),
};

// ==================== VIDEO APIs ====================
export const videoAPI = {
  getAll: () => api.get('/videos'),
  getById: (id: string) => api.get(`/videos/${id}`),
  upload: (formData: FormData) =>
    api.post('/videos/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  delete: (id: string) => api.delete(`/videos/${id}`),
  getByCourse: (courseId: string) => api.get(`/videos/course/${courseId}`),
};

// ==================== NOTIFICATION APIs ====================
export const notificationAPI = {
  getAll: (params?: any) => api.get('/notifications', { params }),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markAsRead: (id: string) => api.post(`/notifications/${id}/read`),
  markAllAsRead: () => api.post('/notifications/read-all'),
  send: (data: any) => api.post('/notifications/send', data),
  broadcast: (data: any) => api.post('/notifications/broadcast', data),
  delete: (id: string) => api.delete(`/notifications/${id}`),
};

// ==================== LEADERBOARD APIs ====================
export const leaderboardAPI = {
  get: (params?: any) => api.get('/leaderboard', { params }),
  getMyRank: () => api.get('/leaderboard/rank'),
};

// ==================== PAYMENT APIs ====================
export const paymentAPI = {
  getHistory: () => api.get('/payments/history'),
  getRecent: () => api.get('/payments/recent'),
  createOrder: (data: any) => api.post('/payments/create-order', data),
  verify: (data: any) => api.post('/payments/verify', data),
};

// ==================== AI ASSISTANT APIs ====================
export const aiAPI = {
  getChats: (limit = 20) => api.get('/ai-assistant/chats', { params: { limit } }),
  getChat: (chatId: string) => api.get(`/ai-assistant/chats/${chatId}`),
  createChat: (data: any) => api.post('/ai-assistant/chats', data),
  deleteChat: (chatId: string) => api.delete(`/ai-assistant/chats/${chatId}`),
  sendMessage: (chatId: string, formData: FormData) =>
    api.post(`/ai-assistant/chats/${chatId}/messages`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
};

// ==================== PROGRESS APIs ====================
export const progressAPI = {
  getMyProgress: () => api.get('/progress'),
  getAnalytics: (period = 'month') =>
    api.get('/progress/analytics', { params: { period } }),
  saveClassProgress: (lessonId: string, data: any) =>
    api.post(`/progress/classes/${lessonId}`, data),
  getCourseProgress: (courseId: string) =>
    api.get(`/progress/courses/${courseId}`),
  getContinueWatching: () => api.get('/progress/continue-watching'),
};

// ==================== FILE APIs ====================
export const fileAPI = {
  upload: (file: File, type: string) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);
    return api.post('/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

export default api;