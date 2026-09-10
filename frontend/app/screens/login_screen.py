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


class LoginScreen(MDScreen):
    """Login screen for the application"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.name = 'login'
        self.api_service = None
        self.build_ui()

    def build_ui(self):
        """Build the login UI"""
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
            size=(dp(150), dp(150)),
            pos_hint={'center_x': 0.5}
        )

        # App name
        self.app_name = MDLabel(
            text='DEEPAK SIR',
            font_style='H4',
            halign='center',
            theme_text_color='Primary',
            size_hint_y=None,
            height=dp(40)
        )

        self.app_subtitle = MDLabel(
            text='NURSING APP',
            font_style='H6',
            halign='center',
            theme_text_color='Custom',
            text_color=[0.8, 0.2, 0.2, 1],
            size_hint_y=None,
            height=dp(30)
        )

        self.tagline = MDLabel(
            text='Your Success, Our Mission',
            font_style='Caption',
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(20)
        )

        # Email field
        self.email_field = MDTextField(
            hint_text='Email',
            helper_text='Enter your email address',
            helper_text_mode='on_focus',
            mode='rectangle',
            size_hint_y=None,
            height=dp(50)
        )

        # Password field
        self.password_field = MDTextField(
            hint_text='Password',
            helper_text='Enter your password',
            helper_text_mode='on_focus',
            mode='rectangle',
            password=True,
            size_hint_y=None,
            height=dp(50)
        )

        # Login button
        self.login_button = MDRaisedButton(
            text='LOGIN',
            pos_hint={'center_x': 0.5},
            size_hint=(1, None),
            height=dp(50),
            on_release=self.login
        )

        # Register button
        self.register_button = MDTextButton(
            text='New user? Register here',
            pos_hint={'center_x': 0.5},
            size_hint=(1, None),
            height=dp(40),
            on_release=self.go_to_register
        )

        # Forgot password button
        self.forgot_button = MDTextButton(
            text='Forgot password?',
            pos_hint={'center_x': 0.5},
            size_hint=(1, None),
            height=dp(40),
            on_release=self.forgot_password
        )

        # Progress bar
        self.progress = MDProgressBar(
            size_hint=(1, None),
            height=dp(3)
        )
        self.progress.opacity = 0

        # Add all widgets
        self.layout.add_widget(self.logo)
        self.layout.add_widget(self.app_name)
        self.layout.add_widget(self.app_subtitle)
        self.layout.add_widget(self.tagline)
        self.layout.add_widget(self.email_field)
        self.layout.add_widget(self.password_field)
        self.layout.add_widget(self.login_button)
        self.layout.add_widget(self.register_button)
        self.layout.add_widget(self.forgot_button)
        self.layout.add_widget(self.progress)

        self.add_widget(self.layout)

    def on_enter(self):
        self.api_service = MDApp.get_running_app().api_service

    def login(self, instance):
        email = self.email_field.text.strip()
        password = self.password_field.text

        if not email or not password:
            self.show_error('Please enter email and password')
            return

        self.show_loading()

        def login_callback(dt):
            result = self.api_service.post(
                '/auth/login',
                {'email': email, 'password': password},
                include_auth=False
            )

            self.hide_loading()

            if result.get('success'):
                data = result.get('data', {})
                app = MDApp.get_running_app()
                app.auth_token = data.get('accessToken')
                app.current_user = data.get('user')

                app.user_store.put('session',
                    token=data.get('accessToken'),
                    refresh_token=data.get('refreshToken'),
                    user=data.get('user')
                )

                self.api_service.save_tokens(
                    data.get('accessToken'),
                    data.get('refreshToken')
                )

                self.manager.current = 'home'
            else:
                self.show_error(result.get('message', 'Login failed'))

        Clock.schedule_once(login_callback, 0.1)

    def go_to_register(self, instance):
        self.manager.current = 'register'

    def forgot_password(self, instance):
        self.show_error('Forgot password feature coming soon')

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