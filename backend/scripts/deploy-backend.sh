#!/bin/bash

echo "🚀 Deploying Deepak Sir Backend..."

# Navigate to backend
cd backend

# Build the application
echo "Building application..."
mvn clean package -DskipTests

# Check if build successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful"
    
    # Run database migrations
    echo "Running database migrations..."
    psql -U postgres -d deepaksir -f ../database/migration_ai.sql
    
    # Start the application
    echo "Starting application..."
    java -jar target/deepak-sir-backend-1.0.0.jar
else
    echo "❌ Build failed"
    exit 1
fi