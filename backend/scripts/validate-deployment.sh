#!/bin/bash

echo "🔍 Deepak Sir - Deployment Validation"
echo "======================================"

PASS=0
FAIL=0

check_file() {
    if [ -f "$1" ]; then
        echo "✅ $1"
        PASS=$((PASS+1))
    else
        echo "❌ $1 MISSING"
        FAIL=$((FAIL+1))
    fi
}

echo ""
echo "Backend Files:"
check_file "backend/src/main/java/com/deepaksir/DeepakSirApplication.java"
check_file "backend/src/main/java/com/deepaksir/config/AIConfig.java"
check_file "backend/src/main/java/com/deepaksir/config/SecurityConfig.java"
check_file "backend/src/main/java/com/deepaksir/config/RestTemplateConfig.java"
check_file "backend/src/main/java/com/deepaksir/config/WebConfig.java"
check_file "backend/src/main/java/com/deepaksir/config/RedisConfig.java"
check_file "backend/src/main/java/com/deepaksir/dto/ApiResponse.java"
check_file "backend/src/main/java/com/deepaksir/dto/AIChatDTO.java"
check_file "backend/src/main/java/com/deepaksir/dto/AIMessageDTO.java"
check_file "backend/src/main/java/com/deepaksir/entity/User.java"
check_file "backend/src/main/java/com/deepaksir/entity/Question.java"
check_file "backend/src/main/java/com/deepaksir/entity/Subject.java"
check_file "backend/src/main/java/com/deepaksir/entity/Topic.java"
check_file "backend/src/main/java/com/deepaksir/entity/Exam.java"
check_file "backend/src/main/java/com/deepaksir/entity/AIChat.java"
check_file "backend/src/main/java/com/deepaksir/entity/AIMessage.java"
check_file "backend/src/main/java/com/deepaksir/entity/QuestionAttempt.java"
check_file "backend/src/main/java/com/deepaksir/exception/ApiException.java"
check_file "backend/src/main/java/com/deepaksir/exception/AIServiceException.java"
check_file "backend/src/main/java/com/deepaksir/exception/GlobalExceptionHandler.java"
check_file "backend/src/main/java/com/deepaksir/repository/AIChatRepository.java"
check_file "backend/src/main/java/com/deepaksir/repository/AIMessageRepository.java"
check_file "backend/src/main/java/com/deepaksir/repository/QuestionAttemptRepository.java"
check_file "backend/src/main/java/com/deepaksir/repository/SubjectRepository.java"
check_file "backend/src/main/java/com/deepaksir/repository/TopicRepository.java"
check_file "backend/src/main/java/com/deepaksir/service/AIAssistantService.java"
check_file "backend/src/main/java/com/deepaksir/service/StudentProgressService.java"
check_file "backend/src/main/java/com/deepaksir/service/storage/StorageService.java"
check_file "backend/src/main/java/com/deepaksir/service/storage/LocalStorageService.java"
check_file "backend/src/main/java/com/deepaksir/service/ai/AIProviderService.java"
check_file "backend/src/main/java/com/deepaksir/service/ai/OpenAIProviderService.java"
check_file "backend/src/main/java/com/deepaksir/controller/AIAssistantController.java"
check_file "backend/src/main/java/com/deepaksir/controller/FileController.java"

echo ""
echo "Frontend Files:"
check_file "frontend/main.py"
check_file "frontend/app/screens/ai_chat_screen.py"
check_file "frontend/app/screens/ai_chat_history_screen.py"
check_file "frontend/app/services/ai_service.py"
check_file "frontend/app/services/api_service.py"
check_file "frontend/requirements.txt"
check_file "frontend/buildozer.spec"

echo ""
echo "Database Files:"
check_file "database/complete_schema.sql"
check_file "database/migration_ai.sql"

echo ""
echo "Configuration Files:"
check_file ".env.example"
check_file "backend/src/main/resources/application.yml"

echo ""
echo "======================================"
echo "Results: $PASS passed, $FAIL failed"

if [ $FAIL -eq 0 ]; then
    echo "✅ ALL FILES PRESENT - READY TO DEPLOY"
    echo ""
    echo "Next Steps:"
    echo "1. Update .env with your OpenAI API key"
    echo "2. Run database migration: psql -U postgres -d deepaksir -f database/complete_schema.sql"
    echo "3. Start backend: cd backend && mvn spring-boot:run"
    echo "4. Start frontend: cd frontend && python main.py"
else
    echo "❌ MISSING FILES - PLEASE CREATE THEM"
fi