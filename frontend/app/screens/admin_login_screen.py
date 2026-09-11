from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.textfield import MDTextField
from kivymd.uix.button import MDRaisedButton, MDTextButton
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.progressbar import MDProgressBar
from kivy.uix.image import Image
from kivy.clock import Clock
from kivy.metrics import dp


class AdminLoginScreen(MDScreen):
    """Admin-only login screen"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.name = 'admin_login'
        self.api_service = None
        self.build_ui()

    def build_ui(self):
        """Build admin login UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            padding=dp(30),
            spacing=dp(15),
            pos_hint={'center_x': 0.5, 'center_y': 0.5}
        )

        # Logo
        self.logo = Image(
            source='app/assets/images/icon.png',
            size_hint=(None, None),
            size=(dp(120), dp(120)),
            pos_hint={'center_x': 0.5}
        )

        # Title
        self.title_label = MDLabel(
            text='ADMIN PANEL',
            font_style='H4',
            halign='center',
            theme_text_color='Primary',
            size_hint_y=None,
            height=dp(40)
        )

        self.subtitle = MDLabel(
            text='Deepak Sir Nursing',
            font_style='Subtitle1',
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(25)
        )

        self.notice = MDLabel(
            text='⚠️ Admin access only',
            font_style='Caption',
            halign='center',
            theme_text_color='Custom',
            text_color=[0.8, 0.2, 0.2, 1],
            size_hint_y=None,
            height=dp(20)
        )

        # Email field
        self.email_field = MDTextField(
            hint_text='Admin Email',
            helper_text='Enter admin email',
            helper_text_mode='on_focus',
            mode='rectangle',
            size_hint_y=None,
            height=dp(50)
        )

        # Password field
        self.password_field = MDTextField(
            hint_text='Admin Password',
            helper_text='Enter admin password',
            helper_text_mode='on_focus',
            mode='rectangle',
            password=True,
            size_hint_y=None,
            height=dp(50)
        )

        # Login button
        self.login_button = MDRaisedButton(
            text='ADMIN LOGIN',
            pos_hint={'center_x': 0.5},
            size_hint=(1, None),
            height=dp(50),
            on_release=self.admin_login
        )

        # Back to student login
        self.back_button = MDTextButton(
            text='← Back to Student Login',
            pos_hint={'center_x': 0.5},
            size_hint=(1, None),
            height=dp(40),
            on_release=self.go_back
        )

        # Progress
        self.progress = MDProgressBar(
            size_hint=(1, None),
            height=dp(3)
        )
        self.progress.opacity = 0

        # Add widgets
        self.layout.add_widget(self.logo)
        self.layout.add_widget(self.title_label)
        self.layout.add_widget(self.subtitle)
        self.layout.add_widget(self.notice)
        self.layout.add_widget(self.email_field)
        self.layout.add_widget(self.password_field)
        self.layout.add_widget(self.login_button)
        self.layout.add_widget(self.back_button)
        self.layout.add_widget(self.progress)

        self.add_widget(self.layout)

    def on_enter(self):
        self.api_service = MDApp.get_running_app().api_service

    def admin_login(self, instance):
        """Admin login - uses /admin/auth/login endpoint"""
        email = self.email_field.text.strip()
        password = self.password_field.text

        if not email or not password:
            self.show_error('Please enter email and password')
            return

        self.show_loading()

        def login_callback(dt):
            result = self.api_service.post(
                '/admin/auth/login',
                {'email': email, 'password': password},
                include_auth=False
            )

            self.hide_loading()

            if result.get('success'):
                data = result.get('data', {})
                app = MDApp.get_running_app()
                app.auth_token = data.get('accessToken')
                app.current_user = data.get('user')
                app.is_admin = True

                app.user_store.put('session',
                    token=data.get('accessToken'),
                    refresh_token=data.get('refreshToken'),
                    user=data.get('user'),
                    is_admin=True
                )

                self.api_service.save_tokens(
                    data.get('accessToken'),
                    data.get('refreshToken')
                )

                # Navigate to admin dashboard
                self.manager.current = 'admin_dashboard'
            else:
                self.show_error(result.get('message', 'Admin login failed'))

        Clock.schedule_once(login_callback, 0.1)

    def go_back(self, instance):
        self.manager.current = 'login'

    def show_loading(self):
        self.progress.opacity = 1
        self.login_button.disabled = True

    def hide_loading(self):
        self.progress.opacity = 0
        self.login_button.disabled = False

    def show_error(self, message):
        Snackbar(
            text=message,
            snackbar_x='10dp',
            snackbar_y='10dp',
            size_hint_x=0.9
        ).open()