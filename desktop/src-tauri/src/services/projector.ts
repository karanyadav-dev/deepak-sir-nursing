import { invoke } from '@tauri-apps/api/tauri';

export async function openProjector() {
  await invoke('open_second_window');
}

export async function closeProjector() {
  await invoke('close_second_window');
}

export async function getDisplays() {
  return await invoke('get_displays');
}