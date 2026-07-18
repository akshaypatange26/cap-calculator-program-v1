# Script to prune Oracle DB logs and calculation history older than 24 hours

Write-Host "Connecting to Oracle Database and deleting data older than 24 hours..." -ForegroundColor Yellow

$sql = @"
SET FEEDBACK ON
-- Delete calculation history older than 24 hours (1 day)
DELETE FROM CALCULATION_HISTORY WHERE CREATED_AT < SYSDATE - 1;

-- Delete calculation error logs older than 24 hours (1 day)
DELETE FROM CALCULATION_ERROR_LOG WHERE CREATED_AT < SYSDATE - 1;

COMMIT;
exit;
"@

$sql | sqlplus -S CALCULATOR_USER/Password123@localhost:1521/FREEPDB1

Write-Host "Database pruning completed successfully!" -ForegroundColor Green
