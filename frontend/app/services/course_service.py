from app.services.api_service import ApiService


class CourseService:
    """Service for course operations"""

    def __init__(self, api_service: ApiService):
        self.api_service = api_service

    def get_courses(self):
        """Get all published courses"""
        return self.api_service.get('/courses')

    def get_course(self, course_id):
        """Get course by ID"""
        return self.api_service.get(f'/courses/{course_id}')

    def get_course_chapters(self, course_id):
        """Get course chapters"""
        return self.api_service.get(f'/courses/{course_id}/chapters')

    def enroll_course(self, course_id):
        """Enroll in course"""
        return self.api_service.post(f'/courses/{course_id}/enroll', {})