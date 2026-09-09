from app.services.api_service import ApiService

class ProgressService:
    """Service for progress operations"""
    
    def __init__(self, api_service: ApiService):
        self.api_service = api_service
    
    def get_progress(self):
        """Get user progress"""
        return self.api_service.get('/progress')
    
    def get_analytics(self, period='month'):
        """Get analytics data"""
        return self.api_service.get(f'/progress/analytics?period={period}')
    
    def get_streak(self):
        """Get user streak"""
        return self.api_service.get('/progress/streak')