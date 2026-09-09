from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.textfield import MDTextField
from kivymd.uix.button import MDRaisedButton, MDTextButton
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.progressbar import MDProgressBar
from kivy.clock import Clock
from kivy.metrics import dp

class RegisterScreen(MDScreen):
    """Registration screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.build_ui()
    
    def build_ui(self):
        """Build registration UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            padding=dp(30),
            spacing=dp(15),
            pos_hint={'center_x': 0.5, 'center_y': 0.5}
        )
        
        # Title
        title = MDLabel(
            text='Create Account',
            font_style='H4',
            halign='center',
            size_hint_y=None,
            height=dp(50)
        )
        
        # Full name field
        self.fullname_field = MDTextField(
            hint_text='Full Name',
            mode='rectangle',
            size_hint_y=None,
            height=dp(50)
        )
        
        # Email field
        self.email_field = MDTextField(
            hint_text='Email',
            mode='rectangle',
            size_hint_y=None,
            height=dp(50)
        )
        
        # Phone field
        self.phone_field = MDTextField(
            hint_text='Phone Number',
            mode='rectangle',
            size_hint_y=None,
            height=dp(50)
        )
        
        # Password field
        self.password_field = MDTextField(
            hint_text='Password',
            mode='rectangle',
            password=True,
            size_hint_y=None,
            height=dp(50)
        )
        
        # Confirm password field
        self.confirm_password_field = MDTextField(
            hint_text='Confirm Password',
            mode='rectangle',
            password=True,
            size_hint_y=None,
            height=dp(50)
        )
        
        # Register button
        self.register_btn = MDRaisedButton(
            text='REGISTER',
            size_hint=(1, None),
            height=dp(50),
            on_release=self.register
        )
        
        # Login button
        login_btn = MDTextButton(
            text='Already have an account? Login',
            pos_hint={'center_x': 0.5},
            size_hint=(1, None),
            height=dp(40),
            on_release=lambda x: setattr(self.manager, 'current', 'login')
        )
        
        # Add all widgets
        self.layout.add_widget(title)
        self.layout.add_widget(self.fullname_field)
        self.layout.add_widget(self.email_field)
        self.layout.add_widget(self.phone_field)
        self.layout.add_widget(self.password_field)
        self.layout.add_widget(self.confirm_password_field)
        self.layout.add_widget(self.register_btn)
        self.layout.add_widget(login_btn)
        
        self.add_widget(self.layout)
    
    def register(self, instance):
        """Handle registration"""
        fullname = self.fullname_field.text.strip()
        email = self.email_field.text.strip()
        phone = self.phone_field.text.strip()
        password = self.password_field.text
        confirm_password = self.confirm_password_field.text
        
        # Validate
        if not fullname:
            self.show_error('Please enter your full name')
            return
        
        if not email:
            self.show_error('Please enter your email')
            return
        
        if not phone:
            self.show_error('Please enter your phone number')
            return
        
        if not password:
            self.show_error('Please enter a password')
            return
        
        if password != confirm_password:
            self.show_error('Passwords do not match')
            return
        
        if len(password) < 6:
            self.show_error('Password must be at least 6 characters')
            return
        
        app = MDApp.get_running_app()
        
        result = app.api_service.post('/auth/register', {
            'fullName': fullname,
            'email': email,
            'phone': phone,
            'password': password
        }, include_auth=False)
        
        if result.get('success'):
            Snackbar(text='Registration successful! Please login.').open()
            self.manager.current = 'login'
        else:
            self.show_error(result.get('message', 'Registration failed'))
    
    def show_error(self, message):
        """Show error message"""
        Snackbar(
            text=message,
            snackbar_x='10dp',
            snackbar_y='10dp',
            size_hint_x=0.9
        ).open()

