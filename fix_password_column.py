import mysql.connector
import yaml

# Read database configuration from application-dev.yml
with open('src/main/resources/application-dev.yml', 'r', encoding='utf-8') as f:
    config = yaml.safe_load(f)

# Extract database connection info
db_url = config['spring']['datasource']['url']
db_user = config['spring']['datasource']['username']
db_password = config['spring']['datasource']['password']

# Parse URL to get host, port, database
# Format: jdbc:mysql://host:port/database?params
url_parts = db_url.replace('jdbc:mysql://', '').split('?')[0]
host_port, database = url_parts.rsplit('/', 1)
host, port = host_port.rsplit(':', 1)

print(f"Connecting to {host}:{port}/{database}...")

try:
    # Connect to database
    connection = mysql.connector.connect(
        host=host,
        port=int(port),
        user=db_user,
        password=db_password,
        database=database,
        ssl_disabled=False
    )

    cursor = connection.cursor()

    # Alter password column to allow NULL
    print("Altering password column to allow NULL...")
    cursor.execute("ALTER TABLE member MODIFY COLUMN password VARCHAR(255) NULL")

    connection.commit()
    print("✅ Successfully altered password column to allow NULL!")

    # Verify the change
    cursor.execute("""
        SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_TYPE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = %s AND TABLE_NAME = 'member' AND COLUMN_NAME = 'password'
    """, (database,))

    result = cursor.fetchone()
    if result:
        print(f"\nVerification:")
        print(f"  Column: {result[0]}")
        print(f"  Type: {result[2]}")
        print(f"  Nullable: {result[1]}")

    cursor.close()
    connection.close()

except mysql.connector.Error as err:
    print(f"❌ Error: {err}")
except Exception as e:
    print(f"❌ Unexpected error: {e}")
