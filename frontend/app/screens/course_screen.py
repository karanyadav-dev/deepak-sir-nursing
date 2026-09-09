from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDIconButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.chip import MDChip
from kivy.metrics import dp
from kivy.clock import Clock

class CourseCard(MDCard):
    """Card for displaying course"""
    
    def __init__(self, course_data, **kwargs):
        super().__init__(**kwargs)
        self.course_data = course_data
        self.orientation = 'vertical'
        self.size_hint_y = None
        self.height = dp(200)
        self.elevation = 2
        self.radius = [dp(15)]
        
        self.build_ui()
    
    def build_ui(self):
        """Build course card UI"""
        # Title
        title = MDLabel(
            text=self.course_data.get('title', 'Course Title'),
            font_style='H6',
            size_hint_y=None,
            height=dp(40)
        )
        
        # Description
        desc = MDLabel(
            text=self.course_data.get('description', '')[:100] + '...',
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(60)
        )
        
        # Price and enroll button
        bottom_row = MDBoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height=dp(50)
        )
        
        price_label = MDLabel(
            text=f"₹{self.course_data.get('price', 0)}",
            font_style='Subtitle1',
            theme_text_color='Primary',
            size_hint_x=0.5
        )
        
        enroll_btn = MDRaisedButton(
            text='ENROLL',
            size_hint=(None, None),
            size=(dp(100), dp(40)),
            on_release=self.enroll
        )
        
        bottom_row.add_widget(price_label)
        bottom_row.add_widget(enroll_btn)
        
        self.add_widget(title)
        self.add_widget(desc)
        self.add_widget(bottom_row)
    
    def enroll(self, instance):
        """Handle enrollment"""
        app = MDApp.get_running_app()
        # Navigate to payment or directly enroll if free
        if self.course_data.get('isPremium'):
            app.screen_manager.current = 'payment'
        else:
            # Enroll directly
            pass


class CoursesScreen(MDScreen):
    """Courses screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.courses = []
        self.build_ui()
    
    def build_ui(self):
        """Build courses screen UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Courses',
            left_action_items=[['menu', lambda x: self.show_menu()]],
            right_action_items=[['search', lambda x: self.show_search()]],
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
        """Load courses when screen is displayed"""
        Clock.schedule_once(self.load_courses, 0.1)
    
    def load_courses(self, dt):
        """Load courses from API"""
        app = MDApp.get_running_app()
        result = app.api_service.get('/courses')
        
        if result.get('success'):
            self.courses = result.get('data', [])
            self.display_courses()
    
    def display_courses(self):
        """Display course cards"""
        self.content.clear_widgets()
        
        if not self.courses:
            # Show empty state
            empty_label = MDLabel(
                text='No courses available',
                halign='center',
                theme_text_color='Secondary'
            )
            self.content.add_widget(empty_label)
            return
        
        for course in self.courses:
            course_card = CourseCard(course)
            self.content.add_widget(course_card)
    
    def show_menu(self):
        """Show menu"""
        pass
    
    def show_search(self):
        """Show search"""
        pass