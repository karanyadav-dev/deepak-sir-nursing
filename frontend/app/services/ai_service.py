import requests
import json
import os
from typing import Optional, Dict, Any, List
from kivy.network.urlrequest import UrlRequest
from app.utils.config import Config
from app.services.api_service import ApiService

class AIService:
    """Service for AI Assistant operations"""
    
    def __init__(self, api_service: ApiService):
        self.api_service = api_service
        self.base_endpoint = '/ai-assistant'
    
    def create_chat(self, title: str, mode: str = 'GENERAL') -> Dict[str, Any]:
        """Create a new AI chat"""
        data = {
            'title': title,
            'mode': mode
        }
        return self.api_service.post(f'{self.base_endpoint}/chats', data)
    
    def get_chats(self, limit: int = 20) -> Dict[str, Any]:
        """Get user's chat history"""
        return self.api_service.get(
            f'{self.base_endpoint}/chats',
            params={'limit': limit}
        )
    
    def get_chat(self, chat_id: str) -> Dict[str, Any]:
        """Get specific chat details"""
        return self.api_service.get(f'{self.base_endpoint}/chats/{chat_id}')
    
    def send_message(self, chat_id: str, content: str, 
                    image_path: Optional[str] = None,
                    language: str = 'en') -> Dict[str, Any]:
        """Send message to AI"""
        # For multipart upload with image
        if image_path:
            return self.send_message_with_image(chat_id, content, image_path, language)
        
        # Simple text message
        data = {
            'content': content,
            'language': language
        }
        return self.api_service.post(
            f'{self.base_endpoint}/chats/{chat_id}/messages',
            data
        )
    
    def send_message_with_image(self, chat_id: str, content: str,
                               image_path: str, language: str = 'en') -> Dict[str, Any]:
        """Send message with image attachment"""
        url = f"{self.api_service.base_url}{self.base_endpoint}/chats/{chat_id}/messages"
        
        try:
            with open(image_path, 'rb') as image_file:
                files = {
                    'image': (os.path.basename(image_path), image_file, 
                             self.get_mime_type(image_path))
                }
                data = {
                    'content': content,
                    'language': language
                }
                headers = self.api_service.get_headers()
                
                response = requests.post(
                    url,
                    headers=headers,
                    data=data,
                    files=files,
                    timeout=Config.UPLOAD_TIMEOUT
                )
                
                return response.json()
        except Exception as e:
            return {
                'success': False,
                'message': f'Failed to send message: {str(e)}',
                'errorCode': 'AI_SEND_FAILED'
            }
    
    def quick_response(self, mode: str, topic: str, 
                      language: str = 'en') -> Dict[str, Any]:
        """Get quick AI response without chat"""
        params = {
            'mode': mode,
            'topic': topic,
            'language': language
        }
        return self.api_service.get(f'{self.base_endpoint}/quick-response', params=params)
    
    def delete_chat(self, chat_id: str) -> Dict[str, Any]:
        """Delete a chat"""
        return self.api_service.delete(f'{self.base_endpoint}/chats/{chat_id}')
    
    def update_chat(self, chat_id: str, title: str = None, 
                   mode: str = None) -> Dict[str, Any]:
        """Update chat details"""
        data = {}
        if title:
            data['title'] = title
        if mode:
            data['mode'] = mode
        
        return self.api_service.put(f'{self.base_endpoint}/chats/{chat_id}', data)
    
    def search_chats(self, query: str) -> Dict[str, Any]:
        """Search user's chats"""
        return self.api_service.get(
            f'{self.base_endpoint}/chats/search',
            params={'q': query}
        )
    
    def get_mime_type(self, file_path: str) -> str:
        """Get MIME type based on file extension"""
        extension = os.path.splitext(file_path)[1].lower()
        mime_types = {
            '.jpg': 'image/jpeg',
            '.jpeg': 'image/jpeg',
            '.png': 'image/png',
            '.webp': 'image/webp'
        }
        return mime_types.get(extension, 'image/jpeg')