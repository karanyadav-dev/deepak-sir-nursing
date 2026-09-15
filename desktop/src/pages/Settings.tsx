import { useState } from 'react';
import Layout from '../components/Layout';
import {
  Save, Monitor, Keyboard, HardDrive, Sun, Moon, CheckCircle
} from 'lucide-react';

export default function Settings() {
  const [theme, setTheme] = useState<'light' | 'dark'>(() => {
    return (localStorage.getItem('theme') as 'light' | 'dark') || 'light';
  });
  const [autoStartTimer, setAutoStartTimer] = useState(
    localStorage.getItem('autoStartTimer') === 'true'
  );
  const [showShortcuts, setShowShortcuts] = useState(
    localStorage.getItem('showShortcuts') !== 'false'
  );
  const [fullscreen, setFullscreen] = useState(
    localStorage.getItem('fullscreen') === 'true'
  );
  const [downloadLocation, setDownloadLocation] = useState(
    localStorage.getItem('downloadLocation') || 'C:\\Users\\DeepakSir\\Downloads'
  );
  const [saved, setSaved] = useState(false);

  const handleSave = () => {
    localStorage.setItem('theme', theme);
    localStorage.setItem('autoStartTimer', String(autoStartTimer));
    localStorage.setItem('showShortcuts', String(showShortcuts));
    localStorage.setItem('fullscreen', String(fullscreen));
    localStorage.setItem('downloadLocation', downloadLocation);

    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  return (
    <Layout title="Settings" showBack>
      <div className="p-6 max-w-3xl">
        {/* Appearance */}
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 mb-4">
          <div className="p-4 border-b border-gray-200">
            <h2 className="font-semibold flex items-center gap-2 text-gray-800">
              <Monitor size={18} className="text-blue-600" />
              Appearance
            </h2>
          </div>
          <div className="p-4 space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-gray-700">Theme</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setTheme('light')}
                  className={`px-4 py-2 rounded-lg flex items-center gap-2 text-sm ${
                    theme === 'light'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700'
                  }`}
                >
                  <Sun size={14} /> Light
                </button>
                <button
                  onClick={() => setTheme('dark')}
                  className={`px-4 py-2 rounded-lg flex items-center gap-2 text-sm ${
                    theme === 'dark'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-700'
                  }`}
                >
                  <Moon size={14} /> Dark
                </button>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-700">Default to Fullscreen</span>
              <input
                type="checkbox"
                checked={fullscreen}
                onChange={(e) => setFullscreen(e.target.checked)}
                className="w-5 h-5 cursor-pointer"
              />
            </div>
          </div>
        </section>

        {/* Classroom */}
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 mb-4">
          <div className="p-4 border-b border-gray-200">
            <h2 className="font-semibold flex items-center gap-2 text-gray-800">
              <Keyboard size={18} className="text-green-600" />
              Classroom
            </h2>
          </div>
          <div className="p-4 space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-gray-700">Auto-start Timer</span>
              <input
                type="checkbox"
                checked={autoStartTimer}
                onChange={(e) => setAutoStartTimer(e.target.checked)}
                className="w-5 h-5 cursor-pointer"
              />
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-700">Show Keyboard Shortcuts</span>
              <input
                type="checkbox"
                checked={showShortcuts}
                onChange={(e) => setShowShortcuts(e.target.checked)}
                className="w-5 h-5 cursor-pointer"
              />
            </div>
          </div>
        </section>

        {/* Storage */}
        <section className="bg-white rounded-xl shadow-sm border border-gray-200 mb-4">
          <div className="p-4 border-b border-gray-200">
            <h2 className="font-semibold flex items-center gap-2 text-gray-800">
              <HardDrive size={18} className="text-orange-600" />
              Storage
            </h2>
          </div>
          <div className="p-4 space-y-4">
            <div>
              <label className="block text-sm text-gray-700 mb-2">
                Download Location
              </label>
              <input
                type="text"
                value={downloadLocation}
                onChange={(e) => setDownloadLocation(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <div className="flex items-center justify-between text-sm mb-2">
                <span className="text-gray-700">Local Storage Used</span>
                <span className="font-medium text-gray-900">1.2 GB / 10 GB</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-blue-600 h-2 rounded-full" style={{ width: '12%' }} />
              </div>
            </div>
          </div>
        </section>

        {/* Save Button */}
        <button
          onClick={handleSave}
          className={`px-6 py-3 rounded-lg flex items-center gap-2 font-medium transition-colors ${
            saved
              ? 'bg-green-600 text-white'
              : 'bg-blue-600 text-white hover:bg-blue-700'
          }`}
        >
          {saved ? (
            <>
              <CheckCircle size={18} />
              Settings Saved!
            </>
          ) : (
            <>
              <Save size={18} />
              Save Settings
            </>
          )}
        </button>
      </div>
    </Layout>
  );
}