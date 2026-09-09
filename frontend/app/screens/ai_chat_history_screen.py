from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDIconButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.textfield import MDTextField
from kivymd.uix.snackbar import Snackbar
from kivy.metrics import dp
from kivy.clock import Clock
from datetime import datetime

class ChatHistoryCard(MDCard):
    """Card for displaying chat history"""
    
    def __init__(self, chat_data, **kwargs):
        super().__init__(**kwargs)
        self.chat_data = chat_data
        self.orientation = 'vertical'
        self.size_hint_y = None
        self.height = dp(80)
        self.padding = dp(10)
        self.spacing = dp(5)
        self.elevation = 1
        self.radius = [dp(10)]
        
        self.build_ui()
    
    def build_ui(self):
        """Build chat history card UI"""
        top_row = MDBoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height=dp(30)
        )
        
        title = MDLabel(
            text=self.chat_data.get('title', 'Untitled Chat'),
            font_style='Subtitle1',
            size_hint_x=0.7
        )
        
        date = MDLabel(
            text=self.format_date(self.chat_data.get('updatedAt')),
            font_style='Caption',
            halign='right',
            size_hint_x=0.3,
            theme_text_color='Secondary'
        )
        
        top_row.add_widget(title)
        top_row.add_widget(date)
        
        bottom_row = MDBoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height=dp(20)
        )
        
        mode = MDLabel(
            text=self.chat_data.get('mode', 'GENERAL'),
            font_style='Caption',
            size_hint_x=0.7,
            theme_text_color='Secondary'
        )
        
        message_count = MDLabel(
            text=f"{len(self.chat_data.get('messages', []))} messages",
            font_style='Caption',
            halign='right',
            size_hint_x=0.3,
            theme_text_color='Secondary'
        )
        
        bottom_row.add_widget(mode)
        bottom_row.add_widget(message_count)
        
        self.add_widget(top_row)
        self.add_widget(bottom_row)
    
    def format_date(self, date_str):
        """Format date string"""
        try:
            dt = datetime.fromisoformat(date_str.replace('Z', '+00:00'))
            return dt.strftime('%d %b, %H:%M')
        except:
            return ''
    
    def on_touch_down(self, touch):
        """Handle card tap"""
        if self.collide_point(*touch.pos):
            app = MDApp.get_running_app()
            ai_screen = app.screen_manager.get_screen('ai_chat')
            ai_screen.current_chat_id = self.chat_data['id']
            ai_screen.load_chat_messages()
            app.screen_manager.current = 'ai_chat'
            return True
        return super().on_touch_down(touch)


class AIChatHistoryScreen(MDScreen):
    """AI chat history screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.build_ui()
    
    def build_ui(self):
        """Build chat history screen UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        self.top_bar = MDTopAppBar(
            title='Chat History',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['magnify', lambda x: self.show_search()]],
            elevation=2
        )
        
        self.search_bar = MDTextField(
            hint_text='Search chats...',
            mode='rectangle',
            size_hint_y=None,
            height=dp(0),
            padding=[dp(10), dp(0), dp(10), dp(0)]
        )
        self.search_bar.bind(text=self.on_search_text)
        self.search_bar.opacity = 0
        
        self.scroll = MDScrollView()
        self.chat_list = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(10), dp(15), dp(10)],
            spacing=dp(10),
            adaptive_height=True
        )
        
        self.scroll.add_widget(self.chat_list)
        
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.search_bar)
        self.layout.add_widget(self.scroll)
        
        self.add_widget(self.layout)
    
    def on_enter(self):
        """Load chats when screen displayed"""
        Clock.schedule_once(self.load_chats, 0.1)
    
    def load_chats(self, dt):
        """Load chat history from API"""
        app = MDApp.get_running_app()
        ai_service = app.ai_service
        
        if not ai_service:
            self.show_empty_state('AI service not available')
            return
        
        result = ai_service.get_chats(limit=50)
        
        if result.get('success'):
            chats = result.get('data', [])
            self.display_chats(chats)
        else:
            self.show_empty_state('Failed to load chats')
    
    def display_chats(self, chats):
        """Display chat cards"""
        self.chat_list.clear_widgets()
        
        if not chats:
            self.show_empty_state('No chats yet')
            return
        
        for chat in chats:
            card = ChatHistoryCard(chat)
            self.chat_list.add_widget(card)
    
    def show_empty_state(self, message):
        """Show empty state"""
        self.chat_list.clear_widgets()
        empty_label = MDLabel(
            text=message,
            halign='center',
            theme_text_color='Secondary'
        )
        self.chat_list.add_widget(empty_label)
    
    def show_search(self):
        """Toggle search bar"""
        if self.search_bar.opacity == 0:
            self.search_bar.opacity = 1
            self.search_bar.height = dp(50)
        else:
            self.search_bar.opacity = 0
            self.search_bar.height = dp(0)
    
    def on_search_text(self, instance, value):
        """Handle search text change"""
        if len(value) >= 2:
            self.search_chats(value)
        elif len(value) == 0:
            self.load_chats(None)
    
    def search_chats(self, query):
        """Search chats via API"""
        app = MDApp.get_running_app()
        ai_service = app.ai_service
        
        if not ai_service:
            return
        
        result = ai_service.search_chats(query)
        
        if result.get('success'):
            chats = result.get('data', [])
            self.display_chats(chats)
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'profile'
