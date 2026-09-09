from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDTextButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.gridlayout import MDGridLayout
from kivymd.uix.selectioncontrol import MDCheckbox
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.dialog import MDDialog
from kivymd.uix.button import MDFlatButton
from kivy.metrics import dp
from kivy.clock import Clock
from kivy.uix.widget import Widget

class TestScreen(MDScreen):
    """Test screen for mock tests"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.current_question_index = 0
        self.questions = []
        self.answers = []
        self.marked_for_review = []
        self.time_remaining = 0
        self.timer_event = None
        self.build_ui()
    
    def build_ui(self):
        """Build test screen UI"""
        # Main layout
        self.layout = MDBoxLayout(orientation='vertical', spacing=dp(0))
        
        # Top bar with timer
        self.top_bar = MDTopAppBar(
            title='Mock Test',
            left_action_items=[['arrow-left', lambda x: self.confirm_exit()]],
            elevation=2
        )
        
        # Timer display
        self.timer_label = MDLabel(
            text='⏱ 60:00',
            font_style='H6',
            theme_text_color='Primary',
            size_hint_y=None,
            height=dp(50),
            halign='center'
        )
        
        # Main content area
        self.content_box = MDBoxLayout(
            orientation='horizontal',
            spacing=dp(0)
        )
        
        # Question area (70% width)
        self.question_scroll = MDScrollView(
            size_hint_x=0.7
        )
        
        self.question_container = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(15), dp(15), dp(15)],
            spacing=dp(10),
            adaptive_height=True
        )
        
        # Question number
        self.question_number = MDLabel(
            text='Question 1 of 100',
            font_style='Subtitle1',
            theme_text_color='Primary',
            size_hint_y=None,
            height=dp(30)
        )
        
        # Question text
        self.question_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            size_hint_y=None,
            height=dp(150),
            elevation=2,
            radius=[dp(10)]
        )
        
        self.question_text = MDLabel(
            text='Question text will appear here',
            font_style='Body1',
            size_hint_y=None,
            height=dp(100)
        )
        
        self.question_card.add_widget(self.question_text)
        
        # Options
        self.options_container = MDBoxLayout(
            orientation='vertical',
            spacing=dp(8),
            size_hint_y=None,
            height=dp(260)
        )
        
        self.option_buttons = []
        option_labels = ['A', 'B', 'C', 'D']
        
        for i, label in enumerate(option_labels):
            option_card = self.create_option_card(label, i)
            self.option_buttons.append(option_card)
            self.options_container.add_widget(option_card)
        
        # Navigation buttons
        self.nav_box = MDBoxLayout(
            orientation='horizontal',
            spacing=dp(10),
            size_hint_y=None,
            height=dp(50),
            padding=[dp(0), dp(10), dp(0), dp(0)]
        )
        
        self.mark_review_button = MDTextButton(
            text='MARK FOR REVIEW',
            size_hint=(0.3, None),
            height=dp(40),
            on_release=self.mark_for_review
        )
        
        self.clear_button = MDTextButton(
            text='CLEAR',
            size_hint=(0.15, None),
            height=dp(40),
            on_release=self.clear_answer
        )
        
        self.prev_button = MDRaisedButton(
            text='PREV',
            size_hint=(0.25, None),
            height=dp(40),
            on_release=self.previous_question,
            disabled=True
        )
        
        self.next_button = MDRaisedButton(
            text='NEXT',
            size_hint=(0.3, None),
            height=dp(40),
            on_release=self.next_question
        )
        
        self.nav_box.add_widget(self.mark_review_button)
        self.nav_box.add_widget(self.clear_button)
        self.nav_box.add_widget(self.prev_button)
        self.nav_box.add_widget(self.next_button)
        
        # Add to question container
        self.question_container.add_widget(self.question_number)
        self.question_container.add_widget(self.question_card)
        self.question_container.add_widget(self.options_container)
        self.question_container.add_widget(self.nav_box)
        
        self.question_scroll.add_widget(self.question_container)
        
        # Question palette (30% width)
        self.palette_box = MDBoxLayout(
            orientation='vertical',
            size_hint_x=0.3,
            padding=[dp(10), dp(10), dp(10), dp(10)],
            spacing=dp(10)
        )
        
        # Palette header
        self.palette_header = MDLabel(
            text='Question Palette',
            font_style='Subtitle1',
            size_hint_y=None,
            height=dp(30)
        )
        
        # Palette grid
        self.palette_scroll = MDScrollView()
        self.palette_grid = MDGridLayout(
            cols=5,
            spacing=dp(5),
            padding=dp(5),
            size_hint_y=None,
            adaptive_height=True
        )
        
        # Create palette buttons
        self.palette_buttons = []
        for i in range(100):
            btn = self.create_palette_button(i + 1)
            self.palette_buttons.append(btn)
            self.palette_grid.add_widget(btn)
        
        self.palette_scroll.add_widget(self.palette_grid)
        
        # Legend
        self.legend_box = MDBoxLayout(
            orientation='vertical',
            spacing=dp(5),
            size_hint_y=None,
            height=dp(120)
        )
        
        legends = [
            ('Answered', [0.3, 0.8, 0.3, 1]),
            ('Not Answered', [1, 0.3, 0.3, 1]),
            ('Marked for Review', [0.9, 0.6, 0.2, 1]),
            ('Not Visited', [0.7, 0.7, 0.7, 1])
        ]
        
        for text, color in legends:
            legend_item = MDBoxLayout(
                orientation='horizontal',
                spacing=dp(5),
                size_hint_y=None,
                height=dp(25)
            )
            
            color_box = MDBoxLayout(
                size_hint=(None, None),
                size=(dp(20), dp(20)),
                md_bg_color=color
            )
            
            legend_text = MDLabel(
                text=text,
                font_style='Caption'
            )
            
            legend_item.add_widget(color_box)
            legend_item.add_widget(legend_text)
            self.legend_box.add_widget(legend_item)
        
        # Submit button
        self.submit_button = MDRaisedButton(
            text='SUBMIT TEST',
            size_hint_y=None,
            height=dp(50),
            on_release=self.confirm_submit
        )
        
        # Add to palette
        self.palette_box.add_widget(self.palette_header)
        self.palette_box.add_widget(self.palette_scroll)
        self.palette_box.add_widget(self.legend_box)
        self.palette_box.add_widget(self.submit_button)
        
        # Add to content
        self.content_box.add_widget(self.question_scroll)
        self.content_box.add_widget(self.palette_box)
        
        # Add to layout
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.timer_label)
        self.layout.add_widget(self.content_box)
        
        self.add_widget(self.layout)
    
    def create_option_card(self, label, index):
        """Create option card"""
        card = MDCard(
            orientation='horizontal',
            padding=dp(10),
            size_hint_y=None,
            height=dp(50),
            elevation=1,
            radius=[dp(8)],
            on_release=lambda x, i=index: self.select_option(i)
        )
        
        option_label = MDLabel(
            text=f'{label}.',
            font_style='Subtitle1',
            size_hint_x=0.08
        )
        
        option_text = MDLabel(
            text=f'Option {label}',
            font_style='Body1',
            size_hint_x=0.82
        )
        
        checkbox = MDCheckbox(
            size_hint_x=0.1,
            group='test_options',
            disabled=True
        )
        checkbox.index = index
        
        card.add_widget(option_label)
        card.add_widget(option_text)
        card.add_widget(checkbox)
        
        card.option_label = option_label
        card.option_text = option_text
        card.checkbox = checkbox
        
        return card
    
    def create_palette_button(self, number):
        """Create palette button"""
        btn = MDTextButton(
            text=str(number),
            font_style='Caption',
            size_hint=(None, None),
            size=(dp(35), dp(35)),
            on_release=lambda x, n=number: self.jump_to_question(n)
        )
        btn.number = number
        return btn
    
    def start_test(self, questions, duration_minutes):
        """Start test with questions and duration"""
        self.questions = questions
        self.answers = [None] * len(questions)
        self.marked_for_review = [False] * len(questions)
        self.current_question_index = 0
        self.time_remaining = duration_minutes * 60
        
        # Start timer
        self.start_timer()
        
        # Display first question
        self.display_question()
        
        # Update palette
        self.update_palette()
    
    def start_timer(self):
        """Start countdown timer"""
        if self.timer_event:
            self.timer_event.cancel()
        
        self.timer_event = Clock.schedule_interval(self.update_timer, 1)
    
    def update_timer(self, dt):
        """Update timer display"""
        self.time_remaining -= 1
        
        minutes = self.time_remaining // 60
        seconds = self.time_remaining % 60
        self.timer_label.text = f'⏱ {minutes:02d}:{seconds:02d}'
        
        # Check if time is up
        if self.time_remaining <= 0:
            self.timer_event.cancel()
            self.auto_submit_test()
    
    def display_question(self):
        """Display current question"""
        question = self.questions[self.current_question_index]
        
        self.question_number.text = f'Question {self.current_question_index + 1} of {len(self.questions)}'
        self.question_text.text = question.get('questionText', '')
        
        options = [
            question.get('optionA', ''),
            question.get('optionB', ''),
            question.get('optionC', ''),
            question.get('optionD', '')
        ]
        
        for i, option_card in enumerate(self.option_buttons):
            option_card.option_text.text = options[i]
            option_card.checkbox.active = (self.answers[self.current_question_index] == i)
            option_card.md_bg_color = [0.9, 0.95, 1, 1] if self.answers[self.current_question_index] == i else [1, 1, 1, 1]
        
        self.prev_button.disabled = self.current_question_index == 0
        self.update_palette()
    
    def select_option(self, index):
        """Select an option"""
        self.answers[self.current_question_index] = index
        
        for i, option_card in enumerate(self.option_buttons):
            option_card.checkbox.active = (i == index)
            option_card.md_bg_color = [0.9, 0.95, 1, 1] if i == index else [1, 1, 1, 1]
        
        self.update_palette()
    
    def clear_answer(self, instance):
        """Clear current answer"""
        self.answers[self.current_question_index] = None
        
        for option_card in self.option_buttons:
            option_card.checkbox.active = False
            option_card.md_bg_color = [1, 1, 1, 1]
        
        self.update_palette()
    
    def mark_for_review(self, instance):
        """Mark current question for review"""
        self.marked_for_review[self.current_question_index] = not self.marked_for_review[self.current_question_index]
        self.update_palette()
        Snackbar(text='Marked for review').open()
    
    def jump_to_question(self, number):
        """Jump to specific question"""
        self.current_question_index = number - 1
        self.display_question()
    
    def previous_question(self, instance):
        """Go to previous question"""
        if self.current_question_index > 0:
            self.current_question_index -= 1
            self.display_question()
    
    def next_question(self, instance):
        """Go to next question"""
        if self.current_question_index < len(self.questions) - 1:
            self.current_question_index += 1
            self.display_question()
    
    def update_palette(self):
        """Update question palette colors"""
        for i, btn in enumerate(self.palette_buttons):
            if i < len(self.questions):
                if self.answers[i] is not None:
                    btn.md_bg_color = [0.3, 0.8, 0.3, 1]
                elif self.marked_for_review[i]:
                    btn.md_bg_color = [0.9, 0.6, 0.2, 1]
                else:
                    btn.md_bg_color = [1, 0.3, 0.3, 1]
                
                # Highlight current question
                if i == self.current_question_index:
                    btn.text_color = [1, 1, 1, 1]
                else:
                    btn.text_color = [0, 0, 0, 1]
            else:
                btn.md_bg_color = [0.7, 0.7, 0.7, 1]
    
    def confirm_submit(self, instance):
        """Confirm test submission"""
        dialog = MDDialog(
            title='Submit Test',
            text='Are you sure you want to submit the test?',
            buttons=[
                MDFlatButton(
                    text='CANCEL',
                    on_release=lambda x: dialog.dismiss()
                ),
                MDFlatButton(
                    text='SUBMIT',
                    on_release=lambda x: self.submit_test()
                ),
            ],
        )
        dialog.open()
    
    def submit_test(self):
        """Submit test"""
        if self.timer_event:
            self.timer_event.cancel()
        
        # Calculate results
        app = MDApp.get_running_app()
        app.test_result = {
            'answers': self.answers,
            'questions': self.questions,
            'marked_for_review': self.marked_for_review
        }
        
        # Navigate to result screen
        self.manager.current = 'test_result'
    
    def auto_submit_test(self):
        """Auto submit when time expires"""
        Snackbar(text='Time is up! Submitting test...').open()
        Clock.schedule_once(lambda dt: self.submit_test(), 2)
    
    def confirm_exit(self):
        """Confirm exit from test"""
        dialog = MDDialog(
            title='Exit Test',
            text='Your progress will be lost. Are you sure?',
            buttons=[
                MDFlatButton(
                    text='CANCEL',
                    on_release=lambda x: dialog.dismiss()
                ),
                MDFlatButton(
                    text='EXIT',
                    on_release=lambda x: self.exit_test()
                ),
            ],
        )
        dialog.open()
    
    def exit_test(self):
        """Exit test"""
        if self.timer_event:
            self.timer_event.cancel()
        self.manager.current = 'tests_list'