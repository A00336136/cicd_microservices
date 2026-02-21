#!/bin/bash

# Lifestyle Service API Test Script
# Usage: ./test-entries-api.sh

BASE_URL="http://localhost:8083"
API_PATH="/lifestyle/entries"
FULL_URL="${BASE_URL}${API_PATH}"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counter
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}Lifestyle Service API Test Suite${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# Function to check if service is running
check_service() {
    echo -e "${YELLOW}Checking if service is running on port 8083...${NC}"
    if curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Service is running${NC}"
        echo ""
    else
        echo -e "${RED}✗ Service is not running. Please start it with: ./mvnw spring-boot:run${NC}"
        exit 1
    fi
}

# Function to run a test
run_test() {
    local test_name=$1
    local method=$2
    local url=$3
    local data=$4
    local expected_status=$5
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    echo -e "${YELLOW}Test ${TOTAL_TESTS}: ${test_name}${NC}"
    echo -e "Request: ${method} ${url}"
    if [ -n "$data" ]; then
        echo -e "Payload: ${data}"
    fi
    
    if [ "$method" == "POST" ] || [ "$method" == "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$url" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$url")
    fi
    
    # Split response body and status code
    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    echo -e "Response Status: ${status_code}"
    echo -e "Response Body: ${body}" | head -n 5
    
    if [ "$status_code" == "$expected_status" ]; then
        echo -e "${GREEN}✓ PASSED${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ FAILED (Expected: ${expected_status}, Got: ${status_code})${NC}"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
    echo ""
}

# Check if service is running
check_service

# ==========================================
# Scenario 1: Success Cases
# ==========================================
echo -e "${BLUE}=== Scenario 1: Success Cases ===${NC}"
echo ""

# Test 1: Create entry with STEPS
run_test "Create entry - STEPS" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "value": 5000,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "201"

# Test 2: Create entry with WATER
run_test "Create entry - WATER" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "WATER",
        "value": 200,
        "timestamp": "2026-02-06T10:00:00"
    }' \
    "201"

# Test 3: Create entry with lowercase type (case-insensitive)
run_test "Create entry - lowercase type (steps)" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "steps",
        "value": 3000,
        "timestamp": "2026-02-06T11:00:00"
    }' \
    "201"

# Test 4: Create entry with JOGGING
run_test "Create entry - JOGGING" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 2,
        "type": "JOGGING",
        "value": 30,
        "timestamp": "2026-02-06T12:00:00"
    }' \
    "201"

# ==========================================
# Scenario 2: Validation Failures
# ==========================================
echo -e "${BLUE}=== Scenario 2: Validation Failures ===${NC}"
echo ""

# Test 5: Negative value
run_test "Negative value (-100 steps)" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "value": -100,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 6: Future timestamp
run_test "Future timestamp" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "value": 5000,
        "timestamp": "2027-02-06T09:00:00"
    }' \
    "400"

# Test 7: Zero value
run_test "Zero value" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "value": 0,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 8: Missing userId
run_test "Missing userId" \
    "POST" \
    "${FULL_URL}" \
    '{
        "type": "STEPS",
        "value": 5000,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 9: Missing type
run_test "Missing type" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "value": 5000,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 10: Invalid type
run_test "Invalid type (INVALID_TYPE)" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "INVALID_TYPE",
        "value": 5000,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 11: Missing value
run_test "Missing value" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 12: Missing timestamp
run_test "Missing timestamp" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "value": 5000
    }' \
    "400"

# Test 13: Value exceeds limit (steps > 100000)
run_test "Value exceeds limit (150000 steps)" \
    "POST" \
    "${FULL_URL}" \
    '{
        "userId": 1,
        "type": "STEPS",
        "value": 150000,
        "timestamp": "2026-02-06T09:00:00"
    }' \
    "400"

# Test 14: Invalid JSON format
run_test "Invalid JSON format" \
    "POST" \
    "${FULL_URL}" \
    '{userId: 1, type: "STEPS"' \
    "400"

# ==========================================
# Scenario 3: Query Operations
# ==========================================
echo -e "${BLUE}=== Scenario 3: Query Operations ===${NC}"
echo ""

# Test 15: Get entries by userId
run_test "Get entries by userId" \
    "GET" \
    "${FULL_URL}?userId=1" \
    "" \
    "200"

# Test 16: Get single entry by ID (assuming ID 1 exists)
run_test "Get single entry by ID" \
    "GET" \
    "${FULL_URL}/1" \
    "" \
    "200"

# Test 17: Get non-existent entry
run_test "Get non-existent entry (ID: 999999)" \
    "GET" \
    "${FULL_URL}/999999" \
    "" \
    "400"

# ==========================================
# Test Summary
# ==========================================
echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}Test Summary${NC}"
echo -e "${BLUE}================================${NC}"
echo -e "Total Tests:  ${TOTAL_TESTS}"
echo -e "${GREEN}Passed:       ${PASSED_TESTS}${NC}"
echo -e "${RED}Failed:       ${FAILED_TESTS}${NC}"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}✗ Some tests failed!${NC}"
    exit 1
fi
