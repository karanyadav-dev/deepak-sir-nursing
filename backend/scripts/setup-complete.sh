#!/bin/bash

echo "🎯 Deepak Sir - Complete Setup"
echo "================================"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Step 1: Check Prerequisites
echo -e "${YELLOW}[1/7] Checking prerequisites...${NC}"
command -v java >/dev/null 2>&1 || { echo -e "${RED}Java required${NC}"; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo -e "${RED}Maven required${NC}"; exit 1; }
command -v python3 >/dev/null 2>&1 || { echo -e "${RED}Python required${NC}"; exit 1; }
command -v psql >/dev/null 2>&1 || { echo -e "${RED}PostgreSQL required${NC}"; exit 1; }
echo -e "${GREEN}✅ Prerequisites OK${NC}"

# Step 2: Setup Database
echo -e "${YELLOW}[2/7] Setting up database...${NC}"
psql -U postgres -c "CREATE DATABASE deepaksir;" 2>/dev/null || echo "Database exists"
psql -U postgres -d deepaksir -f database/schema.sql 2>/dev/null
psql -U postgres -d deepaksir -f database/migration_ai.sql 2>/dev/null
echo -e "${GREEN}✅ Database ready${NC}"

# Step 3: Configure Environment
echo -e "${YELLOW}[3/7] Configuring environment...${NC}"
if [ ! -f .env ]; then
    cp .env.example .env
    echo "Created .env - Please update with your API keys"
else
    echo ".env already exists"
fi
echo -e "${GREEN}✅ Environment configured${NC}"

# Step 4: Build Backend
echo -e "${YELLOW}[4/7] Building backend...${NC}"
cd backend
mvn clean install -DskipTests
cd ..
echo -e "${GREEN}✅ Backend built${NC}"

# Step 5: Install Frontend Dependencies
echo -e "${YELLOW}[5/7] Installing Python dependencies...${NC}"
cd frontend
pip3 install -r requirements.txt
cd ..
echo -e "${GREEN}✅ Dependencies installed${NC}"

# Step 6: Create Upload Directory
echo -e "${YELLOW}[6/7] Creating upload directories...${NC}"
mkdir -p uploads/ai-chat-images
mkdir -p uploads/profile-images
mkdir -p logs
echo -e "${GREEN}✅ Directories created${NC}"

# Step 7: Final Instructions
echo -e "${YELLOW}[7/7] Setup complete!${NC}"
echo ""
echo -e "${GREEN}✅ Deepak Sir is ready to deploy!${NC}"
echo ""
echo "To start the application:"
echo "1. Update .env with your OpenAI API key"
echo "2. Start backend: cd backend && mvn spring-boot:run"
echo "3. Start frontend: cd frontend && python main.py"
echo ""
echo "For Android APK:"
echo "cd frontend && buildozer android debug"