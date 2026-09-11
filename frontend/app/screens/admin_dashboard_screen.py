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


class AdminDashboardScreen(MDScreen):
    """Admin dashboard - only visible to admins"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.name = 'admin_dashboard'
        self.build_ui()

    def build_ui(self):
        self.layout = MDBoxLayout(orientation='vertical', spacing=dp(0))

        # Top bar
        self.top_bar = MDTopAppBar(
            title='Admin Dashboard',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['logout', lambda x: self.logout()]],
            elevation=2
        )

        # Content
        self.scroll = MDScrollView()
        self.content = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(15), dp(15), dp(15)],
            spacing=dp(15),
            adaptive_height=True
        )

        # Welcome
        welcome = MDLabel(
            text='Welcome, Admin! 👋',
            font_style='H5',
            size_hint_y=None,
            height=dp(50)
        )

        admin_notice = MDLabel(
            text='🔐 Full admin access enabled',
            font_style='Caption',
            theme_text_color='Custom',
            text_color=[0.2, 0.7, 0.3, 1],
            size_hint_y=None,
            height=dp(25)
        )

        self.content.add_widget(welcome)
        self.content.add_widget(admin_notice)

        # Admin actions
        actions = [
            ('🎓', 'Manage Courses', 'courses_admin'),
            ('📝', 'Manage Notes', 'notes_admin'),
            ('❓', 'Manage Questions', 'questions_admin'),
            ('📋', 'Manage Tests', 'tests_admin'),
            ('🎥', 'Manage Videos', 'videos_admin'),
            ('📊', 'View Analytics', 'admin_analytics'),
            ('👥', 'Manage Users', 'users_admin'),
            ('📢', 'Send Notifications', 'notifications_admin'),
        ]

        for icon, label, screen_name in actions:
            card = MDCard(
                orientation='horizontal',
                padding=dp(15),
                spacing=dp(15),
                size_hint_y=None,
                height=dp(70),
                elevation=2,
                radius=[dp(12)]
            )

            icon_label = MDLabel(
                text=icon,
                font_style='H4',
                size_hint_x=0.15
            )

            text_label = MDLabel(
                text=label,
                font_style='Subtitle1',
                size_hint_x=0.85
            )

            card.add_widget(icon_label)
            card.add_widget(text_label)

            self.content.add_widget(card)

        self.scroll.add_widget(self.content)

        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)

        self.add_widget(self.layout)

    def go_back(self):
        self.manager.current = 'home'

    def logout(self):
        app = MDApp.get_running_app()
        app.logout()
        Snackbar(text='Logged out').open()