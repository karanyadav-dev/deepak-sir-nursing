from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivy.metrics import dp
from kivy.clock import Clock


class PremiumNotesScreen(MDScreen):
    """Premium notes screen with payment lock"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.name = 'premium_notes'
        self.api_service = None
        self.notes = []
        self.build_ui()

    def build_ui(self):
        self.layout = MDBoxLayout(orientation='vertical', spacing=dp(0))

        # Top bar
        self.top_bar = MDTopAppBar(
            title='Study Notes',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
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

        self.scroll.add_widget(self.content)

        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)

        self.add_widget(self.layout)

    def on_enter(self):
        self.api_service = MDApp.get_running_app().api_service
        Clock.schedule_once(self.load_notes, 0.1)

    def load_notes(self, dt):
        """Load notes from API"""
        result = self.api_service.get('/notes')

        if result.get('success'):
            self.notes = result.get('data', [])
            self.display_notes()
        else:
            self.show_empty_state('Failed to load notes')

    def display_notes(self):
        """Display notes"""
        self.content.clear_widgets()

        if not self.notes:
            self.show_empty_state('No notes available')
            return

        for note in self.notes:
            card = self.create_note_card(note)
            self.content.add_widget(card)

    def create_note_card(self, note):
        """Create note card with premium lock"""
        is_premium = note.get('isPremium', False)
        price = note.get('price', 0)

        card = MDCard(
            orientation='vertical',
            padding=dp(15),
            spacing=dp(8),
            size_hint_y=None,
            height=dp(180) if is_premium else dp(140),
            elevation=2,
            radius=[dp(12)]
        )

        # Title row
        title_row = MDBoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height=dp(30)
        )

        title_label = MDLabel(
            text=note.get('title', 'Untitled Note'),
            font_style='Subtitle1',
            size_hint_x=0.8
        )

        if is_premium:
            badge = MDLabel(
                text='🔒 PREMIUM',
                font_style='Caption',
                theme_text_color='Custom',
                text_color=[0.8, 0.5, 0.0, 1],
                size_hint_x=0.2,
                halign='right'
            )
            title_row.add_widget(title_label)
            title_row.add_widget(badge)
        else:
            title_row.add_widget(title_label)

        card.add_widget(title_row)

        # Content preview
        content_text = note.get('content', '')[:100]
        if len(note.get('content', '')) > 100:
            content_text += '...'

        content_label = MDLabel(
            text=content_text,
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(60)
        )
        card.add_widget(content_label)

        # Price / Action button
        if is_premium:
            price_row = MDBoxLayout(
                orientation='horizontal',
                size_hint_y=None,
                height=dp(40)
            )

            price_label = MDLabel(
                text=f'₹{price}',
                font_style='H6',
                theme_text_color='Primary',
                size_hint_x=0.5
            )

            buy_button = MDRaisedButton(
                text='UNLOCK',
                size_hint=(None, None),
                size=(dp(100), dp(40)),
                on_release=lambda x, n=note: self.unlock_note(n)
            )

            price_row.add_widget(price_label)
            price_row.add_widget(buy_button)
            card.add_widget(price_row)
        else:
            read_button = MDRaisedButton(
                text='READ NOW',
                size_hint=(None, None),
                size=(dp(120), dp(35)),
                pos_hint={'right': 1},
                on_release=lambda x, n=note: self.read_note(n)
            )
            card.add_widget(read_button)

        return card

    def unlock_note(self, note):
        """Handle premium note unlock (payment)"""
        Snackbar(text=f'Payment for ₹{note.get("price", 0)} - Coming soon!').open()

    def read_note(self, note):
        """Read free note"""
        Snackbar(text=f'Opening: {note.get("title", "Note")}').open()

    def show_empty_state(self, message):
        """Show empty state"""
        self.content.clear_widgets()
        empty_label = MDLabel(
            text=message,
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(200)
        )
        self.content.add_widget(empty_label)

    def go_back(self):
        self.manager.current = 'home'