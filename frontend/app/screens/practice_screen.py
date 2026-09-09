from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDIconButton, MDFlatButton
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.dialog import MDDialog
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.progressbar import MDProgressBar
from kivy.metrics import dp
from kivy.clock import Clock
from kivy.uix.behaviors import ButtonBehavior
from kivy.properties import StringProperty, NumericProperty, BooleanProperty, ListProperty
import random

class QuestionCard(MDCard):
    """Card for displaying a question"""
    
    question_text = StringProperty("")
    option_a = StringProperty("")
    option_b = StringProperty("")
    option_c = StringProperty("")
    option_d = StringProperty("")
    selected_option = StringProperty("")
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        self.padding = dp(15)
        self.spacing = dp(10)
        self.size_hint_y = None
        self.height = dp(400)
        self.elevation = 2
        self.radius = [dp(15)]
        
        self.build_ui()
    
    def build_ui(self):
        """Build question card UI"""
        # Question number and bookmark
        header = MDBoxLayout(
            orientation='horizontal',
            size_hint_y=None,
            height=dp(40)
        )
        
        self.question_number = MDLabel(
            text="Question 1",
            font_style='Subtitle1',
            size_hint_x=0.8
        )
        
        self.bookmark_btn = MDIconButton(
            icon='bookmark-outline',
            size_hint_x=0.2,
            on_release=self.toggle_bookmark
        )
        
        header.add_widget(self.question_number)
        header.add_widget(self.bookmark_btn)
        
        # Question text
        self.question_label = MDLabel(
            text=self.question_text,
            font_style='Body1',
            size_hint_y=None,
            height=dp(80)
        )
        
        # Options
        self.option_a_btn = self.create_option_button('A', self.option_a)
        self.option_b_btn = self.create_option_button('B', self.option_b)
        self.option_c_btn = self.create_option_button('C', self.option_c)
        self.option_d_btn = self.create_option_button('D', self.option_d)
        
        # Add all widgets
        self.add_widget(header)
        self.add_widget(self.question_label)
        self.add_widget(self.option_a_btn)
        self.add_widget(self.option_b_btn)
        self.add_widget(self.option_c_btn)
        self.add_widget(self.option_d_btn)
    
    def create_option_button(self, key, text):
        """Create option button"""
        btn = MDFlatButton(
            text=f"{key}. {text}",
            size_hint_y=None,
            height=dp(50),
            on_release=lambda x: self.select_option(key)
        )
        return btn
    
    def select_option(self, option):
        """Handle option selection"""
        self.selected_option = option
        # Update button colors
        for key, btn in [('A', self.option_a_btn), ('B', self.option_b_btn),
                         ('C', self.option_c_btn), ('D', self.option_d_btn)]:
            if key == option:
                btn.md_bg_color = [0.2, 0.6, 1, 1]
                btn.text_color = [1, 1, 1, 1]
            else:
                btn.md_bg_color = [1, 1, 1, 1]
                btn.text_color = [0, 0, 0, 1]
    
    def toggle_bookmark(self, instance):
        """Toggle bookmark icon"""
        if self.bookmark_btn.icon == 'bookmark-outline':
            self.bookmark_btn.icon = 'bookmark'
        else:
            self.bookmark_btn.icon = 'bookmark-outline'


class PracticeScreen(MDScreen):
    """Practice screen for MCQ practice"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.questions = []
        self.current_question_index = 0
        self.answers = {}
        self.bookmarked_questions = set()
        self.build_ui()
    
    def build_ui(self):
        """Build practice screen UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Practice',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
            right_action_items=[['flag-outline', lambda x: self.show_palette()]],
            elevation=2
        )
        
        # Progress bar
        self.progress_bar = MDProgressBar(
            size_hint_y=None,
            height=dp(3)
        )
        
        # Scrollable question area
        self.scroll = MDScrollView()
        self.content = MDBoxLayout(
            orientation='vertical',
            padding=[dp(15), dp(10), dp(15), dp(10)],
            spacing=dp(15),
            adaptive_height=True
        )
        
        # Bottom navigation
        self.bottom_nav = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(60)
        )
        
        self.prev_btn = MDRaisedButton(
            text='PREVIOUS',
            size_hint=(1, None),
            height=dp(40),
            on_release=self.previous_question
        )
        
        self.next_btn = MDRaisedButton(
            text='NEXT',
            size_hint=(1, None),
            height=dp(40),
            on_release=self.next_question
        )
        
        self.submit_btn = MDRaisedButton(
            text='SUBMIT',
            size_hint=(1, None),
            height=dp(40),
            on_release=self.submit_practice
        )
        
        self.bottom_nav.add_widget(self.prev_btn)
        self.bottom_nav.add_widget(self.next_btn)
        
        # Add to layout
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.progress_bar)
        self.layout.add_widget(self.scroll)
        self.layout.add_widget(self.bottom_nav)
        
        self.scroll.add_widget(self.content)
        self.add_widget(self.layout)
    
    def load_questions(self, questions):
        """Load questions for practice"""
        self.questions = questions
        self.current_question_index = 0
        self.answers = {}
        self.bookmarked_questions = set()
        self.display_question()
    
    def display_question(self):
        """Display current question"""
        if not self.questions:
            return
        
        self.content.clear_widgets()
        question = self.questions[self.current_question_index]
        
        question_card = QuestionCard(
            question_text=question.get('questionText', ''),
            option_a=question.get('optionA', ''),
            option_b=question.get('optionB', ''),
            option_c=question.get('optionC', ''),
            option_d=question.get('optionD', '')
        )
        
        # Update question number
        question_card.question_number.text = f"Question {self.current_question_index + 1} of {len(self.questions)}"
        
        # Update progress
        progress = (self.current_question_index + 1) / len(self.questions)
        self.progress_bar.value = progress * 100
        
        # Show/hide navigation buttons
        self.prev_btn.disabled = self.current_question_index == 0
        if self.current_question_index == len(self.questions) - 1:
            self.bottom_nav.remove_widget(self.next_btn)
            if self.submit_btn not in self.bottom_nav.children:
                self.bottom_nav.add_widget(self.submit_btn)
        else:
            if self.submit_btn in self.bottom_nav.children:
                self.bottom_nav.remove_widget(self.submit_btn)
            if self.next_btn not in self.bottom_nav.children:
                self.bottom_nav.add_widget(self.next_btn)
        
        self.content.add_widget(question_card)
    
    def next_question(self, instance):
        """Navigate to next question"""
        if self.current_question_index < len(self.questions) - 1:
            self.current_question_index += 1
            self.display_question()
    
    def previous_question(self, instance):
        """Navigate to previous question"""
        if self.current_question_index > 0:
            self.current_question_index -= 1
            self.display_question()
    
    def show_palette(self):
        """Show question palette"""
        dialog = MDDialog(
            title='Question Palette',
            type='custom',
            content_cls=QuestionPalette(
                total_questions=len(self.questions),
                current_index=self.current_question_index,
                answers=self.answers
            ),
            buttons=[
                MDFlatButton(text='CLOSE', on_release=lambda x: dialog.dismiss())
            ]
        )
        dialog.open()
    
    def submit_practice(self, instance):
        """Submit practice session"""
        # Calculate results
        correct = 0
        wrong = 0
        unanswered = 0
        
        for i, question in enumerate(self.questions):
            if i not in self.answers:
                unanswered += 1
            elif self.answers[i] == question.get('correctAnswer'):
                correct += 1
            else:
                wrong += 1
        
        # Show results
        result_text = f"Correct: {correct}\nWrong: {wrong}\nUnanswered: {unanswered}"
        dialog = MDDialog(
            title='Practice Complete',
            text=result_text,
            buttons=[
                MDFlatButton(text='CLOSE', on_release=lambda x: dialog.dismiss()),
                MDRaisedButton(text='VIEW ANSWERS', on_release=lambda x: self.view_answers())
            ]
        )
        dialog.open()
    
    def view_answers(self):
        """View answers with explanations"""
        # Implement answer review
        pass
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'home'


class QuestionPalette(MDBoxLayout):
    """Question palette for navigation"""
    
    def __init__(self, total_questions, current_index, answers, **kwargs):
        super().__init__(**kwargs)
        self.orientation = 'vertical'
        self.padding = dp(10)
        self.spacing = dp(10)
        
        # Create grid of question numbers
        row = MDBoxLayout(
            orientation='horizontal',
            spacing=dp(5),
            size_hint_y=None,
            height=dp(40)
        )
        
        for i in range(total_questions):
            btn = MDFlatButton(
                text=str(i + 1),
                size_hint=(None, None),
                size=(dp(40), dp(40))
            )
            
            # Color based on status
            if i == current_index:
                btn.md_bg_color = [0.2, 0.6, 1, 1]
                btn.text_color = [1, 1, 1, 1]
            elif i in answers:
                btn.md_bg_color = [0.3, 0.8, 0.3, 1]
                btn.text_color = [1, 1, 1, 1]
            
            row.add_widget(btn)
            
            if (i + 1) % 5 == 0:
                self.add_widget(row)
                row = MDBoxLayout(
                    orientation='horizontal',
                    spacing=dp(5),
                    size_hint_y=None,
                    height=dp(40)
                )
        
        if row.children:
            self.add_widget(row)