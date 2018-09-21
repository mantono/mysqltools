package com.mantono.mysqltools

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.Closeable
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

interface DatabaseSource: DataSource, Closeable {
	fun <T> query(sqlFunc: Connection.() -> T): T {
		return connection.use(sqlFunc)
	}

	fun <T> atomicQuery(
			transactionLevel: Int = Connection.TRANSACTION_REPEATABLE_READ,
			sqlFunc: Connection.() -> T
	): T {
		connection.autoCommit = false
		connection.transactionIsolation = transactionLevel
		return connection.use(sqlFunc).also {
			connection.commit()
		}
	}
}

class HikkariDatabase(dataSource: HikariDataSource): DatabaseSource, DataSource by dataSource, Closeable by dataSource {
	constructor(properties: Properties): this(HikariDataSource(HikariConfig(properties)))
	constructor(config: HikariConfig = HikariConfig("database.properties")): this(HikariDataSource(config))
}

