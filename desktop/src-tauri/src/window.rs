use tauri::{App, Manager};
use crate::commands::DbState;
use rusqlite::Connection;
use std::sync::Mutex;

pub fn setup(app: &mut App) -> Result<(), Box<dyn std::error::Error>> {
    let app_data_dir = app.path_resolver().app_data_dir().unwrap();
    std::fs::create_dir_all(&app_data_dir)?;
    let db_path = app_data_dir.join("deepaksir.db");
    let conn = Connection::open(db_path)?;
    app.manage(DbState(Mutex::new(conn)));
    Ok(())
}