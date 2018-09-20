package com.mantono.mysqltools

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object Database {
	private val dataSource: HikariDataSource

	init {
		val config = HikariConfig("database.properties")
		if(config.jdbcUrl.isNullOrEmpty())
			config.jdbcUrl = System.getenv("JDBC_URL") ?: throw IllegalArgumentException("No valid JDBC URL found")
		config.validate()
		dataSource = HikariDataSource(config)
	}

	fun connection(): Connection = dataSource.connection
}

inline fun <T> query(
		connection: Connection = Database.connection(),
		sqlFunc: Connection.() -> T
): T {
	connection.use { return sqlFunc(connection) }
}

inline fun <T> atomicQuery(
		connection: Connection = Database.connection(),
		transactionLevel: Int = Connection.TRANSACTION_REPEATABLE_READ,
		sqlFunc: Connection.() -> T
): T {
	connection.autoCommit = false
	connection.transactionIsolation = transactionLevel
	return query(connection) { sqlFunc(connection) }.also {
		connection.commit()
	}
}