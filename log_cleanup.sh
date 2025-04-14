#!/bin/bash

# Log directory
LOG_DIR="/Users/kennethosekhuemen/Projects/itex_store/itexstore_api/logs"

# Remove log files older than 7 days
find $LOG_DIR -type f -name "*.log*" -mtime +1 -exec rm -f {} \;