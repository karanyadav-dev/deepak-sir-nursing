import { useState } from 'react';
import { Save, Monitor, Volume2, Keyboard, Database, HardDrive, User, Moon, Sun } from 'lucide-react';

export default function Settings() {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [autoStartTimer, setAutoStartTimer] = useState(false);
  const [defaultFullscreen, setDefaultFullscreen] = useState(true);
  const [downloadLocation, setDownloadLocation] = useState('C:\\Users\\DeepakSir\\Downloads');
  const [showShortcuts, setShowShortcuts] = useState(true);

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow p-4">
        <h1 className="text-2xl font-bold">Settings</h1>
        <p className="text-sm text-gray-500">Configure your teaching environment</p>
      </header>

      <main className="p-6 max-w-3xl">
        {/* Appearance */}
        <section className="bg-white rounded-lg shadow mb-4">
          <div className="p-4 border-b">
            <h2 className="font-semibold flex items-center gap-2">
              <Monitor size={18} className="text-blue-600" />
              Appearance
            </h2>
          </div>
          <div className="p-4 space-y-3">
            <div className="flex items-center justify-between">
              <span>Theme</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setTheme('light')}
                  className={`px-3 py-1 rounded flex items-center gap-1 ${theme === 'light' ? 'bg-blue-600 text-white' : 'bg-gray-100'}`}
                >
                  <Sun size={14} /> Light
                </button>
                <button
                  onClick={() => setTheme('dark')}
                  className={`px-3 py-1 rounded flex items-center gap-1 ${theme === 'dark' ? 'bg-blue-600 text-white' : 'bg-gray-100'}`}
                >
                  <Moon size={14} /> Dark
                </button>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <span>Default to Fullscreen</span>
              <input
                type="checkbox"
                checked={defaultFullscreen}
                onChange={(e) => setDefaultFullscreen(e.target.checked)}
                className="w-5 h-5"
              />
            </div>
          </div>
        </section>

        {/* Classroom */}
        <section className="bg-white rounded-lg shadow mb-4">
          <div className="p-4 border-b">
            <h2 className="font-semibold flex items-center gap-2">
              <Keyboard size={18} className="text-green-600" />
              Classroom
            </h2>
          </div>
          <div className="p-4 space-y-3">
            <div className="flex items-center justify-between">
              <span>Auto-start Timer</span>
              <input
                type="checkbox"
                checked={autoStartTimer}
                onChange={(e) => setAutoStartTimer(e.target.checked)}
                className="w-5 h-5"
              />
            </div>
            <div className="flex items-center justify-between">
              <span>Show Keyboard Shortcuts</span>
              <input
                type="checkbox"
                checked={showShortcuts}
                onChange={(e) => setShowShortcuts(e.target.checked)}
                className="w-5 h-5"
              />
            </div>
          </div>
        </section>

        {/* Storage */}
        <section className="bg-white rounded-lg shadow mb-4">
          <div className="p-4 border-b">
            <h2 className="font-semibold flex items-center gap-2">
              <HardDrive size={18} className="text-orange-600" />
              Storage
            </h2>
          </div>
          <div className="p-4 space-y-3">
            <div>
              <label className="block text-sm mb-1">Download Location</label>
              <input
                type="text"
                value={downloadLocation}
                onChange={(e) => setDownloadLocation(e.target.value)}
                className="w-full px-3 py-2 border rounded-md"
              />
            </div>
            <div className="flex items-center justify-between text-sm">
              <span>Local Storage Used</span>
              <span className="font-medium">1.2 GB / 10 GB</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div className="bg-blue-600 h-2 rounded-full" style={{ width: '12%' }} />
            </div>
          </div>
        </section>

        {/* Save Button */}
        <button className="bg-blue-600 text-white px-6 py-2 rounded-md flex items-center gap-2">
          <Save size={16} />
          Save Settings
        </button>
      </main>
    </div>
  );
}