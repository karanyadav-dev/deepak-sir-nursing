import requests
import json
from typing import Optional, Dict, Any
from kivy.storage.jsonstore import JsonStore
from app.utils.config import Config

class ApiService:
    """Service for handling API requests"""
    
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()
        self.token_store = JsonStore('tokens.json')
        self.access_token = None
        self.refresh_token = None
        self.load_tokens()
    
    def load_tokens(self):
        """Load tokens from storage"""
        if self.token_store.exists('tokens'):
            tokens = self.token_store.get('tokens')
            self.access_token = tokens.get('access_token')
            self.refresh_token = tokens.get('refresh_token')
    
    def save_tokens(self, access_token: str, refresh_token: str):
        """Save tokens to storage"""
        self.access_token = access_token
        self.refresh_token = refresh_token
        self.token_store.put('tokens', 
            access_token=access_token, 
            refresh_token=refresh_token)
    
    def clear_tokens(self):
        """Clear stored tokens"""
        self.access_token = None
        self.refresh_token = None
        if self.token_store.exists('tokens'):
            self.token_store.delete('tokens')
    
    def get_headers(self, include_auth: bool = True) -> Dict[str, str]:
        """Get request headers"""
        headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
        if include_auth and self.access_token:
            headers['Authorization'] = f'Bearer {self.access_token}'
        return headers
    
    def get(self, endpoint: str, params: Optional[Dict] = None, 
            include_auth: bool = True) -> Dict[str, Any]:
        """Make GET request"""
        url = f"{self.base_url}{endpoint}"
        try:
            response = self.session.get(
                url, 
                headers=self.get_headers(include_auth),
                params=params,
                timeout=Config.REQUEST_TIMEOUT
            )
            return self.handle_response(response)
        except requests.exceptions.RequestException as e:
            return {
                'success': False,
                'message': str(e),
                'errorCode': 'NETWORK_ERROR'
            }
    
    def post(self, endpoint: str, data: Dict, include_auth: bool = True) -> Dict[str, Any]:
        """Make POST request"""
        url = f"{self.base_url}{endpoint}"
        try:
            response = self.session.post(
                url,
                headers=self.get_headers(include_auth),
                json=data,
                timeout=Config.REQUEST_TIMEOUT
            )
            return self.handle_response(response)
        except requests.exceptions.RequestException as e:
            return {
                'success': False,
                'message': str(e),
                'errorCode': 'NETWORK_ERROR'
            }
    
    def put(self, endpoint: str, data: Dict, include_auth: bool = True) -> Dict[str, Any]:
        """Make PUT request"""
        url = f"{self.base_url}{endpoint}"
        try:
            response = self.session.put(
                url,
                headers=self.get_headers(include_auth),
                json=data,
                timeout=Config.REQUEST_TIMEOUT
            )
            return self.handle_response(response)
        except requests.exceptions.RequestException as e:
            return {
                'success': False,
                'message': str(e),
                'errorCode': 'NETWORK_ERROR'
            }
    
    def delete(self, endpoint: str, include_auth: bool = True) -> Dict[str, Any]:
        """Make DELETE request"""
        url = f"{self.base_url}{endpoint}"
        try:
            response = self.session.delete(
                url,
                headers=self.get_headers(include_auth),
                timeout=Config.REQUEST_TIMEOUT
            )
            return self.handle_response(response)
        except requests.exceptions.RequestException as e:
            return {
                'success': False,
                'message': str(e),
                'errorCode': 'NETWORK_ERROR'
            }
    
    def handle_response(self, response: requests.Response) -> Dict[str, Any]:
        """Handle API response"""
        try:
            data = response.json()
            if response.status_code == 401 and self.refresh_token:
                # Token expired, try to refresh
                if self.refresh_access_token():
                    # Retry the original request
                    return self.retry_request(response.request)
            return data
        except json.JSONDecodeError:
            return {
                'success': False,
                'message': 'Invalid response from server',
                'errorCode': 'INVALID_RESPONSE'
            }
    
    def refresh_access_token(self) -> bool:
        """Refresh access token using refresh token"""
        if not self.refresh_token:
            return False
        
        url = f"{self.base_url}{Config.ENDPOINTS['refresh']}"
        try:
            response = self.session.post(
                url,
                headers={'Content-Type': 'application/json'},
                json={'refreshToken': self.refresh_token},
                timeout=Config.REQUEST_TIMEOUT
            )
            if response.status_code == 200:
                data = response.json()
                if data.get('success'):
                    tokens = data['data']
                    self.save_tokens(
                        tokens['accessToken'],
                        tokens.get('refreshToken', self.refresh_token)
                    )
                    return True
        except:
            pass
        return False
    
    def retry_request(self, original_request) -> Dict[str, Any]:
        """Retry original request with new token"""
        try:
            prepared_request = original_request.copy()
            prepared_request.headers['Authorization'] = f'Bearer {self.access_token}'
            
            response = self.session.send(prepared_request, timeout=Config.REQUEST_TIMEOUT)
            return response.json()
        except:
            return {
                'success': False,
                'message': 'Request failed after token refresh',
                'errorCode': 'REQUEST_FAILED'
            }