from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDIconButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivy.metrics import dp
from kivy.clock import Clock

class BookmarkScreen(MDScreen):
    """Bookmarks screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.build_ui()
    
    def build_ui(self):
        """Build bookmarks UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='My Bookmarks',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
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
        """Load bookmarks when screen displayed"""
        Clock.schedule_once(self.load_bookmarks, 0.1)
    
    def load_bookmarks(self, dt):
        """Load bookmarks from API"""
        app = MDApp.get_running_app()
        result = app.api_service.get('/bookmarks')
        
        if result.get('success'):
            bookmarks = result.get('data', [])
            self.display_bookmarks(bookmarks)
        else:
            self.show_empty_state('No bookmarks found')
    
    def display_bookmarks(self, bookmarks):
        """Display bookmark cards"""
        self.content.clear_widgets()
        
        if not bookmarks:
            self.show_empty_state('No bookmarks yet')
            return
        
        for bookmark in bookmarks:
            card = MDCard(
                orientation='horizontal',
                padding=dp(15),
                size_hint_y=None,
                height=dp(80),
                elevation=1,
                radius=[dp(10)]
            )
            
            # Bookmark type icon
            icon_label = MDLabel(
                text=self.get_icon(bookmark.get('type')),
                font_style='H5',
                size_hint_x=0.2
            )
            
            # Bookmark info
            info_box = MDBoxLayout(
                orientation='vertical',
                size_hint_x=0.6
            )
            
            type_label = MDLabel(
                text=bookmark.get('type', 'Unknown'),
                font_style='Subtitle2'
            )
            
            info_box.add_widget(type_label)
            
            # Delete button
            delete_btn = MDIconButton(
                icon='delete',
                size_hint_x=0.2,
                on_release=lambda x, b=bookmark: self.delete_bookmark(b)
            )
            
            card.add_widget(icon_label)
            card.add_widget(info_box)
            card.add_widget(delete_btn)
            
            self.content.add_widget(card)
    
    def get_icon(self, bookmark_type):
        """Get icon for bookmark type"""
        icons = {
            'QUESTION': '❓',
            'COURSE': '📚',
            'NOTES': '📝',
            'LECTURE': '🎬',
            'TEST': '📋'
        }
        return icons.get(bookmark_type, '🔖')
    
    def delete_bookmark(self, bookmark):
        """Delete bookmark"""
        app = MDApp.get_running_app()
        result = app.api_service.delete(f"/bookmarks/{bookmark.get('id')}")
        
        if result.get('success'):
            Snackbar(text='Bookmark removed').open()
            self.load_bookmarks(None)
    
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
        self.manager.current = 'profile'