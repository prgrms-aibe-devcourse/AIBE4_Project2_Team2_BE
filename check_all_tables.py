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

try:
    print("Connecting to database...")
    connection = mysql.connector.connect(**config)
    cursor = connection.cursor()

    # Get all tables
    cursor.execute("SHOW TABLES")
    tables = [table[0] for table in cursor.fetchall()]

    print(f"\nFound {len(tables)} tables\n")
    print("Tables with 'id' column as PK:")
    print("="*60)

    tables_with_id = []

    for table in tables:
        cursor.execute(f"DESCRIBE {table}")
        columns = cursor.fetchall()

        # Check if has 'id' column with PRI key
        for col in columns:
            if col[0] == 'id' and 'PRI' in col[3]:
                tables_with_id.append(table)
                print(f"  - {table}")
                break

    if not tables_with_id:
        print("  ✓ No tables found with 'id' as PK")

    print(f"\n{'='*60}")
    print(f"Total: {len(tables_with_id)} tables need migration")

    cursor.close()
    connection.close()

except mysql.connector.Error as err:
    print(f"Database error: {err}")
    sys.exit(1)
except Exception as e:
    print(f"Unexpected error: {e}")
    sys.exit(1)
