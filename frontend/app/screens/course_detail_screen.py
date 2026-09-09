from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDIconButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.progressbar import MDProgressBar
from kivy.metrics import dp
from kivy.clock import Clock
import json

class CourseDetailScreen(MDScreen):
    """Course detail screen showing chapters and lessons"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.course_id = None
        self.course_data = None
        self.build_ui()
    
    def build_ui(self):
        """Build course detail UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Course Details',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['bookmark-outline', lambda x: self.bookmark_course()]],
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
        
        # Bottom enroll button
        self.bottom_bar = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(60)
        )
        
        self.enroll_btn = MDRaisedButton(
            text='ENROLL NOW',
            size_hint=(1, None),
            height=dp(45),
            on_release=self.enroll_course
        )
        
        self.bottom_bar.add_widget(self.enroll_btn)
        
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)
        self.layout.add_widget(self.bottom_bar)
        
        self.add_widget(self.layout)
    
    def load_course(self, course_id, course_data=None):
        """Load course details"""
        self.course_id = course_id
        self.course_data = course_data
        self.display_course_info()
        self.load_chapters()
    
    def display_course_info(self):
        """Display course basic info"""
        self.content.clear_widgets()
        
        if not self.course_data:
            return
        
        # Course title card
        title_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(120),
            elevation=2,
            radius=[dp(15)]
        )
        
        title_label = MDLabel(
            text=self.course_data.get('title', 'Course Title'),
            font_style='H5',
            size_hint_y=None,
            height=dp(40)
        )
        
        desc_label = MDLabel(
            text=self.course_data.get('description', '')[:150],
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(50)
        )
        
        price_label = MDLabel(
            text=f"₹{self.course_data.get('price', 0)}",
            font_style='Subtitle1',
            theme_text_color='Primary',
            size_hint_y=None,
            height=dp(30)
        )
        
        title_card.add_widget(title_label)
        title_card.add_widget(desc_label)
        title_card.add_widget(price_label)
        
        self.content.add_widget(title_card)
    
    def load_chapters(self):
        """Load chapters from API"""
        app = MDApp.get_running_app()
        
        if not self.course_id:
            return
        
        result = app.api_service.get(f'/courses/{self.course_id}/chapters')
        
        if result.get('success'):
            chapters = result.get('data', [])
            self.display_chapters(chapters)
        else:
            self.show_empty_state('No chapters available')
    
    def display_chapters(self, chapters):
        """Display chapters and lessons"""
        if not chapters:
            self.show_empty_state('No chapters available')
            return
        
        for chapter in chapters:
            chapter_card = MDCard(
                orientation='vertical',
                padding=dp(15),
                size_hint_y=None,
                height=dp(200),
                elevation=1,
                radius=[dp(10)]
            )
            
            chapter_title = MDLabel(
                text=chapter.get('title', f"Chapter {chapter.get('orderIndex', '')}"),
                font_style='Subtitle1',
                size_hint_y=None,
                height=dp(35)
            )
            
            chapter_card.add_widget(chapter_title)
            
            # Lessons
            lessons = chapter.get('lessons', [])
            for lesson in lessons:
                lesson_btn = MDRaisedButton(
                    text=f"  📹 {lesson.get('title', 'Lesson')} ({lesson.get('durationMinutes', 0)} min)",
                    size_hint_y=None,
                    height=dp(45),
                    on_release=lambda x, l=lesson: self.open_lesson(l)
                )
                chapter_card.add_widget(lesson_btn)
                chapter_card.height += dp(50)
            
            self.content.add_widget(chapter_card)
    
    def open_lesson(self, lesson):
        """Open lesson with video"""
        app = MDApp.get_running_app()
        
        # Check if there's a video player screen
        if app.screen_manager.has_screen('video_player'):
            video_screen = app.screen_manager.get_screen('video_player')
            video_screen.load_lesson(lesson)
            app.screen_manager.current = 'video_player'
        else:
            # Show lesson info
            Snackbar(text=f"Opening: {lesson.get('title', 'Lesson')}").open()
    
    def enroll_course(self, instance):
        """Enroll in course"""
        app = MDApp.get_running_app()
        
        result = app.api_service.post(f'/courses/{self.course_id}/enroll', {})
        
        if result.get('success'):
            Snackbar(text='Enrolled successfully!').open()
            self.enroll_btn.text = 'ENROLLED'
            self.enroll_btn.disabled = True
        else:
            Snackbar(text=result.get('message', 'Enrollment failed')).open()
    
    def bookmark_course(self):
        """Bookmark course"""
        app = MDApp.get_running_app()
        result = app.api_service.post(
            '/bookmarks',
            {'type': 'COURSE', 'itemId': self.course_id}
        )
        
        if result.get('success'):
            Snackbar(text='Course bookmarked!').open()
        else:
            Snackbar(text='Failed to bookmark').open()
    
    def show_empty_state(self, message):
        """Show empty state"""
        empty_label = MDLabel(
            text=message,
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(100)
        )
        self.content.add_widget(empty_label)
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'courses'