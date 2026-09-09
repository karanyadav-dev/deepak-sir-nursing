import sqlite3
import json
from datetime import datetime
from typing import Optional, List, Dict, Any
from app.utils.config import Config
import os

class LocalDatabase:
    """Local SQLite database for offline support"""
    
    def __init__(self):
        self.db_path = os.path.join(Config.DATA_DIR, 'deepak_sir.db')
        self.connection = None
        self.init_database()
    
    def init_database(self):
        """Initialize database tables"""
        self.connection = sqlite3.connect(self.db_path)
        cursor = self.connection.cursor()
        
        # Create tables
        cursor.executescript('''
            CREATE TABLE IF NOT EXISTS cached_questions (
                id TEXT PRIMARY KEY,
                question_data TEXT NOT NULL,
                subject_id TEXT,
                topic_id TEXT,
                cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS cached_tests (
                id TEXT PRIMARY KEY,
                test_data TEXT NOT NULL,
                cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS offline_attempts (
                id TEXT PRIMARY KEY,
                test_id TEXT NOT NULL,
                answers TEXT NOT NULL,
                submitted_at TIMESTAMP,
                synced INTEGER DEFAULT 0
            );
            
            CREATE TABLE IF NOT EXISTS course_progress (
                course_id TEXT PRIMARY KEY,
                progress_data TEXT NOT NULL,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS study_sessions (
                id TEXT PRIMARY KEY,
                session_data TEXT NOT NULL,
                synced INTEGER DEFAULT 0
            );
            
            CREATE TABLE IF NOT EXISTS bookmarks (
                id TEXT PRIMARY KEY,
                type TEXT NOT NULL,
                item_id TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                synced INTEGER DEFAULT 0,
                UNIQUE(type, item_id)
            );
            
            CREATE INDEX IF NOT EXISTS idx_cached_questions_subject 
                ON cached_questions(subject_id);
            CREATE INDEX IF NOT EXISTS idx_cached_questions_topic 
                ON cached_questions(topic_id);
            CREATE INDEX IF NOT EXISTS idx_offline_attempts_synced 
                ON offline_attempts(synced);
        ''')
        
        self.connection.commit()
    
    def cache_questions(self, questions: List[Dict[str, Any]], 
                       subject_id: Optional[str] = None, 
                       topic_id: Optional[str] = None):
        """Cache questions for offline use"""
        cursor = self.connection.cursor()
        
        for question in questions:
            cursor.execute('''
                INSERT OR REPLACE INTO cached_questions 
                (id, question_data, subject_id, topic_id)
                VALUES (?, ?, ?, ?)
            ''', (
                question.get('id'),
                json.dumps(question),
                subject_id,
                topic_id
            ))
        
        self.connection.commit()
    
    def get_cached_questions(self, subject_id: Optional[str] = None,
                            topic_id: Optional[str] = None,
                            limit: int = 10) -> List[Dict[str, Any]]:
        """Get cached questions"""
        cursor = self.connection.cursor()
        
        query = 'SELECT question_data FROM cached_questions WHERE 1=1'
        params = []
        
        if subject_id:
            query += ' AND subject_id = ?'
            params.append(subject_id)
        
        if topic_id:
            query += ' AND topic_id = ?'
            params.append(topic_id)
        
        query += f' ORDER BY cached_at DESC LIMIT {limit}'
        
        cursor.execute(query, params)
        rows = cursor.fetchall()
        
        return [json.loads(row[0]) for row in rows]
    
    def save_offline_attempt(self, test_id: str, answers: Dict[str, str]):
        """Save test attempt for offline submission"""
        import uuid
        attempt_id = str(uuid.uuid4())
        
        cursor = self.connection.cursor()
        cursor.execute('''
            INSERT INTO offline_attempts (id, test_id, answers)
            VALUES (?, ?, ?)
        ''', (attempt_id, test_id, json.dumps(answers)))
        
        self.connection.commit()
        return attempt_id
    
    def get_unsynced_attempts(self) -> List[Dict[str, Any]]:
        """Get attempts that haven't been synced"""
        cursor = self.connection.cursor()
        cursor.execute('''
            SELECT id, test_id, answers 
            FROM offline_attempts 
            WHERE synced = 0
        ''')
        
        rows = cursor.fetchall()
        return [
            {
                'id': row[0],
                'testId': row[1],
                'answers': json.loads(row[2])
            }
            for row in rows
        ]
    
    def mark_attempt_synced(self, attempt_id: str):
        """Mark attempt as synced"""
        cursor = self.connection.cursor()
        cursor.execute('''
            UPDATE offline_attempts 
            SET synced = 1, submitted_at = CURRENT_TIMESTAMP
            WHERE id = ?
        ''', (attempt_id,))
        self.connection.commit()
    
    def update_course_progress(self, course_id: str, progress_data: Dict[str, Any]):
        """Update course progress locally"""
        cursor = self.connection.cursor()
        cursor.execute('''
            INSERT OR REPLACE INTO course_progress (course_id, progress_data)
            VALUES (?, ?)
        ''', (course_id, json.dumps(progress_data)))
        self.connection.commit()
    
    def get_course_progress(self, course_id: str) -> Optional[Dict[str, Any]]:
        """Get course progress from local storage"""
        cursor = self.connection.cursor()
        cursor.execute('''
            SELECT progress_data FROM course_progress WHERE course_id = ?
        ''', (course_id,))
        
        row = cursor.fetchone()
        return json.loads(row[0]) if row else None
    
    def add_bookmark(self, item_type: str, item_id: str):
        """Add bookmark locally"""
        import uuid
        bookmark_id = str(uuid.uuid4())
        
        cursor = self.connection.cursor()
        cursor.execute('''
            INSERT OR IGNORE INTO bookmarks (id, type, item_id)
            VALUES (?, ?, ?)
        ''', (bookmark_id, item_type, item_id))
        self.connection.commit()
    
    def get_bookmarks(self, item_type: Optional[str] = None) -> List[Dict[str, Any]]:
        """Get bookmarks from local storage"""
        cursor = self.connection.cursor()
        
        if item_type:
            cursor.execute('''
                SELECT id, type, item_id FROM bookmarks WHERE type = ?
            ''', (item_type,))
        else:
            cursor.execute('SELECT id, type, item_id FROM bookmarks')
        
        rows = cursor.fetchall()
        return [
            {'id': row[0], 'type': row[1], 'itemId': row[2]}
            for row in rows
        ]
    
    def sync_data(self):
        """Sync offline data with server"""
        unsynced_attempts = self.get_unsynced_attempts()
        # Sync logic implemented in service layer
        
    def clear_cache(self, older_than_days: int = 7):
        """Clear old cached data"""
        cursor = self.connection.cursor()
        cursor.execute('''
            DELETE FROM cached_questions 
            WHERE cached_at < datetime('now', ?)
        ''', (f'-{older_than_days} days',))
        self.connection.commit()
    
    def close(self):
        """Close database connection"""
        if self.connection:
            self.connection.close()