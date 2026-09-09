from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.card import MDCard
from kivy.metrics import dp
from kivy.clock import Clock


class AnalyticsScreen(MDScreen):
    """Analytics dashboard screen"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.analytics_data = None
        self.build_ui()

    def build_ui(self):
        self.layout = MDBoxLayout(orientation='vertical', spacing=dp(0))

        self.top_bar = MDTopAppBar(
            title='Performance Analytics',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            elevation=2
        )

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
        Clock.schedule_once(self.load_analytics, 0.1)

    def load_analytics(self, dt):
        app = MDApp.get_running_app()
        result = app.api_service.get('/progress/analytics?period=month')
        if result.get('success'):
            self.analytics_data = result.get('data', {})
            self.display_analytics()

    def display_analytics(self):
        self.content.clear_widgets()
        if not self.analytics_data:
            empty_label = MDLabel(
                text='No analytics data available',
                halign='center',
                theme_text_color='Secondary'
            )
            self.content.add_widget(empty_label)
            return

        card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(250),
            elevation=2,
            radius=[dp(15)]
        )

        title = MDLabel(
            text='Overall Statistics',
            font_style='H6',
            size_hint_y=None,
            height=dp(30)
        )
        card.add_widget(title)

        stats = [
            ('Tests Taken', self.analytics_data.get('totalTests', 0)),
            ('Average Score', f"{self.analytics_data.get('averageScore', 0):.1f}%"),
            ('Best Score', f"{self.analytics_data.get('bestScore', 0):.1f}%"),
            ('Accuracy', f"{self.analytics_data.get('accuracy', 0):.1f}%")
        ]

        for label, value in stats:
            row = MDBoxLayout(
                orientation='horizontal',
                size_hint_y=None,
                height=dp(35)
            )
            lbl = MDLabel(text=label, size_hint_x=0.7)
            val = MDLabel(text=str(value), halign='right', size_hint_x=0.3)
            row.add_widget(lbl)
            row.add_widget(val)
            card.add_widget(row)

        self.content.add_widget(card)

    def go_back(self):
        self.manager.current = 'profile'