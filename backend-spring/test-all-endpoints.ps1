$ErrorActionPreference = "Continue"
$base = "http://localhost:5000/api"
$pass = 0; $fail = 0; $errors = @()

function Test-Endpoint($name, $method, $url, $body=$null, $expectStatus=200) {
    try {
        $params = @{ Uri = $url; Method = $method; ContentType = "application/json" }
        if ($body) { $params.Body = ($body | ConvertTo-Json -Depth 5) }
        $resp = Invoke-WebRequest @params -ErrorAction Stop
        $code = $resp.StatusCode
        $data = $resp.Content | ConvertFrom-Json
        if ($code -eq $expectStatus) {
            Write-Host "  PASS  $name ($method $url) -> $code" -ForegroundColor Green
            $script:pass++
            return $data
        } else {
            Write-Host "  FAIL  $name -> Expected $expectStatus, Got $code" -ForegroundColor Red
            $script:fail++
            $script:errors += "$name -> Expected $expectStatus, Got $code"
            return $data
        }
    } catch {
        $code = 0
        if ($_.Exception.Response) { $code = [int]$_.Exception.Response.StatusCode }
        $msg = $_.Exception.Message
        # Some tests expect non-200 status
        if ($expectStatus -ne 200 -and $code -eq $expectStatus) {
            Write-Host "  PASS  $name ($method $url) -> $code (expected)" -ForegroundColor Green
            $script:pass++
            return $null
        }
        Write-Host "  FAIL  $name -> $code $msg" -ForegroundColor Red
        $script:fail++
        $script:errors += "$name -> Status $code : $msg"
        return $null
    }
}

Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Digital Black Board - Spring Boot API Test Suite" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# ── 1. HEALTH ────────────────────────────────────────────────────────────────
Write-Host "--- Health ---" -ForegroundColor Yellow
Test-Endpoint "Health Check" "GET" "$base/health"

# ── 2. AUTH ───────────────────────────────────────────────────────────────────
Write-Host "--- Auth ---" -ForegroundColor Yellow
$loginResult = Test-Endpoint "Login (valid)" "POST" "$base/auth/login" @{email="admin@gmail.com"; password="Admin@123"}
Test-Endpoint "Login (wrong password)" "POST" "$base/auth/login" @{email="admin@gmail.com"; password="wrong"} 401
Test-Endpoint "Login (missing email)" "POST" "$base/auth/login" @{password="test"} 400
$regResult = Test-Endpoint "Register (new user)" "POST" "$base/auth/register" @{name="Test User"; email="test_springtest@gmail.com"; password="test123"; role="Student"} 201

# ── 3. USERS ──────────────────────────────────────────────────────────────────
Write-Host "--- Users ---" -ForegroundColor Yellow
$usersResult = Test-Endpoint "Get all users" "GET" "$base/users"
Test-Endpoint "Get user by ID" "GET" "$base/users/1"
Test-Endpoint "Get user not found" "GET" "$base/users/9999" 404
$newUser = Test-Endpoint "Create user" "POST" "$base/users" @{name="API Test User"; email="api_test_user@gmail.com"; password="pass123"; role="Student"} 201
if ($newUser) {
    $uid = $newUser.id
    Test-Endpoint "Update user" "PUT" "$base/users/$uid" @{name="Updated Name"; role="Instructor"}
    Test-Endpoint "Delete user" "DELETE" "$base/users/$uid"
}
Test-Endpoint "Create duplicate email" "POST" "$base/users" @{name="Dup"; email="admin@gmail.com"; password="x"; role="Student"} 409

# ── 4. COURSES ────────────────────────────────────────────────────────────────
Write-Host "--- Courses ---" -ForegroundColor Yellow
Test-Endpoint "Get all courses" "GET" "$base/courses"
Test-Endpoint "Get courses with filter" "GET" "$base/courses?status=published"
$newCourse = Test-Endpoint "Create course" "POST" "$base/courses" @{title="Spring Boot Test Course"; category="Tech"; status="draft"; description="Test"; createdBy=1; createdByName="Admin"; duration="2h"; lessons=5; tags=@("java","spring")} 201
if ($newCourse) {
    $cid = $newCourse.id
    Test-Endpoint "Get course by ID" "GET" "$base/courses/$cid"
    Test-Endpoint "Update course" "PUT" "$base/courses/$cid" @{title="Updated Course Title"; status="published"}
    
    # Enrollment
    Test-Endpoint "Enroll student" "POST" "$base/courses/$cid/enroll" @{userId=4}
    Test-Endpoint "Duplicate enroll" "POST" "$base/courses/$cid/enroll" @{userId=4} 409
    Test-Endpoint "Get enrollment" "GET" "$base/courses/$cid/enrollment"
}

# ── 5. ASSIGNMENTS ────────────────────────────────────────────────────────────
Write-Host "--- Assignments ---" -ForegroundColor Yellow
Test-Endpoint "Get all assignments" "GET" "$base/assignments"
if ($cid) {
    $newAssignment = Test-Endpoint "Create assignment" "POST" "$base/assignments" @{title="Test Assignment"; courseId=$cid; dueDate="2026-12-31"; maxScore=100; description="Test desc"; createdBy=1} 201
    if ($newAssignment) {
        $aid = $newAssignment.id
        Test-Endpoint "Get assignment by ID" "GET" "$base/assignments/$aid"
        Test-Endpoint "Update assignment" "PUT" "$base/assignments/$aid" @{title="Updated Assignment"; dueDate="2027-01-15"}
        Test-Endpoint "Get student assignments" "GET" "$base/assignments/student/4"
    }
}

# ── 6. SUBMISSIONS ────────────────────────────────────────────────────────────
Write-Host "--- Submissions ---" -ForegroundColor Yellow
Test-Endpoint "Get all submissions" "GET" "$base/submissions"
if ($aid) {
    # Submit via multipart
    try {
        $boundary = [System.Guid]::NewGuid().ToString()
        $bodyLines = @(
            "--$boundary",
            'Content-Disposition: form-data; name="assignmentId"', '', "$aid",
            "--$boundary",
            'Content-Disposition: form-data; name="studentId"', '', "4",
            "--$boundary",
            'Content-Disposition: form-data; name="studentName"', '', "Kumar",
            "--$boundary",
            'Content-Disposition: form-data; name="studentInitials"', '', "KU",
            "--$boundary",
            'Content-Disposition: form-data; name="answer"', '', "My test answer",
            "--$boundary--"
        ) -join "`r`n"
        $resp = Invoke-WebRequest -Uri "$base/submissions" -Method POST -ContentType "multipart/form-data; boundary=$boundary" -Body $bodyLines -ErrorAction Stop
        $subData = $resp.Content | ConvertFrom-Json
        Write-Host "  PASS  Submit assignment (multipart) -> $($resp.StatusCode)" -ForegroundColor Green
        $pass++
        $sid = $subData.id
        
        Test-Endpoint "Get submission by ID" "GET" "$base/submissions/$sid"
        Test-Endpoint "Get submissions by assignmentId" "GET" "$base/submissions?assignmentId=$aid"
        Test-Endpoint "Get submissions by studentId" "GET" "$base/submissions?studentId=4"
        Test-Endpoint "Grade submission" "PUT" "$base/submissions/$sid/grade" @{score=85; feedback="Good work!"}
        Test-Endpoint "Get admin submissions" "GET" "$base/submissions/admin/1"
    } catch {
        Write-Host "  FAIL  Submit assignment (multipart) -> $($_.Exception.Message)" -ForegroundColor Red
        $fail++
        $errors += "Submit assignment (multipart) -> $($_.Exception.Message)"
    }
}

# ── 7. ANNOUNCEMENTS ─────────────────────────────────────────────────────────
Write-Host "--- Announcements ---" -ForegroundColor Yellow
Test-Endpoint "Get all announcements" "GET" "$base/announcements"
$newAnn = Test-Endpoint "Create announcement" "POST" "$base/announcements" @{title="Test Announcement"; message="This is a test"; priority="normal"; author="Admin"; authorId=1} 201
if ($newAnn) {
    Test-Endpoint "Delete announcement" "DELETE" "$base/announcements/$($newAnn.id)"
}

# ── 8. CONTENT ────────────────────────────────────────────────────────────────
Write-Host "--- Content ---" -ForegroundColor Yellow
Test-Endpoint "Get all content" "GET" "$base/content"
$newContent = Test-Endpoint "Create content" "POST" "$base/content" @{title="Test Content"; type="document"; description="Test"; content="Body text"; createdBy=1} 201
if ($newContent) {
    $contentId = $newContent.id
    Test-Endpoint "Update content" "PUT" "$base/content/$contentId" @{title="Updated Content"; description="Updated desc"}
    Test-Endpoint "Delete content" "DELETE" "$base/content/$contentId"
}

# ── 9. MESSAGES ───────────────────────────────────────────────────────────────
Write-Host "--- Messages ---" -ForegroundColor Yellow
$newMsg = Test-Endpoint "Send message" "POST" "$base/messages" @{fromId=1; toId=4; subject="Test Subject"; body="Hello from test"} 201
if ($newMsg) {
    $msgId = $newMsg.id
    Test-Endpoint "Get inbox" "GET" "$base/messages/inbox/4"
    Test-Endpoint "Get sent" "GET" "$base/messages/sent/1"
    Test-Endpoint "Mark message read" "PUT" "$base/messages/$msgId/read"
    Test-Endpoint "Delete message (as recipient)" "DELETE" "$base/messages/$msgId`?asSender=false"
}

# ── CLEANUP ───────────────────────────────────────────────────────────────────
Write-Host "--- Cleanup ---" -ForegroundColor Yellow
if ($sid) { Test-Endpoint "Delete submission" "DELETE" "$base/submissions/$sid" }
if ($aid) { Test-Endpoint "Delete assignment" "DELETE" "$base/assignments/$aid" }
if ($cid) { Test-Endpoint "Delete course" "DELETE" "$base/courses/$cid" }
# Delete test user created during register
try { Invoke-RestMethod -Uri "$base/users" -Method GET | Where-Object { $_.email -eq "test_springtest@gmail.com" } | ForEach-Object { Invoke-RestMethod -Uri "$base/users/$($_.id)" -Method DELETE } } catch {}

# ── RESULTS ───────────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  RESULTS: $pass PASSED, $fail FAILED" -ForegroundColor $(if ($fail -eq 0) { "Green" } else { "Red" })
Write-Host "========================================================" -ForegroundColor Cyan

if ($errors.Count -gt 0) {
    Write-Host ""
    Write-Host "  ERRORS:" -ForegroundColor Red
    $errors | ForEach-Object { Write-Host "    - $_" -ForegroundColor Red }
}
Write-Host ""
