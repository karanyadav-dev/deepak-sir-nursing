from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDIconButton, MDRaisedButton, MDFlatButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.dialog import MDDialog
from kivy.metrics import dp
from kivy.clock import Clock
from kivy.uix.behaviors import ButtonBehavior


class HomeScreen(MDScreen):
    """Home screen with dashboard"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.build_ui()

    def build_ui(self):
        """Build the home screen UI"""
        # Main layout
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )

        # Top app bar
        self.top_bar = MDTopAppBar(
            title='Deepak Sir',
            left_action_items=[['menu', lambda x: self.show_menu()]],
            right_action_items=[['bell', lambda x: self.show_notifications()]],
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

        # Greeting section
        self.greeting_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(100),
            elevation=3,
            radius=[dp(15)]
        )

        self.greeting_label = MDLabel(
            text='Hello, Student 👋',
            font_style='H5',
            theme_text_color='Primary'
        )

        self.subtitle_label = MDLabel(
            text='Welcome to your learning dashboard',
            font_style='Body2',
            theme_text_color='Secondary'
        )

        greeting_box = MDBoxLayout(
            orientation='vertical',
            spacing=dp(5)
        )
        greeting_box.add_widget(self.greeting_label)
        greeting_box.add_widget(self.subtitle_label)

        self.greeting_card.add_widget(greeting_box)

        # Target exam card
        self.exam_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(130),
            elevation=3,
            radius=[dp(15)],
            md_bg_color=[0.9, 0.95, 1, 1]
        )

        self.exam_name = MDLabel(
            text='NORCET 2024',
            font_style='H6',
            theme_text_color='Primary'
        )

        self.exam_info = MDLabel(
            text='120 Days Remaining',
            font_style='Body2',
            theme_text_color='Secondary'
        )

        self.preparation_label = MDLabel(
            text='Preparation: 68%',
            font_style='Subtitle1',
            theme_text_color='Secondary'
        )

        exam_box = MDBoxLayout(
            orientation='vertical',
            spacing=dp(5)
        )
        exam_box.add_widget(self.exam_name)
        exam_box.add_widget(self.exam_info)
        exam_box.add_widget(self.preparation_label)

        self.exam_card.add_widget(exam_box)

        # Quick actions grid
        self.actions_card = MDCard(
            orientation='vertical',
            padding=dp(10),
            size_hint_y=None,
            height=dp(200),
            elevation=2,
            radius=[dp(15)]
        )

        actions_title = MDLabel(
            text='Quick Actions',
            font_style='Subtitle1',
            size_hint_y=None,
            height=dp(30)
        )

        self.actions_card.add_widget(actions_title)

        # Action buttons row 1
        actions_row1 = MDBoxLayout(
            orientation='horizontal',
            spacing=dp(10),
            size_hint_y=None,
            height=dp(70)
        )

        self.practice_btn = self.create_action_button('📝', 'Practice')
        self.test_btn = self.create_action_button('📋', 'Tests')
        self.notes_btn = self.create_action_button('📚', 'Notes')

        # Bind Notes button to open premium notes
        self.notes_btn.bind(on_touch_down=self.on_notes_click)

        actions_row1.add_widget(self.practice_btn)
        actions_row1.add_widget(self.test_btn)
        actions_row1.add_widget(self.notes_btn)

        self.actions_card.add_widget(actions_row1)

        # Action buttons row 2
        actions_row2 = MDBoxLayout(
            orientation='horizontal',
            spacing=dp(10),
            size_hint_y=None,
            height=dp(70)
        )

        self.course_btn = self.create_action_button('🎓', 'Courses')
        self.affairs_btn = self.create_action_button('📰', 'Current Affairs')
        self.more_btn = self.create_action_button('✨', 'More')

        actions_row2.add_widget(self.course_btn)
        actions_row2.add_widget(self.affairs_btn)
        actions_row2.add_widget(self.more_btn)

        self.actions_card.add_widget(actions_row2)

        # Continue learning card
        self.continue_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(100),
            elevation=3,
            radius=[dp(15)]
        )

        self.continue_title = MDLabel(
            text='Continue Learning',
            font_style='Subtitle1',
            size_hint_y=None,
            height=dp(25)
        )

        self.continue_info = MDLabel(
            text='Medical Surgical Nursing\nCardiovascular System - 65% Complete',
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(40)
        )

        self.continue_button = MDRaisedButton(
            text='CONTINUE',
            size_hint=(None, None),
            size=(dp(120), dp(30)),
            pos_hint={'right': 1}
        )

        self.continue_card.add_widget(self.continue_title)
        self.continue_card.add_widget(self.continue_info)
        self.continue_card.add_widget(self.continue_button)

        # Daily challenge card
        self.daily_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(110),
            elevation=3,
            radius=[dp(15)]
        )

        self.daily_title = MDLabel(
            text='Daily Challenge',
            font_style='Subtitle1',
            size_hint_y=None,
            height=dp(25)
        )

        self.daily_info = MDLabel(
            text='Complete 20 MCQs today\nStreak: 5 days 🔥',
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(40)
        )

        self.daily_button = MDRaisedButton(
            text='START NOW',
            size_hint=(None, None),
            size=(dp(120), dp(30)),
            pos_hint={'right': 1}
        )

        self.daily_card.add_widget(self.daily_title)
        self.daily_card.add_widget(self.daily_info)
        self.daily_card.add_widget(self.daily_button)

        # Add all cards to content
        self.content.add_widget(self.greeting_card)
        self.content.add_widget(self.exam_card)
        self.content.add_widget(self.actions_card)
        self.content.add_widget(self.continue_card)
        self.content.add_widget(self.daily_card)

        self.scroll.add_widget(self.content)

        # Add to main layout
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)

        self.add_widget(self.layout)

    def create_action_button(self, icon, text):
        """Create an action button"""
        card = MDCard(
            orientation='vertical',
            padding=dp(5),
            elevation=1,
            radius=[dp(10)],
            size_hint=(1, None),
            height=dp(60)
        )

        icon_label = MDLabel(
            text=icon,
            halign='center',
            font_style='H6',
            size_hint_y=None,
            height=dp(25)
        )

        text_label = MDLabel(
            text=text,
            halign='center',
            font_style='Caption',
            size_hint_y=None,
            height=dp(20)
        )

        card.add_widget(icon_label)
        card.add_widget(text_label)

        return card

    def on_notes_click(self, instance, touch):
        """Handle notes button click"""
        if instance.collide_point(*touch.pos):
            self.manager.current = 'premium_notes'
            return True
        return False

    def on_enter(self):
        """Load dashboard data when screen is displayed"""
        Clock.schedule_once(self.load_dashboard_data, 0.1)

    def load_dashboard_data(self, dt):
        """Load user dashboard data from API"""
        app = MDApp.get_running_app()
        if app.current_user:
            self.greeting_label.text = f"Hello, {app.current_user.get('fullName', 'Student')} 👋"

    def show_menu(self):
        """Show navigation menu"""
        dialog = MDDialog(
            title='Menu',
            type='simple',
            items=[
                MDFlatButton(
                    text='👤 Profile',
                    on_release=lambda x: self.goto_profile()
                ),
                MDFlatButton(
                    text='🔐 Admin Login',
                    on_release=lambda x: self.goto_admin_login()
                ),
                MDFlatButton(
                    text='📚 My Courses',
                    on_release=lambda x: self.goto_courses()
                ),
                MDFlatButton(
                    text='📝 Premium Notes',
                    on_release=lambda x: self.goto_notes()
                ),
                MDFlatButton(
                    text='⚙️ Settings',
                    on_release=lambda x: self.show_settings()
                ),
                MDFlatButton(
                    text='🚪 Logout',
                    on_release=lambda x: self.logout()
                ),
            ]
        )
        dialog.open()

    def goto_profile(self):
        self.manager.current = 'profile'

    def goto_admin_login(self):
        self.manager.current = 'admin_login'

    def goto_courses(self):
        self.manager.current = 'courses'

    def goto_notes(self):
        self.manager.current = 'premium_notes'

    def show_settings(self):
        Snackbar(text='Settings coming soon').open()

    def show_notifications(self):
        """Show notifications"""
        self.manager.current = 'notifications'

    def logout(self):
        app = MDApp.get_running_app()
        app.logout()
        Snackbar(text='Logged out').open()