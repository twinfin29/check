import org.apache.spark.sql.SparkSession

object FetchTableStructure {
  def main(args: Array[String]): Unit = {

    // Define connection parameters
    val tenantId = "<your-tenant-id>"
    val clientId = "<your-client-id>"
    val clientSecret = "<your-client-secret>"
    val synapseServer = "<your-synapse-server>"
    val database = "<your-database-name>"
    val tableName = "your_table"

    // Initialize SynapseConnection
    val synapseConn = new SynapseConnection(tenantId, clientId, clientSecret, synapseServer, database)

    // Create Spark session
    val spark = SparkSession.builder()
      .appName("SynapseConnect")
      .getOrCreate()

    // Query to fetch table schema
    val schemaQuery = s"(SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '$tableName') as schema_table"

    // Read schema from Synapse
    val df = spark.read
      .format("jdbc")
      .option("url", synapseConn.getJdbcUrl())
      .option("dbtable", schemaQuery)
      .options(synapseConn.getConnectionProperties())
      .load()

    // Show table structure
    df.show()
  }
}
