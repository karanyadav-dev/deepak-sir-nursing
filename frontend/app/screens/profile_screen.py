from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDIconButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.card import MDCard
from kivymd.uix.list import MDList, OneLineListItem
from kivy.metrics import dp
from kivy.clock import Clock

class ProfileScreen(MDScreen):
    """User profile screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.build_ui()
    
    def build_ui(self):
        """Build profile UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='My Profile',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['logout', lambda x: self.logout()]],
            elevation=2
        )
        
        # Scrollable content
        self.scroll = MDScrollView()
        self.content = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(10), dp(15), dp(10)],
            spacing=dp(15),
            adaptive_height=True
        )
        
        # User info card
        self.user_card = MDCard(
            orientation='vertical',
            padding=dp(20),
            size_hint_y=None,
            height=dp(150),
            elevation=2,
            radius=[dp(15)]
        )
        
        self.username_label = MDLabel(
            text='Student',
            font_style='H5',
            halign='center',
            size_hint_y=None,
            height=dp(40)
        )
        
        self.email_label = MDLabel(
            text='',
            font_style='Body2',
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(30)
        )
        
        self.phone_label = MDLabel(
            text='',
            font_style='Body2',
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(30)
        )
        
        self.user_card.add_widget(self.username_label)
        self.user_card.add_widget(self.email_label)
        self.user_card.add_widget(self.phone_label)
        
        self.content.add_widget(self.user_card)
        
        # Menu options
        self.menu_card = MDCard(
            orientation='vertical',
            padding=dp(10),
            size_hint_y=None,
            height=dp(400),
            elevation=2,
            radius=[dp(15)]
        )
        
        menu_list = MDList()
        
        menu_items = [
            ('ðŸ“Š', 'My Analytics', self.show_analytics),
            ('ðŸ¤–', 'AI Chat History', self.show_ai_history),
            ('ðŸ”–', 'Bookmarks', self.show_bookmarks),
            ('ðŸ“š', 'My Courses', self.show_courses),
            ('ðŸ“', 'My Tests', self.show_tests),
            ('âš™ï¸', 'Settings', self.show_settings),
            ('â“', 'Help & Support', self.show_help),
        ]
        
        for icon, text, callback in menu_items:
            item = OneLineListItem(
                text=f'{icon}  {text}',
                on_release=lambda x, cb=callback: cb()
            )
            menu_list.add_widget(item)
        
        self.menu_card.add_widget(menu_list)
        self.content.add_widget(self.menu_card)
        
        self.scroll.add_widget(self.content)
        
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)
        
        self.add_widget(self.layout)
    
    def on_enter(self):
        """Load user data when screen displayed"""
        Clock.schedule_once(self.load_user_data, 0.1)
    
    def load_user_data(self, dt):
        """Load user data"""
        app = MDApp.get_running_app()
        if app.current_user:
            self.username_label.text = app.current_user.get('fullName', 'Student')
            self.email_label.text = app.current_user.get('email', '')
            self.phone_label.text = app.current_user.get('phone', '')
    
    def show_analytics(self):
        """Navigate to analytics"""
        self.manager.current = 'analytics'
    
    def show_ai_history(self):
        """Navigate to AI chat history"""
        self.manager.current = 'ai_chat_history'
    
    def show_bookmarks(self):
        """Show bookmarks (placeholder)"""
        from kivymd.uix.snackbar import Snackbar
        Snackbar(text='Bookmarks coming soon').open()
    
    def show_courses(self):
        """Navigate to courses"""
        self.manager.current = 'courses'
    
    def show_tests(self):
        """Navigate to tests"""
        self.manager.current = 'tests'
    
    def show_settings(self):
        """Show settings (placeholder)"""
        from kivymd.uix.snackbar import Snackbar
        Snackbar(text='Settings coming soon').open()
    
    def show_help(self):
        """Show help (placeholder)"""
        from kivymd.uix.snackbar import Snackbar
        Snackbar(text='Help coming soon').open()
    
    def logout(self):
        """Logout user"""
        app = MDApp.get_running_app()
        app.logout()
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'home'

