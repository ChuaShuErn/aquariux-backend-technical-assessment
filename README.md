# Technical Assessment Instructions

Welcome to the Aquariux Trading System technical assessment.

**⏰ Time Limit: You have 4 hours from your initial commit to complete this assessment.**

## Prerequisites

Before starting the assessment, ensure you have:
1. **Public GitHub repository** ready for code submission
2. **IDE setup** with Java 21 and Maven support

## Getting Started

### Step 1: Initial Commit
Make your initial commit to establish a baseline and **start your 4-hour timer**:
```bash
git add .
git commit -m "Initial commit - baseline code"
```

**⚠️ Important: Your 4-hour assessment period begins with this commit!**

### Step 2: Understand Business Requirements
Review the [1_BUSINESS_REQUIREMENTS.md](1_BUSINESS_REQUIREMENTS.md) to understand:
- Business context and objectives
- Core functional requirements
- Success metrics and compliance needs

### Step 3: Understand Entity Relations
Study the [2_DATABASE_SCHEMA.md](2_DATABASE_SCHEMA.md) to understand:
- Database entity relationships
- Table structures and constraints
- Data flow between entities

### Step 4: Understand the Application
Review the [3_APPLICATION_OVERVIEW.md](3_APPLICATION_OVERVIEW.md) file to understand:
- Application architecture and technology stack
- Project structure and components
- Available API endpoints
- Technology stack details

### Step 5: Begin Assessment
Read the [4_ASSESSMENT.md](4_ASSESSMENT.md) file for:
- Detailed requirements
- Implementation tasks
- Evaluation criteria

## Assessment Workflow

### Setup
1. **Download** the provided source code from our assessment package
2. **Create** a new public GitHub repository for your submission
3. **Initialize** the repository with the downloaded code
4. **Make initial commit** to start your 4-hour timer

### Submission
1. **Implement** the trade execution functionality
2. **Make final commit** with your complete solution within the 4-hour time limit
3. **Push** your changes to your GitHub repository
4. **Share** your repository link for review

**Note**: Your repository should contain exactly 2 commits - the initial baseline commit and your final solution commit.

### Guidelines
- Consider this a production-ready implementation
- Document any assumptions made
- Ensure your solution runs without errors
- Be prepared to explain your design decisions

## Quick Start

### Option 1: Maven Wrapper (Recommended)
```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

### Option 2: Maven (if installed)
```bash
mvn clean spring-boot:run
```

### Option 3: IDE
- **IntelliJ IDEA**: Right-click `TradeApplication.java` → Run
- **Eclipse**: Right-click `TradeApplication.java` → Run As → Java Application
- **VS Code**: Open `TradeApplication.java` → Click Run button

### Option 4: JAR File
```bash
# Build the JAR
./mvnw clean package

# Run the JAR
java -jar target/trade-2025.11.18.jar
```

### Option 5: Docker (if available)
```bash
# Build image
docker build -t aquariux-trade .

# Run container
docker run -p 8080:8080 aquariux-trade
```

### Verify Application
Once started, access:
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **H2 Console**: http://localhost:8080/h2-console

Good luck with your assessment!# backend-technical-assessment
# backend-technical-assessment
