package kotlin.sql

import java.sql.Connection
import java.util.LinkedHashMap

class UpdateQuery(val table: Table, val where: Op) {
    val values = LinkedHashMap<Column<*>, Any>()

    fun <T> set(column: Column<T>, value: T) {
        if (values containsKey column) {
            throw RuntimeException("$column is already initialized")
        }
        values[column] = value
    }

    fun execute(session: Session) {
        if (!values.isEmpty()) {
            var sql = StringBuilder("UPDATE ${session.identity(table)}")
            var c = 0;
            sql.append(" ")
            for ((col, value) in values) {
                sql.append("SET ").append(session.identity(col)).append(" = ")
                when (col.columnType) {
                    is ColumnType.STRING -> sql.append("'").append(value).append("'")
                    else -> sql.append(value)
                }
                c++
                if (c < values.size()) {
                    sql.append(", ")
                }
            }
            sql.append(" WHERE " + where.toSQL())
            println("SQL: " + sql)
            session.connection.createStatement()!!.executeUpdate(sql.toString())
        }
    }
}