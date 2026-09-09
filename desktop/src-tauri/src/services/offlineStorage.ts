import { invoke } from '@tauri-apps/api/tauri';

export async function saveOfflineContent(type: string, path: string, metadata?: any) {
  await invoke('execute_query', {
    query: 'INSERT OR REPLACE INTO offline_content (id, type, path, metadata) VALUES (?, ?, ?, ?)',
    params: [crypto.randomUUID(), type, path, JSON.stringify(metadata || {})],
  });
}

export async function listOfflineContent(type?: string) {
  const query = type
    ? 'SELECT * FROM offline_content WHERE type = ?'
    : 'SELECT * FROM offline_content';
  const params = type ? [type] : [];
  return await invoke('execute_query', { query, params });
}