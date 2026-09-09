from app.services.api_service import ApiService

class NotificationService:
    """Service for notification operations"""
    
    def __init__(self, api_service: ApiService):
        self.api_service = api_service
    
    def get_notifications(self, limit=20, unread_only=False):
        """Get user notifications"""
        return self.api_service.get(
            '/notifications',
            params={'limit': limit, 'unreadOnly': unread_only}
        )
    
    def mark_as_read(self, notification_id):
        """Mark notification as read"""
        return self.api_service.post(f'/notifications/{notification_id}/read', {})
    
    def mark_all_read(self):
        """Mark all notifications as read"""
        return self.api_service.post('/notifications/read-all', {})