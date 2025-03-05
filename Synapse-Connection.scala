import scalaj.http._

class SynapseConnection(tenantId: String, clientId: String, clientSecret: String, synapseServer: String, database: String) {

  private val tokenUrl = s"https://login.microsoftonline.com/$tenantId/oauth2/token"

  // Fetch access token
  private def getAccessToken(): String = {
    val response = Http(tokenUrl)
      .postForm(Seq(
        "grant_type" -> "client_credentials",
        "client_id" -> clientId,
        "client_secret" -> clientSecret,
        "resource" -> "https://database.windows.net/"
      ))
      .asString

    val json = ujson.read(response.body)
    json("access_token").str
  }

  val accessToken: String = getAccessToken()

  def getJdbcUrl(): String = {
    s"jdbc:sqlserver://$synapseServer.sql.azuresynapse.net:1433;" +
    s"database=$database;encrypt=true;trustServerCertificate=false;" +
    s"hostNameInCertificate=*.sql.azuresynapse.net;loginTimeout=30"
  }

  def getConnectionProperties(): Map[String, String] = {
    Map(
      "accessToken" -> accessToken,
      "driver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver"
    )
  }
}
