from kivymd.app import MDApp

class ThemeManager:
    \"\"\"Theme manager for the app\"\"\"
    
    @staticmethod
    def apply_light_theme(app):
        app.theme_cls.theme_style = 'Light'
        app.theme_cls.primary_palette = 'Blue'
        app.theme_cls.accent_palette = 'Teal'
    
    @staticmethod
    def apply_dark_theme(app):
        app.theme_cls.theme_style = 'Dark'
        app.theme_cls.primary_palette = 'Blue'
        app.theme_cls.accent_palette = 'Teal'
