from pyspark.sql import SparkSession
from SynapseConnection import SynapseConnection

# Initialize connection details
tenant_id = "<your-tenant-id>"
client_id = "<your-client-id>"
client_secret = "<your-client-secret>"
synapse_server = "<your-synapse-server>"
database = "<your-database-name>"

# Create a SynapseConnection instance
synapse_conn = SynapseConnection(tenant_id, client_id, client_secret, synapse_server, database)

# Initialize Spark session
spark = SparkSession.builder.appName("SynapseConnect").getOrCreate()

# Fetch table structure
table_name = "your_table"
df = spark.read \
    .format("jdbc") \
    .option("url", synapse_conn.get_jdbc_url()) \
    .option("dbtable", f"(SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '{table_name}') as schema_table") \
    .options(**synapse_conn.get_connection_properties()) \
    .load()

# Show table structure
df.show()
