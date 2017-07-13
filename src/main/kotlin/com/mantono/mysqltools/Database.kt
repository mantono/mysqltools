package com.mantono.mysqltools

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.experimental.future.future
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.concurrent.Future

object Database
{
	private val dataSource: HikariDataSource
	private const val DRIVER = "com.mysql.jdbc.Driver"

	init
	{
		Class.forName(DRIVER).newInstance()
		val config = HikariConfig("database.properties")
		if(config.jdbcUrl.isNullOrEmpty())
			config.jdbcUrl = System.getenv("JDBC_URL") ?: throw IllegalArgumentException("No valid JDBC URL found")
		config.validate()
		dataSource = HikariDataSource(config)
	}

	fun connection(): Connection = dataSource.connection

	fun prepare(query: String): Future<PreparedStatement>
	{
		return future<PreparedStatement> { connection().prepareStatement(query) }
	}
}