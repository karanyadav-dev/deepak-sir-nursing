import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Classroom from './pages/Classroom';
import Notes from './pages/Notes';
import PDFs from './pages/PDFs';
import Models from './pages/Models';
import AIAssistant from './pages/AIAssistant';
import Settings from './pages/Settings';
import { useUserStore } from './store/userStore';

function App() {
  const { user } = useUserStore();

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={user ? <Navigate to="/classroom" /> : <Navigate to="/login" />} />
        <Route path="/classroom" element={user ? <Classroom /> : <Navigate to="/login" />} />
        <Route path="/notes" element={user ? <Notes /> : <Navigate to="/login" />} />
        <Route path="/pdfs" element={user ? <PDFs /> : <Navigate to="/login" />} />
        <Route path="/models" element={user ? <Models /> : <Navigate to="/login" />} />
        <Route path="/ai" element={user ? <AIAssistant /> : <Navigate to="/login" />} />
        <Route path="/settings" element={user ? <Settings /> : <Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;