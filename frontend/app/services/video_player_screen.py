from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDIconButton
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivy.metrics import dp
from kivy.clock import Clock
from kivy.uix.video import Video
from kivy.uix.progressbar import ProgressBar

class VideoPlayerScreen(MDScreen):
    """Video player screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.lesson_data = None
        self.video = None
        self.build_ui()
    
    def build_ui(self):
        """Build video player UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Lesson',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['check', lambda x: self.mark_complete()]],
            elevation=2
        )
        
        # Video area
        self.video_container = MDBoxLayout(
            orientation='vertical',
            size_hint_y=0.5
        )
        
        # Video placeholder
        self.video_placeholder = MDLabel(
            text='🎬 Video Player\n\nVideo will play here',
            halign='center',
            theme_text_color='Secondary'
        )
        
        self.video_container.add_widget(self.video_placeholder)
        
        # Lesson info area
        self.info_container = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(10), dp(15), dp(10)],
            spacing=dp(10)
        )
        
        self.lesson_title = MDLabel(
            text='',
            font_style='H6',
            size_hint_y=None,
            height=dp(40)
        )
        
        self.lesson_description = MDLabel(
            text='',
            font_style='Body2',
            theme_text_color='Secondary'
        )
        
        self.info_container.add_widget(self.lesson_title)
        self.info_container.add_widget(self.lesson_description)
        
        # Bottom controls
        self.controls = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(60)
        )
        
        self.prev_btn = MDRaisedButton(
            text='← PREV',
            size_hint=(1, None),
            height=dp(40)
        )
        
        self.complete_btn = MDRaisedButton(
            text='MARK COMPLETE',
            size_hint=(1, None),
            height=dp(40),
            on_release=self.mark_complete
        )
        
        self.next_btn = MDRaisedButton(
            text='NEXT →',
            size_hint=(1, None),
            height=dp(40)
        )
        
        self.controls.add_widget(self.prev_btn)
        self.controls.add_widget(self.complete_btn)
        self.controls.add_widget(self.next_btn)
        
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.video_container)
        self.layout.add_widget(self.info_container)
        self.layout.add_widget(self.controls)
        
        self.add_widget(self.layout)
    
    def load_lesson(self, lesson_data):
        """Load lesson data"""
        self.lesson_data = lesson_data
        self.lesson_title.text = lesson_data.get('title', 'Lesson')
        self.lesson_description.text = lesson_data.get('description', '')
        
        video_url = lesson_data.get('videoUrl')
        
        if video_url:
            self.play_video(video_url)
        else:
            self.video_placeholder.text = '📄 No video available for this lesson'
    
    def play_video(self, video_url):
        """Play video from URL"""
        try:
            # Remove placeholder
            self.video_container.remove_widget(self.video_placeholder)
            
            # Create video player
            self.video = Video(
                source=video_url,
                state='play',
                options={'eos': 'loop'}
            )
            
            self.video_container.add_widget(self.video)
        except Exception as e:
            self.video_placeholder.text = f'❌ Failed to load video: {str(e)}'
            self.video_container.add_widget(self.video_placeholder)
    
    def mark_complete(self, instance=None):
        """Mark lesson as complete"""
        if not self.lesson_data:
            return
        
        app = MDApp.get_running_app()
        lesson_id = self.lesson_data.get('id')
        
        result = app.api_service.post(f'/progress/complete-lesson', {
            'lessonId': lesson_id
        })
        
        if result.get('success'):
            Snackbar(text='Lesson marked as complete!').open()
            self.complete_btn.text = '✓ COMPLETED'
            self.complete_btn.disabled = True
        else:
            Snackbar(text='Failed to mark complete').open()
    
    def go_back(self):
        """Navigate back"""
        if self.video:
            self.video.state = 'stop'
        self.manager.current = 'course_detail'