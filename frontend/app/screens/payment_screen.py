from kivymd.app import MDApp
from kivymd.uix.screen import MDScreen
from kivymd.uix.boxlayout import MDBoxLayout
from kivymd.uix.card import MDCard
from kivymd.uix.label import MDLabel
from kivymd.uix.button import MDRaisedButton, MDFlatButton, MDIconButton
from kivymd.uix.textfield import MDTextField
from kivymd.uix.scrollview import MDScrollView
from kivymd.uix.toolbar import MDTopAppBar
from kivymd.uix.snackbar import Snackbar
from kivymd.uix.dialog import MDDialog
from kivymd.uix.progressbar import MDProgressBar
from kivy.metrics import dp
from kivy.clock import Clock

class PaymentScreen(MDScreen):
    """Payment screen for premium content"""
    
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.product_data = None
        self.payment_method = 'UPI'
        self.build_ui()
    
    def build_ui(self):
        """Build payment UI"""
        self.layout = MDBoxLayout(
            orientation='vertical',
            spacing=dp(0)
        )
        
        # Top bar
        self.top_bar = MDTopAppBar(
            title='Payment',
            left_action_items=[['arrow-left', lambda x: self.go_back()]],
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
        
        # Pay button
        self.bottom_bar = MDBoxLayout(
            orientation='horizontal',
            padding=dp(10),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(60)
        )
        
        self.pay_btn = MDRaisedButton(
            text='PAY NOW',
            size_hint=(1, None),
            height=dp(45),
            on_release=self.process_payment
        )
        
        self.bottom_bar.add_widget(self.pay_btn)
        
        self.layout.add_widget(self.top_bar)
        self.layout.add_widget(self.scroll)
        self.layout.add_widget(self.bottom_bar)
        
        self.add_widget(self.layout)
    
    def load_product(self, product_data):
        """Load product details for payment"""
        self.product_data = product_data
        self.display_product()
    
    def display_product(self):
        """Display product details"""
        self.content.clear_widgets()
        
        if not self.product_data:
            return
        
        # Product card
        product_card = MDCard(
            orientation='vertical',
            padding=dp(15),
            spacing=dp(10),
            size_hint_y=None,
            height=dp(150),
            elevation=2,
            radius=[dp(15)]
        )
        
        title = MDLabel(
            text=self.product_data.get('title', 'Product'),
            font_style='H6',
            size_hint_y=None,
            height=dp(30)
        )
        
        description = MDLabel(
            text=self.product_data.get('description', ''),
            font_style='Body2',
            theme_text_color='Secondary',
            size_hint_y=None,
            height=dp(50)
        )
        
        price = MDLabel(
            text=f"₹{self.product_data.get('price', 0)}",
            font_style='H5',
            theme_text_color='Primary',
            size_hint_y=None,
            height=dp(40)
        )
        
        product_card.add_widget(title)
        product_card.add_widget(description)
        product_card.add_widget(price)
        
        self.content.add_widget(product_card)
        
        # Payment methods
        methods_title = MDLabel(
            text='Select Payment Method',
            font_style='Subtitle1',
            size_hint_y=None,
            height=dp(30)
        )
        
        self.content.add_widget(methods_title)
        
        # Payment method options
        methods = [
            ('UPI', '📱 UPI (GPay, PhonePe, Paytm)'),
            ('CARD', '💳 Credit/Debit Card'),
            ('NETBANKING', '🏦 Net Banking'),
        ]
        
        for method_id, method_label in methods:
            method_btn = MDFlatButton(
                text=method_label,
                size_hint_y=None,
                height=dp(50),
                on_release=lambda x, m=method_id: self.select_method(m)
            )
            self.content.add_widget(method_btn)
        
        # Coupon section
        coupon_title = MDLabel(
            text='Have a coupon code?',
            font_style='Subtitle2',
            size_hint_y=None,
            height=dp(25)
        )
        
        self.content.add_widget(coupon_title)
        
        coupon_row = MDBoxLayout(
            orientation='horizontal',
            spacing=dp(10),
            size_hint_y=None,
            height=dp(50)
        )
        
        self.coupon_field = MDTextField(
            hint_text='Enter coupon code',
            mode='rectangle',
            size_hint_x=0.7
        )
        
        apply_btn = MDRaisedButton(
            text='APPLY',
            size_hint=(None, None),
            size=(dp(80), dp(45)),
            on_release=self.apply_coupon
        )
        
        coupon_row.add_widget(self.coupon_field)
        coupon_row.add_widget(apply_btn)
        
        self.content.add_widget(coupon_row)
    
    def select_method(self, method):
        """Select payment method"""
        self.payment_method = method
        Snackbar(text=f'Selected: {method}').open()
    
    def apply_coupon(self, instance):
        """Apply coupon code"""
        coupon_code = self.coupon_field.text.strip()
        
        if not coupon_code:
            Snackbar(text='Please enter coupon code').open()
            return
        
        app = MDApp.get_running_app()
        result = app.api_service.post('/coupons/validate', {
            'code': coupon_code,
            'amount': self.product_data.get('price', 0)
        })
        
        if result.get('success'):
            discount = result.get('data', {}).get('discount', 0)
            Snackbar(text=f'Coupon applied! Discount: ₹{discount}').open()
        else:
            Snackbar(text=result.get('message', 'Invalid coupon')).open()
    
    def process_payment(self, instance):
        """Process payment"""
        if not self.product_data:
            return
        
        self.pay_btn.disabled = True
        self.pay_btn.text = 'PROCESSING...'
        
        app = MDApp.get_running_app()
        
        # Create order
        order_result = app.api_service.post('/payments/create-order', {
            'productId': self.product_data.get('id'),
            'amount': self.product_data.get('price', 0),
            'method': self.payment_method
        })
        
        if order_result.get('success'):
            order_data = order_result.get('data', {})
            order_id = order_data.get('orderId')
            
            # Simulate payment verification
            # In production, integrate with actual payment gateway
            verify_result = app.api_service.post('/payments/verify', {
                'orderId': order_id,
                'paymentId': f"PAY_{order_id}",
                'signature': 'dummy_signature'
            })
            
            if verify_result.get('success'):
                self.show_success_dialog()
            else:
                self.pay_btn.disabled = False
                self.pay_btn.text = 'PAY NOW'
                Snackbar(text='Payment verification failed').open()
        else:
            self.pay_btn.disabled = False
            self.pay_btn.text = 'PAY NOW'
            Snackbar(text='Failed to create order').open()
    
    def show_success_dialog(self):
        """Show payment success dialog"""
        dialog = MDDialog(
            title='✅ Payment Successful',
            text='Your payment has been processed successfully!\n\nYou now have access to this content.',
            buttons=[
                MDRaisedButton(
                    text='CONTINUE',
                    on_release=lambda x: self.finish_payment(dialog)
                )
            ]
        )
        dialog.open()
    
    def finish_payment(self, dialog):
        """Finish payment and navigate back"""
        dialog.dismiss()
        self.go_back()
    
    def go_back(self):
        """Navigate back"""
        self.manager.current = 'courses'