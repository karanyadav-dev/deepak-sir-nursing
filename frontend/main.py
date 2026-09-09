import os
import sys
from kivy.core.window import Window
from kivy.lang import Builder
from kivy.uix.screenmanager import ScreenManager
from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivy.clock import Clock
from kivy.storage.jsonstore import JsonStore
from kivy.utils import platform

# Add project directories to path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from app.screens.login_screen import LoginScreen
from app.screens.register_screen import RegisterScreen
from app.screens.home_screen import HomeScreen
from app.screens.profile_screen import ProfileScreen
from app.screens.ai_chat_screen import AIChatScreen
from app.screens.ai_chat_history_screen import AIChatHistoryScreen
from app.services.api_service import ApiService
from app.services.ai_service import AIService
from app.utils.config import Config

# Request Android permissions if on Android
if platform == 'android':
    try:
        from android.permissions import request_permissions, Permission
        request_permissions([
            Permission.INTERNET,
            Permission.CAMERA,
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE,
            Permission.READ_MEDIA_IMAGES
        ])
    except ImportError:
        pass


class DeepakSirApp(MDApp):
    """Main application class for Deepak Sir"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.title = "Deepak Sir"
        self.icon = "app/assets/images/icon.png"
        self.api_service = None
        self.ai_service = None
        self.current_user = None
        self.auth_token = None
        self.user_store = None

    def build(self):
        """Build the application"""
        # Theme configuration
        self.theme_cls.theme_style = "Light"
        self.theme_cls.primary_palette = "Blue"
        self.theme_cls.accent_palette = "Teal"
        self.theme_cls.material_style = "M3"

        # Initialize JsonStore for session
        try:
            self.user_store = JsonStore('user_session.json')
        except Exception as e:
            print(f"Session store error: {e}")
            self.user_store = None

        # Initialize API service
        self.api_service = ApiService(Config.API_BASE_URL)

        # Initialize AI service
        self.ai_service = AIService(self.api_service)

        # Create screen manager
        self.screen_manager = ScreenManager()

        # ============================================
        # SCREENS CREATE KARO (NAAM KE SAATH)
        # ============================================

        # Login Screen
        self.login_screen = LoginScreen()
        self.login_screen.name = 'login'

        # Register Screen
        self.register_screen = RegisterScreen()
        self.register_screen.name = 'register'

        # Home Screen
        self.home_screen = HomeScreen()
        self.home_screen.name = 'home'

        # Profile Screen
        self.profile_screen = ProfileScreen()
        self.profile_screen.name = 'profile'

        # AI Chat Screen
        self.ai_chat_screen = AIChatScreen()
        self.ai_chat_screen.name = 'ai_chat'

        # AI Chat History Screen
        self.ai_chat_history_screen = AIChatHistoryScreen()
        self.ai_chat_history_screen.name = 'ai_chat_history'

        # ============================================
        # SCREENS ADD KARO SCREEN MANAGER MEIN
        # ============================================

        self.screen_manager.add_widget(self.login_screen)
        self.screen_manager.add_widget(self.register_screen)
        self.screen_manager.add_widget(self.home_screen)
        self.screen_manager.add_widget(self.profile_screen)
        self.screen_manager.add_widget(self.ai_chat_screen)
        self.screen_manager.add_widget(self.ai_chat_history_screen)

        # Check for existing session
        Clock.schedule_once(self.check_session, 0)

        return self.screen_manager

    def check_session(self, dt):
        """Check if user is already logged in"""
        try:
            if self.user_store and self.user_store.exists('session'):
                session_data = self.user_store.get('session')
                if 'token' in session_data:
                    self.auth_token = session_data['token']
                    self.current_user = session_data.get('user')
                    self.screen_manager.current = 'home'
                    return
        except Exception as e:
            print(f"Session check error: {e}")

        # Default: Login screen dikhao
        self.screen_manager.current = 'login'

    def logout(self):
        """Logout user"""
        try:
            if self.user_store:
                self.user_store.delete('session')
        except Exception:
            pass

        self.auth_token = None
        self.current_user = None
        self.screen_manager.current = 'login'

    def on_stop(self):
        """Clean up when app closes"""
        pass


if __name__ == '__main__':
    DeepakSirApp().run()