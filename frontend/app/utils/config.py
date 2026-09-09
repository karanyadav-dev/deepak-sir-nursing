import os

class Config:
    """Application configuration"""
    
    # API Configuration
    API_BASE_URL = os.getenv('API_BASE_URL', 'http://localhost:8080/api')
    
    # App Configuration
    APP_NAME = "Deepak Sir"
    APP_VERSION = "1.0.0"
    APP_ICON = "assets/images/icon.png"
    
    # Storage Configuration
    DATA_DIR = os.path.join(os.path.expanduser('~'), '.deepak_sir')
    CACHE_DIR = os.path.join(DATA_DIR, 'cache')
    DOWNLOAD_DIR = os.path.join(DATA_DIR, 'downloads')
    
    # API Endpoints
    ENDPOINTS = {
        'login': '/auth/login',
        'register': '/auth/register',
        'refresh': '/auth/refresh',
        'subjects': '/subjects',
        'topics': '/topics',
        'questions': '/questions',
        'practice': '/practice',
        'tests': '/tests',
        'attempts': '/attempts',
        'progress': '/progress',
        'bookmarks': '/bookmarks',
        'leaderboard': '/leaderboard',
        'achievements': '/achievements',
        'current_affairs': '/current-affairs',
        'notifications': '/notifications',
        'orders': '/orders',
        'payments': '/payments',
        'coupons': '/coupons',
        'courses': '/courses',
        'ai_chats': '/ai-assistant/chats',
    }
    
    # Timeouts (seconds)
    REQUEST_TIMEOUT = 30
    UPLOAD_TIMEOUT = 300
    
    # Pagination
    ITEMS_PER_PAGE = 20
    
    # Cache duration (seconds)
    CACHE_DURATION = 3600
    
    @classmethod
    def ensure_directories(cls):
        """Create necessary directories if they don't exist"""
        for dir_path in [cls.DATA_DIR, cls.CACHE_DIR, cls.DOWNLOAD_DIR]:
            os.makedirs(dir_path, exist_ok=True)

# Create directories on import
Config.ensure_directories()