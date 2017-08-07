package com.mantono.mysqltools

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

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
}

inline fun <T> query(sqlFunc: Connection.() -> T): T
{
	val connection = Database.connection()
	connection.use{ return sqlFunc(connection) }
}