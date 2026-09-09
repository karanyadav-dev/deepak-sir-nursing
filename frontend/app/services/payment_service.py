from app.services.api_service import ApiService

class PaymentService:
    """Service for payment operations"""
    
    def __init__(self, api_service: ApiService):
        self.api_service = api_service
    
    def create_order(self, product_id, amount, method='UPI'):
        """Create payment order"""
        return self.api_service.post('/payments/create-order', {
            'productId': product_id,
            'amount': amount,
            'method': method
        })
    
    def verify_payment(self, order_id, payment_id, signature):
        """Verify payment"""
        return self.api_service.post('/payments/verify', {
            'orderId': order_id,
            'paymentId': payment_id,
            'signature': signature
        })
    
    def get_payment_history(self):
        """Get payment history"""
        return self.api_service.get('/payments/history')
    
    def validate_coupon(self, code, amount):
        """Validate coupon code"""
        return self.api_service.post('/coupons/validate', {
            'code': code,
            'amount': amount
        })