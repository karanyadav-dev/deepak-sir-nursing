from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDFlatButton, MDIconButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivy.metrics import dp
from kivy.clock import Clock

class LeaderboardScreen(MDScreen):
    """Leaderboard screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.period = 'weekly'
        self.build_ui()
    
    def build_ui(self):
        """Build leaderboard UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Leaderboard',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            elevation=2
        )
        
        # Period selector
        self.period_bar = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(5),
            size_hint_y=None,
            height=dp(50)
        )
        
        self.periods = ['daily', 'weekly', 'monthly', 'overall']
        self.period_buttons = []
        
        for period in self.periods:
            btn = MDFlatButton(
                text=period.upper(),
                size_hint=(1, None),
                height=dp(35),
                on_release=lambda x, p=period: self.change_period(p)
            )
            self.period_buttons.append(btn)
            self.period_bar.add_widget(btn)
        
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
        self.layout.add_widget(self.period_bar)
        self.layout.add_widget(self.scroll)
        
        self.add_widget(self.layout)
    
    def on_enter(self):
        """Load leaderboard when screen displayed"""
        self.change_period(self.period)
    
    def change_period(self, period):
        """Change leaderboard period"""
        self.period = period
        
        # Update button colors
        for btn in self.period_buttons:
            if btn.text.lower() == period:
                btn.md_bg_color = [0.2, 0.6, 1, 1]
                btn.text_color = [1, 1, 1, 1]
            else:
                btn.md_bg_color = [1, 1, 1, 1]
                btn.text_color = [0, 0, 0, 1]
        
        self.load_leaderboard(period)
    
    def load_leaderboard(self, period):
        """Load leaderboard from API"""
        app = MDApp.get_running_app()
        result = app.api_service.get(f'/leaderboard?period={period}&limit=50')
        
        if result.get('success'):
            data = result.get('data', [])
            self.display_leaderboard(data)
        else:
            self.show_empty_state('Leaderboard not available')
    
    def display_leaderboard(self, entries):
        """Display leaderboard entries"""
        self.content.clear_widgets()
        
        if not entries:
            self.show_empty_state('No data available')
            return
        
        for index, entry in enumerate(entries):
            rank = index + 1
            card = self.create_leaderboard_card(rank, entry)
            self.content.add_widget(card)
    
    def create_leaderboard_card(self, rank, entry):
        """Create leaderboard card"""
        card = MDCard(
            orientation='horizontal',
            padding=dp(15),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(70),
            elevation=1,
            radius=[dp(10)]
        )
        
        # Rank badge
        rank_colors = {
            1: [1, 0.84, 0, 1],  # Gold
            2: [0.75, 0.75, 0.75, 1],  # Silver
            3: [0.8, 0.5, 0.2, 1],  # Bronze
        }
        
        rank_badge = MDLabel(
            text=self.get_rank_text(rank),
            font_style='H5',
            halign='center',
            size_hint_x=0.15,
            theme_text_color='Custom',
            text_color=rank_colors.get(rank, [0.5, 0.5, 0.5, 1])
        )
        
        # Student info
        info_box = MDBoxLayout(
            orientation='vertical',
            size_hint_x=0.65,
            spacing=dp(2)
        )
        
        name_label = MDLabel(
            text=entry.get('name', entry.get('studentName', 'Student')),
            font_style='Subtitle1',
            size_hint_y=None,
            height=dp(25)
        )
        
        info_box.add_widget(name_label)
        
        # Score
        score_label = MDLabel(
            text=f"Score: {entry.get('score', 0)}",
            font_style='Caption',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(20)
        )
        info_box.add_widget(score_label)
        
        # Percentile
        percentile_label = MDLabel(
            text=f"{entry.get('percentile', 0)}%",
            font_style='Subtitle2',
            halign='right',
            size_hint_x=0.2,
            theme_text_color='Primary'
        )
        
        card.add_widget(rank_badge)
        card.add_widget(info_box)
        card.add_widget(percentile_label)
        
        return card
    
    def get_rank_text(self, rank):
        """Get rank text with emoji"""
        if rank == 1:
            return '🥇'
        elif rank == 2:
            return '🥈'
        elif rank == 3:
            return '🥉'
        else:
            return f'#{rank}'
    
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