import requests
import json
from typing import Optional, Dict, Any
from kivy.storage.jsonstore import JsonStore
from app.utils.config import Config

class AuthService:
    """Service for handling authentication operations"""
    
    def __init__(self, api_service):
        self.api_service = api_service
        self.user_store = JsonStore('user_session.json')
    
    def register(self, full_name: str, email: str, phone: str, 
                password: str) -> Dict[str, Any]:
        """Register a new user"""
        data = {
            'fullName': full_name,
            'email': email,
            'phone': phone,
            'password': password
        }
        
        result = self.api_service.post('/auth/register', data, include_auth=False)
        
        if result.get('success'):
            self.save_session(result.get('data', {}))
        
        return result
    
    def login(self, email: str, password: str) -> Dict[str, Any]:
        """Login user with email and password"""
        data = {
            'email': email,
            'password': password
        }
        
        result = self.api_service.post('/auth/login', data, include_auth=False)
        
        if result.get('success'):
            self.save_session(result.get('data', {}))
        
        return result
    
    def login_with_phone(self, phone: str, otp: str) -> Dict[str, Any]:
        """Login with phone and OTP"""
        data = {
            'phone': phone,
            'otp': otp
        }
        
        result = self.api_service.post('/auth/login/phone', data, include_auth=False)
        
        if result.get('success'):
            self.save_session(result.get('data', {}))
        
        return result
    
    def logout(self) -> None:
        """Logout user"""
        try:
            if self.user_store.exists('session'):
                self.user_store.delete('session')
        except:
            pass
        
        if self.api_service:
            self.api_service.clear_tokens()
    
    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """Get current logged in user"""
        try:
            if self.user_store.exists('session'):
                session = self.user_store.get('session')
                return session.get('user')
        except:
            pass
        return None
    
    def is_logged_in(self) -> bool:
        """Check if user is logged in"""
        try:
            return self.user_store.exists('session')
        except:
            return False
    
    def save_session(self, data: Dict[str, Any]) -> None:
        """Save session data"""
        try:
            self.user_store.put('session',
                token=data.get('accessToken'),
                refresh_token=data.get('refreshToken'),
                user=data.get('user', {})
            )
            
            # Save tokens to API service
            if self.api_service:
                self.api_service.save_tokens(
                    data.get('accessToken'),
                    data.get('refreshToken')
                )
        except Exception as e:
            print(f"Failed to save session: {e}")
    
    def get_token(self) -> Optional[str]:
        """Get access token"""
        try:
            if self.user_store.exists('session'):
                session = self.user_store.get('session')
                return session.get('token')
        except:
            pass
        return None
    
    def forgot_password(self, email: str) -> Dict[str, Any]:
        """Request password reset"""
        data = {'email': email}
        return self.api_service.post('/auth/forgot-password', data, include_auth=False)
    
    def reset_password(self, token: str, new_password: str) -> Dict[str, Any]:
        """Reset password with token"""
        data = {
            'token': token,
            'newPassword': new_password
        }
        return self.api_service.post('/auth/reset-password', data, include_auth=False)