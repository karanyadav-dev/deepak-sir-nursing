use std::fs;
use tauri::Manager;
use rusqlite::Connection;
use std::sync::Mutex;

pub struct DbState(pub Mutex<Connection>);

#[tauri::command]
pub fn save_file(path: String, content: String) -> Result<(), String> {
    fs::write(path, content).map_err(|e| e.to_string())
}

#[tauri::command]
pub fn read_file(path: String) -> Result<String, String> {
    fs::read_to_string(path).map_err(|e| e.to_string())
}

#[tauri::command]
pub fn list_files(path: String) -> Result<Vec<String>, String> {
    let mut files = Vec::new();
    for entry in fs::read_dir(path).map_err(|e| e.to_string())? {
        let entry = entry.map_err(|e| e.to_string())?;
        files.push(entry.path().display().to_string());
    }
    Ok(files)
}

#[tauri::command]
pub fn delete_file(path: String) -> Result<(), String> {
    fs::remove_file(path).map_err(|e| e.to_string())
}

#[tauri::command]
pub fn open_second_window(app: tauri::AppHandle) -> Result<(), String> {
    tauri::WindowBuilder::new(
        &app,
        "projector",
        tauri::WindowUrl::App("index.html".into())
    )
    .title("Projector")
    .fullscreen(true)
    .build()
    .map_err(|e| e.to_string())?;
    Ok(())
}

#[tauri::command]
pub fn close_second_window(app: tauri::AppHandle) -> Result<(), String> {
    if let Some(window) = app.get_window("projector") {
        window.close().map_err(|e| e.to_string())?;
    }
    Ok(())
}

#[tauri::command]
pub fn init_sqlite(state: tauri::State<DbState>) -> Result<(), String> {
    let conn = state.0.lock().map_err(|e| e.to_string())?;
    conn.execute_batch("
        CREATE TABLE IF NOT EXISTS offline_content (
            id TEXT PRIMARY KEY,
            type TEXT NOT NULL,
            path TEXT NOT NULL,
            metadata TEXT
        );
        CREATE TABLE IF NOT EXISTS settings (
            key TEXT PRIMARY KEY,
            value TEXT
        );
        CREATE TABLE IF NOT EXISTS class_sessions (
            id TEXT PRIMARY KEY,
            course TEXT,
            subject TEXT,
            chapter TEXT,
            date TEXT,
            duration INTEGER,
            notes TEXT
        );
    ").map_err(|e| e.to_string())?;
    Ok(())
}