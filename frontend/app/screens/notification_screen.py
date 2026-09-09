from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDIconButton, MDRaisedButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivy.metrics import dp
from kivy.clock import Clock
from datetime import datetime

class NotificationScreen(MDScreen):
    """Notifications screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.notifications = []
        self.build_ui()
    
    def build_ui(self):
        """Build notifications UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Notifications',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['check-all', lambda x: self.mark_all_read()]],
            elevation=2
        )
        
        # Scrollable content
        self.scroll = MDScrollView()
        self.content = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(10), dp(15), dp(10)],
            spacing=dp(10),
            adaptive_height=True
        )
        
        self.scroll.add_widget(self.content)
        
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)
        
        self.add_widget(self.layout)
    
    def on_enter(self):
        """Load notifications when screen displayed"""
        Clock.schedule_once(self.load_notifications, 0.1)
    
    def load_notifications(self, dt):
        """Load notifications from API"""
        app = MDApp.get_running_app()
        result = app.api_service.get('/notifications?limit=50')
        
        if result.get('success'):
            self.notifications = result.get('data', [])
            self.display_notifications(self.notifications)
        else:
            self.show_empty_state('No notifications')
    
    def display_notifications(self, notifications):
        """Display notification cards"""
        self.content.clear_widgets()
        
        if not notifications:
            self.show_empty_state('No notifications yet')
            return
        
        for notification in notifications:
            card = self.create_notification_card(notification)
            self.content.add_widget(card)
    
    def create_notification_card(self, notification):
        """Create notification card"""
        is_read = notification.get('read', False)
        
        card = MDCard(
            orientation='vertical',
            padding=dp(15),
            spacing=dp(5),
            size_hint_y=None,
            height=dp(100),
            elevation=2 if not is_read else 1,
            radius=[dp(10)],
            md_bg_color=[1, 1, 1, 1] if not is_read else [0.95, 0.95, 0.95, 1]
        )
        
        # Notification icon and title row
        title_row = MDBoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height=dp(30)
        )
        
        icon_label = MDLabel(
            text=self.get_icon(notification.get('type')),
            font_style='H6',
            size_hint_x=0.15
        )
        
        title_label = MDLabel(
            text=notification.get('title', 'Notification'),
            font_style='Subtitle1',
            size_hint_x=0.65
        )
        
        time_label = MDLabel(
            text=self.format_time(notification.get('createdAt')),
            font_style='Caption',
            halign='right',
            size_hint_x=0.2,
            theme_text_color='Secondary'
        )
        
        title_row.add_widget(icon_label)
        title_row.add_widget(title_label)
        title_row.add_widget(time_label)
        
        # Message
        message_label = MDLabel(
            text=notification.get('message', ''),
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(40)
        )
        
        card.add_widget(title_row)
        card.add_widget(message_label)
        
        return card
    
    def get_icon(self, notification_type):
        """Get icon for notification type"""
        icons = {
            'NEW_COURSE': '📚',
            'NEW_LECTURE': '🎬',
            'NEW_TEST': '📋',
            'DAILY_CHALLENGE': '🔥',
            'STUDY_REMINDER': '⏰',
            'EXAM_REMINDER': '📅',
            'ANNOUNCEMENT': '📢',
            'CURRENT_AFFAIRS': '📰',
            'ACHIEVEMENT': '🏆'
        }
        return icons.get(notification_type, '🔔')
    
    def format_time(self, date_str):
        """Format timestamp"""
        try:
            dt = datetime.fromisoformat(date_str.replace('Z', '+00:00'))
            return dt.strftime('%d %b, %H:%M')
        except:
            return ''
    
    def mark_all_read(self):
        """Mark all notifications as read"""
        app = MDApp.get_running_app()
        result = app.api_service.post('/notifications/read-all', {})
        
        if result.get('success'):
            Snackbar(text='All notifications marked as read').open()
            self.load_notifications(None)
    
    def show_empty_state(self, message):
        """Show empty state"""
        self.content.clear_widgets()
        empty_label = MDLabel(
            text=message,
            halign='center',
            theme_text_color='Secondary'
        )
        self.content.add_widget(empty_label)
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'home'