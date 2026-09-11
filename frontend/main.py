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

# Screens - Student
from app.screens.login_screen import LoginScreen
from app.screens.register_screen import RegisterScreen
from app.screens.home_screen import HomeScreen
from app.screens.profile_screen import ProfileScreen
from app.screens.ai_chat_screen import AIChatScreen
from app.screens.ai_chat_history_screen import AIChatHistoryScreen
from app.screens.premium_notes_screen import PremiumNotesScreen
from app.screens.course_screen import CoursesScreen
from app.screens.practice_screen import PracticeScreen
from app.screens.test_screen import TestScreen
from app.screens.analytics_screen import AnalyticsScreen
from app.screens.bookmark_screen import BookmarkScreen
from app.screens.notification_screen import NotificationScreen
from app.screens.leaderboard_screen import LeaderboardScreen
from app.screens.payment_screen import PaymentScreen

# Screens - Admin
from app.screens.admin_login_screen import AdminLoginScreen
from app.screens.admin_dashboard_screen import AdminDashboardScreen
from app.screens.classroom_screen import ClassroomScreen

# Services
from app.services.api_service import ApiService
from app.services.ai_service import AIService
from app.utils.config import Config

# Android permissions
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
    """Main application class"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.title = "Deepak Sir Nursing"
        self.icon = "app/assets/images/icon.png"
        self.api_service = None
        self.ai_service = None
        self.current_user = None
        self.auth_token = None
        self.user_store = None
        self.is_admin = False

    def build(self):
        """Build the application"""
        # Theme
        self.theme_cls.theme_style = "Light"
        self.theme_cls.primary_palette = "Blue"
        self.theme_cls.accent_palette = "Teal"
        self.theme_cls.material_style = "M3"

        # Session store
        try:
            self.user_store = JsonStore('user_session.json')
        except Exception as e:
            print(f"Session store error: {e}")
            self.user_store = None

        # Services
        self.api_service = ApiService(Config.API_BASE_URL)
        self.ai_service = AIService(self.api_service)

        # Screen manager
        self.screen_manager = ScreenManager()

        # ========== STUDENT SCREENS ==========
        self.login_screen = LoginScreen()
        self.login_screen.name = 'login'

        self.register_screen = RegisterScreen()
        self.register_screen.name = 'register'

        self.home_screen = HomeScreen()
        self.home_screen.name = 'home'

        self.profile_screen = ProfileScreen()
        self.profile_screen.name = 'profile'

        self.ai_chat_screen = AIChatScreen()
        self.ai_chat_screen.name = 'ai_chat'

        self.ai_chat_history_screen = AIChatHistoryScreen()
        self.ai_chat_history_screen.name = 'ai_chat_history'

        self.premium_notes_screen = PremiumNotesScreen()
        self.premium_notes_screen.name = 'premium_notes'

        self.courses_screen = CoursesScreen()
        self.courses_screen.name = 'courses'

        self.practice_screen = PracticeScreen()
        self.practice_screen.name = 'practice'

        self.test_screen = TestScreen()
        self.test_screen.name = 'test'

        self.analytics_screen = AnalyticsScreen()
        self.analytics_screen.name = 'analytics'

        self.bookmark_screen = BookmarkScreen()
        self.bookmark_screen.name = 'bookmarks'

        self.notification_screen = NotificationScreen()
        self.notification_screen.name = 'notifications'

        self.leaderboard_screen = LeaderboardScreen()
        self.leaderboard_screen.name = 'leaderboard'

        self.payment_screen = PaymentScreen()
        self.payment_screen.name = 'payment'

        # ========== ADMIN SCREENS ==========
        self.admin_login_screen = AdminLoginScreen()
        self.admin_login_screen.name = 'admin_login'

        self.admin_dashboard_screen = AdminDashboardScreen()
        self.admin_dashboard_screen.name = 'admin_dashboard'

        self.classroom_screen = ClassroomScreen()
        self.classroom_screen.name = 'classroom'

        # Add all screens
        all_screens = [
            self.login_screen,
            self.register_screen,
            self.home_screen,
            self.profile_screen,
            self.ai_chat_screen,
            self.ai_chat_history_screen,
            self.premium_notes_screen,
            self.courses_screen,
            self.practice_screen,
            self.test_screen,
            self.analytics_screen,
            self.bookmark_screen,
            self.notification_screen,
            self.leaderboard_screen,
            self.payment_screen,
            self.admin_login_screen,
            self.admin_dashboard_screen,
            self.classroom_screen,
        ]

        for screen in all_screens:
            self.screen_manager.add_widget(screen)

        # Check session
        Clock.schedule_once(self.check_session, 0)

        return self.screen_manager

    def check_session(self, dt):
        """Check if user is logged in"""
        try:
            if self.user_store and self.user_store.exists('session'):
                session = self.user_store.get('session')
                if 'token' in session:
                    self.auth_token = session['token']
                    self.current_user = session.get('user')
                    self.is_admin = session.get('is_admin', False)

                    # Route based on role
                    if self.is_admin:
                        self.screen_manager.current = 'admin_dashboard'
                    else:
                        self.screen_manager.current = 'home'
                    return
        except Exception as e:
            print(f"Session check error: {e}")

        # Default: login screen
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
        self.is_admin = False

        # Clear API tokens
        if self.api_service:
            self.api_service.clear_tokens()

        self.screen_manager.current = 'login'

    def on_stop(self):
        """Called on app exit"""
        pass


if __name__ == '__main__':
    DeepakSirApp().run()