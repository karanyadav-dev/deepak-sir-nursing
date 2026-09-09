from app.services.api_service import ApiService

class BookmarkService:
    """Service for bookmark operations"""
    
    def __init__(self, api_service: ApiService):
        self.api_service = api_service
    
    def get_bookmarks(self):
        """Get user bookmarks"""
        return self.api_service.get('/bookmarks')
    
    def add_bookmark(self, bookmark_type, item_id):
        """Add a bookmark"""
        return self.api_service.post(
            '/bookmarks',
            {'type': bookmark_type, 'itemId': item_id}
        )
    
    def remove_bookmark(self, bookmark_id):
        """Remove a bookmark"""
        return self.api_service.delete(f'/bookmarks/{bookmark_id}')