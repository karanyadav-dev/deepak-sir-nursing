from app.services.api_service import ApiService

class LeaderboardService:
    """Service for leaderboard operations"""
    
    def __init__(self, api_service: ApiService):
        self.api_service = api_service
    
    def get_leaderboard(self, period='weekly', limit=50):
        """Get leaderboard data"""
        return self.api_service.get(
            '/leaderboard',
            params={'period': period, 'limit': limit}
        )
    
    def get_user_rank(self, period='weekly'):
        """Get current user's rank"""
        return self.api_service.get(
            '/leaderboard/rank',
            params={'period': period}
        )