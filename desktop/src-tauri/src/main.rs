#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

mod commands;
mod window;

fn main() {
    tauri::Builder::default()
        .invoke_handler(tauri::generate_handler![
            commands::save_file,
            commands::read_file,
            commands::list_files,
            commands::delete_file,
            commands::open_second_window,
            commands::close_second_window,
            commands::init_sqlite,
        ])
        .setup(|app| {
            window::setup(app)?;
            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}