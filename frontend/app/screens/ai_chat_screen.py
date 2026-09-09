from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDIconButton, MDRaisedButton, MDFlatButton
from kivymd.uix.textfield import MDTextField
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.dialog import MDDialog
from kivymd.uix.menu import MDDropdownMenu
from kivymd.uix.chip import MDChip
from kivymd.uix.progressbar import MDProgressBar
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.fitimage import FitImage
from kivy.metrics import dp
from kivy.clock import Clock
from kivy.uix.image import Image
from kivy.uix.behaviors import ButtonBehavior
from kivy.properties import StringProperty, BooleanProperty, ListProperty
from kivy.core.window import Window
from plyer import camera, filechooser
import os
from datetime import datetime

class MessageBubble(MDCard):
    """Chat message bubble"""
    
    content = StringProperty("")
    is_user = BooleanProperty(False)
    image_path = StringProperty("")
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        self.padding = dp(10)
        self.spacing = dp(5)
        self.size_hint_y = None
        self.elevation = 1
        self.radius = [dp(15)]
        
        if self.is_user:
            self.md_bg_color = [0.2, 0.6, 1, 1]
            self.pos_hint = {'right': 1}
        else:
            self.md_bg_color = [0.95, 0.95, 0.95, 1]
            self.pos_hint = {'left': 1}
        
        self.build_ui()
    
    def build_ui(self):
        """Build message bubble UI"""
        if self.image_path:
            # Image message
            img = FitImage(
                source=self.image_path,
                size_hint_y=None,
                height=dp(200)
            )
            self.add_widget(img)
            self.height = dp(250)
        
        if self.content:
            label = MDLabel(
                text=self.content,
                font_style='Body1',
                theme_text_color='Custom',
                text_color=[1, 1, 1, 1] if self.is_user else [0, 0, 0, 1],
                size_hint_y=None,
                height=self.calculate_height()
            )
            self.add_widget(label)
            self.height += label.height + dp(20)
    
    def calculate_height(self):
        """Calculate label height based on content length"""
        # Rough calculation
        lines = len(self.content) / 40 + 1
        return dp(lines * 20)


class AIChatScreen(MDScreen):
    """AI Assistant chat screen"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.ai_service = None
        self.current_chat_id = None
        self.messages = []
        self.selected_image = None
        self.language = 'en'
        self.mode = 'GENERAL'
        self.is_loading = False
        self.build_ui()
    
    def build_ui(self):
        """Build AI chat screen UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='ðŸ©º Nursing AI Assistant',
            left_action_items=[
                ['arrow-left', lambda x: self.go_back()],
                ['plus', lambda x: self.new_chat()]
            ],
            right_action_items=[
                ['translate', lambda x: self.show_language_menu()],
                ['dots-vertical', lambda x: self.show_options()]
            ],
            elevation=2
        )
        
        # Mode chips
        self.mode_bar = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(5),
            size_hint_y=None,
            height=dp(40)
        )
        
        self.modes = ['General', 'Explain', 'MCQ', 'Notes', 'Care Plan', 'Medicine']
        self.mode_chips = []
        
        for mode in self.modes:
            chip = MDChip(
                text=mode,
                size_hint=(None, None),
                size=(dp(80), dp(35)),
                on_release=lambda x, m=mode: self.select_mode(m)
            )
            self.mode_chips.append(chip)
            self.mode_bar.add_widget(chip)
        
        # Chat area
        self.chat_scroll = MDScrollView()
        self.chat_container = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(10), dp(15), dp(10)],
            spacing=dp(10),
            adaptive_height=True
        )
        
        # Quick prompts
        self.quick_prompts = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(5),
            size_hint_y=None,
            height=dp(40)
        )
        
        quick_prompts = ['ðŸ“š Explain', 'ðŸ§  MCQs', 'ðŸ“ Notes', 'ðŸ‘©â€âš•ï¸ Care Plan']
        for prompt in quick_prompts:
            chip = MDChip(
                text=prompt,
                size_hint=(None, None),
                size=(dp(100), dp(35)),
                on_release=lambda x, p=prompt: self.quick_action(p)
            )
            self.quick_prompts.add_widget(chip)
        
        self.chat_scroll.add_widget(self.chat_container)
        
        # Image preview area
        self.image_preview = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(0)
        )
        
        # Input area
        self.input_area = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(60)
        )
        
        self.camera_btn = MDIconButton(
            icon='camera',
            size_hint=(None, None),
            size=(dp(40), dp(40)),
            on_release=self.take_photo
        )
        
        self.gallery_btn = MDIconButton(
            icon='image',
            size_hint=(None, None),
            size=(dp(40), dp(40)),
            on_release=self.pick_image
        )
        
        self.text_input = MDTextField(
            hint_text='Ask me anything about nursing...',
            mode='rectangle',
            size_hint_x=0.7,
            multiline=False
        )
        
        self.send_btn = MDIconButton(
            icon='send',
            size_hint=(None, None),
            size=(dp(40), dp(40)),
            on_release=self.send_message
        )
        
        self.input_area.add_widget(self.camera_btn)
        self.input_area.add_widget(self.gallery_btn)
        self.input_area.add_widget(self.text_input)
        self.input_area.add_widget(self.send_btn)
        
        # Loading indicator
        self.loading_bar = MDProgressBar(
            size_hint_y=None,
            height=dp(3)
        )
        self.loading_bar.opacity = 0
        
        # Add all to layout
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.mode_bar)
        self.layout.add_widget(self.chat_scroll)
        self.layout.add_widget(self.quick_prompts)
        self.layout.add_widget(self.image_preview)
        self.layout.add_widget(self.loading_bar)
        self.layout.add_widget(self.input_area)
        
        self.add_widget(self.layout)
    
    def on_enter(self):
        """Initialize when screen is displayed"""
        app = MDApp.get_running_app()
        self.ai_service = app.ai_service
        
        if not self.current_chat_id:
            self.load_recent_chat()
        else:
            self.load_chat_messages()
        
        # Show welcome message if no messages
        if not self.messages:
            self.show_welcome_message()
    
    def load_recent_chat(self):
        """Load most recent chat"""
        if not self.ai_service:
            return
        
        result = self.ai_service.get_chats(limit=1)
        if result.get('success'):
            chats = result.get('data', [])
            if chats:
                self.current_chat_id = chats[0]['id']
                self.load_chat_messages()
    
    def load_chat_messages(self):
        """Load messages for current chat"""
        if not self.current_chat_id or not self.ai_service:
            return
        
        result = self.ai_service.get_chat(self.current_chat_id)
        if result.get('success'):
            chat = result.get('data', {})
            self.messages = chat.get('messages', [])
            self.display_messages()
    
    def display_messages(self):
        """Display all messages"""
        self.chat_container.clear_widgets()
        
        for message in self.messages:
            is_user = message.get('role') == 'USER'
            bubble = MessageBubble(
                content=message.get('content', ''),
                is_user=is_user,
                image_path=message.get('imageUrl', '')
            )
            self.chat_container.add_widget(bubble)
        
        # Scroll to bottom
        Clock.schedule_once(lambda dt: self.scroll_to_bottom(), 0.1)
    
    def scroll_to_bottom(self):
        """Scroll chat to bottom"""
        if self.chat_container.height > self.chat_scroll.height:
            self.chat_scroll.scroll_y = 0
    
    def show_welcome_message(self):
        """Show welcome message"""
        welcome_text = """ðŸ‘‹ Hello! I'm your Nursing AI Assistant.

I can help you with:
ðŸ“š Explaining nursing topics
ðŸ§  Generating MCQs
ðŸ“ Making study notes
ðŸ‘©â€âš•ï¸ Creating care plans
ðŸ’Š Medicine information
ðŸ”¬ Explaining medical images

What would you like to learn today?"""
        
        welcome_bubble = MessageBubble(
            content=welcome_text,
            is_user=False
        )
        self.chat_container.add_widget(welcome_bubble)
    
    def send_message(self, instance):
        """Send message to AI"""
        content = self.text_input.text.strip()
        
        if not content and not self.selected_image:
            Snackbar(text='Please enter a message').open()
            return
        
        if self.is_loading:
            Snackbar(text='Please wait for previous response').open()
            return
        
        # Add user message to chat
        user_bubble = MessageBubble(
            content=content,
            is_user=True,
            image_path=self.selected_image
        )
        self.chat_container.add_widget(user_bubble)
        
        # Clear input
        self.text_input.text = ''
        selected_image = self.selected_image
        self.selected_image = None
        self.hide_image_preview()
        
        # Show loading
        self.show_loading()
        
        # Send to API
        Clock.schedule_once(lambda dt: self.process_message(content, selected_image), 0.1)
    
    def process_message(self, content, image_path):
        """Process message through API"""
        if not self.ai_service:
            self.hide_loading()
            self.show_error('AI service not available')
            return
        
        if not self.current_chat_id:
            # Create new chat
            result = self.ai_service.create_chat(
                title=content[:50] if content else 'New Chat',
                mode=self.mode.upper().replace(' ', '_')
            )
            
            if result.get('success'):
                self.current_chat_id = result['data']['id']
            else:
                self.hide_loading()
                self.show_error('Failed to create chat')
                return
        
        # Send message
        result = self.ai_service.send_message(
            self.current_chat_id,
            content,
            image_path,
            self.language
        )
        
        self.hide_loading()
        
        if result.get('success'):
            ai_message = result.get('data', {})
            ai_bubble = MessageBubble(
                content=ai_message.get('content', ''),
                is_user=False
            )
            self.chat_container.add_widget(ai_bubble)
            self.scroll_to_bottom()
        else:
            self.show_error(result.get('message', 'Failed to get AI response'))
    
    def new_chat(self, instance=None):
        """Start new chat"""
        self.current_chat_id = None
        self.messages = []
        self.chat_container.clear_widgets()
        self.show_welcome_message()
        Snackbar(text='New chat started').open()
    
    def select_mode(self, mode):
        """Select AI mode"""
        self.mode = mode.upper().replace(' ', '_')
        
        # Update chip colors
        for chip in self.mode_chips:
            if chip.text == mode:
                chip.md_bg_color = [0.2, 0.6, 1, 1]
                chip.text_color = [1, 1, 1, 1]
            else:
                chip.md_bg_color = [1, 1, 1, 1]
                chip.text_color = [0, 0, 0, 1]
        
        Snackbar(text=f'Mode: {mode}').open()
    
    def quick_action(self, prompt):
        """Handle quick action prompts"""
        actions = {
            'ðŸ“š Explain': 'Explain this topic: ',
            'ðŸ§  MCQs': 'Generate 20 MCQs on: ',
            'ðŸ“ Notes': 'Make short notes on: ',
            'ðŸ‘©â€âš•ï¸ Care Plan': 'Create nursing care plan for: '
        }
        
        self.text_input.text = actions.get(prompt, '')
        self.text_input.focus = True
    
    def take_photo(self, instance):
        """Take photo using camera"""
        try:
            camera.take_picture(
                filename=os.path.join(Config.DATA_DIR, 'ai_photo.jpg'),
                on_complete=self.on_camera_complete
            )
        except Exception as e:
            self.show_error('Camera not available')
    
    def on_camera_complete(self, filepath):
        """Handle captured photo"""
        if filepath:
            self.selected_image = filepath
            self.show_image_preview(filepath)
    
    def pick_image(self, instance):
        """Pick image from gallery"""
        try:
            filechooser.open_file(
                title='Select Image',
                filters=[('Images', '*.jpg', '*.jpeg', '*.png', '*.webp')],
                on_selection=self.on_image_selected
            )
        except Exception as e:
            self.show_error('Gallery not available')
    
    def on_image_selected(self, selection):
        """Handle selected image"""
        if selection:
            self.selected_image = selection[0]
            self.show_image_preview(selection[0])
    
    def show_image_preview(self, image_path):
        """Show selected image preview"""
        self.image_preview.clear_widgets()
        self.image_preview.height = dp(100)
        
        # Image thumbnail
        img = FitImage(
            source=image_path,
            size_hint=(None, None),
            size=(dp(80), dp(80))
        )
        
        # Remove button
        remove_btn = MDIconButton(
            icon='close',
            size_hint=(None, None),
            size=(dp(30), dp(30)),
            on_release=lambda x: self.remove_image()
        )
        
        self.image_preview.add_widget(img)
        self.image_preview.add_widget(remove_btn)
    
    def hide_image_preview(self):
        """Hide image preview"""
        self.image_preview.clear_widgets()
        self.image_preview.height = dp(0)
    
    def remove_image(self):
        """Remove selected image"""
        self.selected_image = None
        self.hide_image_preview()
    
    def show_language_menu(self):
        """Show language selection menu"""
        menu_items = [
            {'text': 'English', 'viewclass': 'OneLineListItem', 
             'on_release': lambda x='English': self.select_language('en', x)},
            {'text': 'à¤¹à¤¿à¤‚à¤¦à¥€', 'viewclass': 'OneLineListItem', 
             'on_release': lambda x='à¤¹à¤¿à¤‚à¤¦à¥€': self.select_language('hi', x)},
            {'text': 'Hinglish', 'viewclass': 'OneLineListItem', 
             'on_release': lambda x='Hinglish': self.select_language('hinglish', x)},
        ]
        
        self.language_menu = MDDropdownMenu(
            caller=self.top_bar,
            items=menu_items,
            width_mult=3
        )
        self.language_menu.open()
    
    def select_language(self, lang_code, lang_name):
        """Select response language"""
        self.language = lang_code
        Snackbar(text=f'Language: {lang_name}').open()
        if hasattr(self, 'language_menu'):
            self.language_menu.dismiss()
    
    def show_options(self):
        """Show additional options"""
        dialog = MDDialog(
            title='Chat Options',
            type='simple',
            items=[
                MDFlatButton(
                    text='Clear Chat',
                    on_release=lambda x: self.clear_chat()
                ),
                MDFlatButton(
                    text='Delete Chat',
                    on_release=lambda x: self.delete_chat()
                ),
                MDFlatButton(
                    text='Cancel',
                    on_release=lambda x: dialog.dismiss()
                ),
            ]
        )
        dialog.open()
    
    def clear_chat(self):
        """Clear current chat"""
        self.messages = []
        self.chat_container.clear_widgets()
        self.show_welcome_message()
    
    def delete_chat(self):
        """Delete current chat"""
        if self.current_chat_id and self.ai_service:
            result = self.ai_service.delete_chat(self.current_chat_id)
            if result.get('success'):
                self.new_chat()
                Snackbar(text='Chat deleted').open()
    
    def show_loading(self):
        """Show loading indicator"""
        self.is_loading = True
        self.loading_bar.opacity = 1
        self.send_btn.disabled = True
    
    def hide_loading(self):
        """Hide loading indicator"""
        self.is_loading = False
        self.loading_bar.opacity = 0
        self.send_btn.disabled = False
    
    def show_error(self, message):
        """Show error message"""
        Snackbar(
            text=message,
            snackbar_x='10dp',
            snackbar_y='10dp',
            size_hint_x=0.9
        ).open()
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'home'

