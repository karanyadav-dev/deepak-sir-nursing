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


class ClassroomScreen(MDScreen):
    """Classroom - admin/instructor only"""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.name = 'classroom'
        self.build_ui()

    def build_ui(self):
        self.layout = MDBoxLayout(orientation='vertical', spacing=dp(0))

        # Top bar
        self.top_bar = MDTopAppBar(
            title='Classroom',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            elevation=2
        )

        # Content
        self.scroll = MDScrollView()
        self.content = MDBoxLayout(
            orientation='vertical',
            padding=dp(20),
            spacing=dp(15),
            adaptive_size=True,
            pos_hint={'center_x': 0.5, 'center_y': 0.5}
        )

        title = MDLabel(
            text='🎓 Classroom',
            font_style='H4',
            halign='center',
            size_hint_y=None,
            height=dp(60)
        )

        subtitle = MDLabel(
            text='Teaching Mode (Admin/Instructor Only)',
            font_style='Subtitle1',
            halign='center',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(30)
        )

        self.content.add_widget(title)
        self.content.add_widget(subtitle)

        # Classroom actions
        actions = [
            ('📊', 'Present PPT', 'Start PPT presentation'),
            ('📄', 'Open PDF', 'Open PDF document'),
            ('🧊', '3D Anatomy', 'Interactive 3D models'),
            ('🎥', 'Play Video', 'Play lecture video'),
            ('✏️', 'Whiteboard', 'Digital whiteboard'),
            ('🖥️', 'Projector Mode', 'Project to second screen'),
        ]

        for icon, label, desc in actions:
            card = MDCard(
                orientation='horizontal',
                padding=dp(15),
                spacing=dp(15),
                size_hint=(None, None),
                size=(dp(320), dp(70)),
                elevation=2,
                radius=[dp(12)],
                pos_hint={'center_x': 0.5}
            )

            icon_label = MDLabel(
                text=icon,
                font_style='H4',
                size_hint_x=0.15
            )

            text_box = MDBoxLayout(orientation='vertical', size_hint_x=0.85)

            name_label = MDLabel(
                text=label,
                font_style='Subtitle1',
                size_hint_y=None,
                height=dp(25)
            )

            desc_label = MDLabel(
                text=desc,
                font_style='Caption',
                theme_text_color='Secondary',
                size_hint_y=None,
                height=dp(20)
            )

            text_box.add_widget(name_label)
            text_box.add_widget(desc_label)

            card.add_widget(icon_label)
            card.add_widget(text_box)
            card.bind(on_release=lambda x, l=label: self.handle_action(l))

            self.content.add_widget(card)

        self.scroll.add_widget(self.content)

        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)

        self.add_widget(self.layout)

    def on_enter(self):
        """Check if user is admin - otherwise block access"""
        app = MDApp.get_running_app()
        if not getattr(app, 'is_admin', False):
            Snackbar(text='Access denied: Admin only').open()
            self.manager.current = 'home'

    def handle_action(self, action):
        Snackbar(text=f'Opening: {action}').open()

    def go_back(self):
        self.manager.current = 'admin_dashboard'