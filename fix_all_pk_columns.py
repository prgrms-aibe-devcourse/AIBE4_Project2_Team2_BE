#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import mysql.connector
import sys
import io

# Set stdout to UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Database connection configuration
config = {
    'host': 'mysql-2075cc2d-majormate2026-b576.l.aivencloud.com',
    'port': 22631,
    'user': 'avnadmin',
    'password': 'AVNS_as_ExPF4QyMzRuaBjVi',
    'database': 'defaultdb',
    'ssl_disabled': False
}

# Tables that need PK column renaming
tables_to_fix = [
    ('major_role_request_status_history', 'id', 'history_id'),
    # Add more tables here if needed
]

try:
    print("Connecting to database...")
    connection = mysql.connector.connect(**config)
    cursor = connection.cursor()

    for table_name, old_col, new_col in tables_to_fix:
        print(f"\n{'='*60}")
        print(f"Processing table: {table_name}")
        print(f"{'='*60}")

        # Check if table exists
        cursor.execute(f"SHOW TABLES LIKE '{table_name}'")
        if not cursor.fetchone():
            print(f"✗ Table '{table_name}' does not exist. Skipping...")
            continue

        # Check current columns
        cursor.execute(f"DESCRIBE {table_name}")
        columns = cursor.fetchall()

        col_names = [col[0] for col in columns]

        if new_col in col_names:
            print(f"✓ Column '{new_col}' already exists. Skipping...")
            continue

        if old_col not in col_names:
            print(f"✗ Column '{old_col}' not found. Skipping...")
            continue

        print(f"→ Found column '{old_col}'. Renaming to '{new_col}'...")

        # Get column definition
        old_col_def = next((col for col in columns if col[0] == old_col), None)
        if not old_col_def:
            print(f"✗ Could not get column definition. Skipping...")
            continue

        # Rename column
        cursor.execute(f"""
            ALTER TABLE {table_name}
            CHANGE COLUMN {old_col} {new_col} BIGINT NOT NULL AUTO_INCREMENT
        """)
        connection.commit()
        print(f"✓ Column renamed: {old_col} → {new_col}")

    print(f"\n{'='*60}")
    print("Verifying all changes...")
    print(f"{'='*60}")

    for table_name, old_col, new_col in tables_to_fix:
        cursor.execute(f"SHOW TABLES LIKE '{table_name}'")
        if cursor.fetchone():
            cursor.execute(f"DESCRIBE {table_name}")
            columns = cursor.fetchall()
            col_names = [col[0] for col in columns]
            if new_col in col_names:
                print(f"✓ {table_name}.{new_col} exists")
            else:
                print(f"✗ {table_name}.{new_col} NOT found!")

    cursor.close()
    connection.close()
    print("\n✓ All migrations completed successfully!")

except mysql.connector.Error as err:
    print(f"\n✗ Database error: {err}")
    sys.exit(1)
except Exception as e:
    print(f"\n✗ Unexpected error: {e}")
    import traceback
    traceback.print_exc()
    sys.exit(1)
