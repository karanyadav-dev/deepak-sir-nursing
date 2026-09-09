#!/bin/bash

echo "🚀 Setting up Deepak Sir - Complete Setup"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Check prerequisites
echo -e "${YELLOW}Step 1: Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java not found. Please install Java 17+${NC}"
    exit 1
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Maven not found. Please install Maven${NC}"
    exit 1
fi

# Check PostgreSQL
if ! command -v psql &> /dev/null; then
    echo -e "${RED}PostgreSQL not found. Please install PostgreSQL${NC}"
    exit 1
fi

# Check Redis
if ! command -v redis-cli &> /dev/null; then
    echo -e "${YELLOW}Redis not found. Installing Redis...${NC}"
    # Install Redis based on OS
fi

# Check Python
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}Python 3 not found. Please install Python 3.8+${NC}"
    exit 1
fi

# Step 2: Setup Database
echo -e "${YELLOW}Step 2: Setting up database...${NC}"
psql -U postgres -c "CREATE DATABASE deepaksir;" 2>/dev/null || echo "Database already exists"
psql -U postgres -d deepaksir -f database/schema.sql

# Step 3: Setup Backend
echo -e "${YELLOW}Step 3: Setting up backend...${NC}"
cd backend
mvn clean install -DskipTests
cd ..

# Step 4: Setup Frontend
echo -e "${YELLOW}Step 4: Setting up frontend...${NC}"
cd frontend
pip3 install -r requirements.txt
cd ..

# Step 5: Setup Admin Panel
echo -e "${YELLOW}Step 5: Setting up admin panel...${NC}"
cd admin
npm install
npm run build
cd ..

# Step 6: Create environment file
echo -e "${YELLOW}Step 6: Creating environment file...${NC}"
if [ ! -f .env ]; then
    cp .env.example .env
    echo "Created .env file. Please update with your configurations."
fi

echo -e "${GREEN}✅ Setup completed successfully!${NC}"
echo ""
echo "To start the application:"
echo "1. Backend: cd backend && mvn spring-boot:run"
echo "2. Frontend: cd frontend && python main.py"
echo "3. Admin: cd admin && npm run dev"