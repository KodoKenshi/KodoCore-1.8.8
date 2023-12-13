package me.kodokenshi.kodocore1_8_8.oop

import me.kodokenshi.kodocore1_8_8.extras.javaPlugin
import me.kodokenshi.kodocore1_8_8.extras.log
import me.kodokenshi.kodocore1_8_8.plugin.KPlugin
import java.sql.*

open class SQL {

    internal var connection: Connection? = null
    var isConnected = false; internal set

    fun connectMySQL(url: String, database: String, user: String, password: String) {

        try { Class.forName("com.mysql.jdbc.Driver") } catch (_: Exception) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't connect to MySQL because driver wasn't found.".log()
            return
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://$url/$database", user, password)
            isConnected = true
        } catch (e: Exception) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't connect to MySQL for database $database.".log()
            e.printStackTrace()
        }

    }

    fun select(vararg column: String, from: String, where: String = ""): Result {

        if (!isConnected) return Result.EMPTY

        if (from.isBlank()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't get because table is blank.".log()
            return Result.EMPTY
        }
        if (column.isEmpty()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't get from \"$from\" because column list is empty.".log()
            return Result.EMPTY
        }

        try {

            val statement = connection!!.prepareStatement(buildString {

                append("SELECT ")
                val lastKey = column.last()
                for (key in column) {

                    append(key)
                    if (key != lastKey) append(", ")

                }
                append(" FROM $from")
                if (where.isNotBlank()) append(" WHERE $where")

            })

            val result = statement.executeQuery()
            val objects = mutableMapOf<String, Content>()

            while (result.next())
                for (key in column) {
                    val get = result.getObject(key)
                    objects[key] = objects[key]?.apply { content.add(get) } ?: Content(mutableListOf(get), 0)
                }

            result.close()
            statement.close()



            return Result(objects)

        } catch (e: Exception) {

            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't get from \"$from\"".log()
            e.printStackTrace()

            return Result.EMPTY

        }

    }
    
    fun createTable(
        name: String,
        columns: Map<String, Type>,
        primaryKey: String = ""
    ): Boolean {

        if (!isConnected) return false

        if (name.isBlank()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't create table because table is blank.".log()
            return false
        }
        if (columns.isEmpty()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't create table \"$name\" because columns map is empty.".log()
            return false
        }

        return try {

            val statement = connection!!.createStatement()

            statement.executeUpdate(buildString {

                append("CREATE TABLE IF NOT EXISTS $name (")

                val lastKey = columns.keys.last()
                for ((key, type) in columns) {

                    append("$key ${type.sql}")
                    if (primaryKey.isNotBlank() && key == primaryKey) append(" PRIMARY KEY")
                    if (key != lastKey) append(", ")

                }
                append(")")

            })

            statement.close()

            true

        } catch (e: Exception) {
            
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't create table \"$name\".".log()
            e.printStackTrace()
            
            false
            
        }
        
    }
    
    fun insertString(table: String, column: String, value: String) = doInsert(table, mapOf(column to value))
    fun <T: Number> insertNumber(table: String, column: String, value: T) = doInsert(table, mapOf(column to value))
    fun <T: Date> insertDate(table: String, column: String, value: T) = doInsert(table, mapOf(column to value))
    fun <T: Blob> insertBlob(table: String, column: String, value: T) = doInsert(table, mapOf(column to value))
    fun <T: SQLXML> insertSQLXML(table: String, column: String, value: T) = doInsert(table, mapOf(column to value))
    fun insertString(table: String, columnValue: Map<String, String>) = doInsert(table, columnValue)
    fun <T: Number> insertNumber(table: String, columnValue: Map<String, T>) = doInsert(table, columnValue)
    fun <T: Date> insertDate(table: String, columnValue: Map<String, T>) = doInsert(table, columnValue)
    fun <T: Blob> insertBlob(table: String, columnValue: Map<String, T>) = doInsert(table, columnValue)
    fun <T: SQLXML> insertSQLXML(table: String, columnValue: Map<String, T>) = doInsert(table, columnValue)
    private fun doInsert(
        table: String,
        columnValue: Map<String, Any>
    ): Boolean {

        if (!isConnected) return false

        if (table.isBlank()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't insert because table is blank.".log()
            return false
        }
        if (columnValue.isEmpty()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't insert into table \"$table\" because columnValue list is empty.".log()
            return false
        }

        return try {

            val statement = connection!!.prepareStatement(buildString {

                append("INSERT OR REPLACE INTO $table (")
                
                val columns = columnValue.keys
                
                val lastKey = columns.last()
                for (key in columns) {

                    append(key)
                    if (key != lastKey) append(", ")

                }
                append(") VALUES (")

                val size = columns.size - 1
                repeat(size + 1) {

                    append("?")
                    if (it != size) append(", ")

                }
                append(")")

            })

            val values = columnValue.values
            
            for ((index, content) in values.withIndex()) {

                val type = getType(content)

                if (type == Type.INVALID) {
                    "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't insert into table \"$table\" because value type is invalid. (are you using this method through reflection?)".log()
                    continue
                }
                
                type.set(statement, index + 1, content)
                
            }

            statement.executeUpdate()
            statement.close()

            true

        } catch (e: Exception) {

            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't insert into table \"$table\".".log()
            e.printStackTrace()

            false

        }

    }

    fun deleteString(table: String, column: String, value: String) = doDeleteFrom(table, column, value)
    fun <T: Number> deleteNumber(table: String, column: String, value: T) = doDeleteFrom(table, column, value)
    fun <T: Date> deleteDate(table: String, column: String, value: T) = doDeleteFrom(table, column, value)
    fun <T: Blob> deleteBlob(table: String, column: String, value: T) = doDeleteFrom(table, column, value)
    fun <T: SQLXML> deleteSQLXML(table: String, column: String, value: T) = doDeleteFrom(table, column, value)
    private fun doDeleteFrom(
        table: String,
        column: String,
        value: Any
    ): Boolean {

        if (!isConnected) return false

        if (table.isBlank()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't delete because table is blank.".log()
            return false
        }
        if (column.isBlank()) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't delete because column is blank.".log()
            return false
        }

        val type = getType(value)

        if (type == Type.INVALID) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't delete \"$column\" from table \"$table\" because value type is invalid. (are you using this method through reflection?)".log()
            return false
        }

        return try {

            val statement = connection!!.prepareStatement("DELETE FROM $table WHERE $column = ?")

            type.set(statement, 1, value)

            val result = statement.executeUpdate()

            statement.close()
            result > 0

        } catch (e: Exception) {
            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't delete \"$column\" from table \"$table\".".log()
            e.printStackTrace()
            false
        }

    }

    fun closeConnection() {
        isConnected = false
        connection?.close()
    }

    private fun <T> getType(type: T) = when(type) {
        is String -> Type.STRING
        is Byte -> Type.BYTE
        is Short -> Type.SHORT
        is Int -> Type.INT
        is Long -> Type.LONG
        is Float -> Type.FLOAT
        is Double -> Type.DOUBLE
        is Timestamp -> Type.TIMESTAMP
        is Time -> Type.TIME
        is Date -> Type.DATE
        is Boolean -> Type.BOOLEAN
        is Blob -> Type.BLOB
        is SQLXML -> Type.XML
        else -> Type.INVALID
    }

    enum class Type(
        internal inline val sql: String,
        internal inline val get: (ResultSet, String) -> Any,
        internal inline val set: (PreparedStatement, Int, Any) -> Unit,
    ) {

        STRING("TEXT", { result, key -> result.getString(key) }, { statement, index, value -> statement.setString(index, value as String) }),
        BYTE("TINYINT", { result, key -> result.getByte(key) }, { statement, index, value -> statement.setByte(index, value as Byte) }),
        SHORT("SMALLINT", { result, key -> result.getShort(key) }, { statement, index, value -> statement.setShort(index, value as Short) }),
        INT("INTEGER", { result, key -> result.getInt(key) }, { statement, index, value -> statement.setInt(index, value as Int) }),
        LONG("BIGINT", { result, key -> result.getLong(key) }, { statement, index, value -> statement.setLong(index, value as Long) }),
        FLOAT("FLOAT", { result, key -> result.getFloat(key) }, { statement, index, value -> statement.setFloat(index, value as Float) }),
        DOUBLE("FLOAT", { result, key -> result.getDouble(key) }, { statement, index, value -> statement.setDouble(index, value as Double) }),
        DATE("DATE", { result, key -> result.getDate(key) }, { statement, index, value -> statement.setDate(index, value as Date) }),
        TIME("TIME", { result, key -> result.getTime(key) }, { statement, index, value -> statement.setTime(index, value as Time) }),
        TIMESTAMP("TIMESTAMP", { result, key -> result.getTimestamp(key) }, { statement, index, value -> statement.setTimestamp(index, value as Timestamp) }),
        BOOLEAN("BOOLEAN", { result, key -> result.getBoolean(key) }, { statement, index, value -> statement.setBoolean(index, value as Boolean) }),
        BLOB("BLOB", { result, key -> result.getBlob(key) }, { statement, index, value -> statement.setBlob(index, value as Blob) }),
        XML("XML", { result, key -> result.getSQLXML(key) }, { statement, index, value -> statement.setSQLXML(index, value as SQLXML) }),
        INVALID("", { _, _ -> 0 }, { _, _, _ -> });

    }

    internal class Content(
        inline val content: MutableList<Any?>,
        inline var index: Int
    )
    class Result internal constructor(private inline val objects: MutableMap<String, Content>) {

        companion object { internal val EMPTY = Result(mutableMapOf()) }

        fun isEmpty() = objects.isEmpty()
        fun firstColumn() = objects.keys.firstOrNull()
        
        fun hasColumn(column: String) = objects.containsKey(column)
        @Deprecated("Only for first column.") fun removeColumn() = if (isEmpty()) false else removeColumn(objects.keys.first())
        fun removeColumn(column: String): Boolean {
            if (!hasColumn(column)) return false
            objects.remove(column)
            return true
        }
        
        @Deprecated("Only for first column.") fun hasNext() = if (isEmpty()) false else hasNext(objects.keys.first())
        fun hasNext(column: String): Boolean {
            
            val content = objects[column] ?: return false
            return content.index < content.content.size
            
        }

        @Deprecated("Only for first column.") fun nextObject() = if (isEmpty()) null else nextObject(objects.keys.first())
        @Deprecated("Only for first column.") fun nextObjectOrElse(or: Any) = nextObject() ?: or
        fun nextObject(column: String): Any? {

            val content = objects[column] ?: return null
            
            if (content.index >= content.content.size) return null
            
            val get = content.content.getOrNull(content.index)
            content.index += 1
            
            return get
            
        }
        fun nextObjectOrElse(column: String, or: Any) = nextObject(column) ?: or
        @Deprecated("Only for first column.") fun getObject(index: Int = 0) = if (isEmpty()) null else objects[objects.keys.first()]?.content?.getOrNull(index)
        fun getObject(column: String, index: Int = 0) = objects[column]?.content?.getOrNull(index)
        fun getObjectOrElse(column: String, index: Int = 0, or: Any) = getObject(column, index) ?: or

        @Deprecated("Only for first column.") fun getString(index: Int = 0) = getObject(index) as? String?
        @Deprecated("Only for first column.") fun getInt(index: Int = 0) = getObject(index) as? Int?
        @Deprecated("Only for first column.") fun getShort(index: Int = 0) = getObject(index) as? Short?
        @Deprecated("Only for first column.") fun getByte(index: Int = 0) = getObject(index) as? Byte?
        @Deprecated("Only for first column.") fun getDouble(index: Int = 0) = getObject(index) as? Double?
        @Deprecated("Only for first column.") fun getFloat(index: Int = 0) = getObject(index) as? Float?
        @Deprecated("Only for first column.") fun getLong(index: Int = 0) = getObject(index) as? Long?
        @Deprecated("Only for first column.") fun getJson(index: Int = 0) = getString(index)?.toJson()
        @Deprecated("Only for first column.") fun getJsonOrEmpty(index: Int = 0) = getString(index)?.toJsonEmptyIfError() ?: Json()
        
        @Deprecated("Only for first column.") fun getStringOrElse(index: Int = 0, or: String) = getString(index) ?: or
        @Deprecated("Only for first column.") fun getIntOrElse(index: Int = 0, or: Int) = getInt(index) ?: or
        @Deprecated("Only for first column.") fun getShortOrElse(index: Int = 0, or: Short) = getShort(index) ?: or
        @Deprecated("Only for first column.") fun getByteOrElse(index: Int = 0, or: Byte) = getByte(index) ?: or
        @Deprecated("Only for first column.") fun getDoubleOrElse(index: Int = 0, or: Double) = getDouble(index) ?: or
        @Deprecated("Only for first column.") fun getFloatOrElse(index: Int = 0, or: Float) = getFloat(index) ?: or
        @Deprecated("Only for first column.") fun getLongOrElse(index: Int = 0, or: Long) = getLong(index) ?: or
        @Deprecated("Only for first column.") fun getJsonOrElse(index: Int = 0, or: Json) = try { getJson(index) ?: or } catch (_: Exception) { or }
        
        fun getString(column: String, index: Int = 0) = getObject(column, index) as? String?
        fun getInt(column: String, index: Int = 0) = getObject(column, index) as? Int?
        fun getShort(column: String, index: Int = 0) = getObject(column, index) as? Short?
        fun getByte(column: String, index: Int = 0) = getObject(column, index) as? Byte?
        fun getDouble(column: String, index: Int = 0) = getObject(column, index) as? Double?
        fun getFloat(column: String, index: Int = 0) = getObject(column, index) as? Float?
        fun getLong(column: String, index: Int = 0) = getObject(column, index) as? Long?
        fun getJson(column: String, index: Int = 0) = getString(column, index)?.toJson()
        fun getJsonOrEmpty(column: String, index: Int = 0) = getString(column, index)?.toJsonEmptyIfError() ?: Json()

        fun getStringOrElse(column: String, index: Int = 0, or: String) = getString(column, index) ?: or
        fun getIntOrElse(column: String, index: Int = 0, or: Int) = getInt(column, index) ?: or
        fun getShortOrElse(column: String, index: Int = 0, or: Short) = getShort(column, index) ?: or
        fun getByteOrElse(column: String, index: Int = 0, or: Byte) = getByte(column, index) ?: or
        fun getDoubleOrElse(column: String, index: Int = 0, or: Double) = getDouble(column, index) ?: or
        fun getFloatOrElse(column: String, index: Int = 0, or: Float) = getFloat(column, index) ?: or
        fun getLongOrElse(column: String, index: Int = 0, or: Long) = getLong(column, index) ?: or
        fun getJsonOrElse(column: String, index: Int = 0, or: Json) = try { getJson(column, index) ?: or } catch (_: Exception) { or }
        
        @Deprecated("Only for first column.") fun nextString() = nextObject() as? String?
        @Deprecated("Only for first column.") fun nextInt() = nextObject() as? Int?
        @Deprecated("Only for first column.") fun nextShort() = nextObject() as? Short?
        @Deprecated("Only for first column.") fun nextByte() = nextObject() as? Byte?
        @Deprecated("Only for first column.") fun nextDouble() = nextObject() as? Double?
        @Deprecated("Only for first column.") fun nextFloat() = nextObject() as? Float?
        @Deprecated("Only for first column.") fun nextLong() = nextObject() as? Long?
        @Deprecated("Only for first column.") fun nextJson() = nextString()?.toJson()
        @Deprecated("Only for first column.") fun nextJsonOrEmpty() = nextString()?.toJsonEmptyIfError() ?: Json()

        @Deprecated("Only for first column.") fun nextStringOrElse(or: String) = nextObject() ?: or
        @Deprecated("Only for first column.") fun nextIntOrElse(or: Int) = nextInt() ?: or
        @Deprecated("Only for first column.") fun nextShortOrElse(or: Short) = nextShort() ?: or
        @Deprecated("Only for first column.") fun nextByteOrElse(or: Byte) = nextByte() ?: or
        @Deprecated("Only for first column.") fun nextDoubleOrElse(or: Double) = nextDouble() ?: or
        @Deprecated("Only for first column.") fun nextFloatOrElse(or: Float) = nextFloat() ?: or
        @Deprecated("Only for first column.") fun nextLongOrElse(or: Long) = nextLong() ?: or
        @Deprecated("Only for first column.") fun nextJsonOrElse(or: Json) = try { nextJson() ?: or } catch (_: Exception) { or }
        
        fun nextString(column: String) = nextObject(column) as? String?
        fun nextInt(column: String) = nextObject(column) as? Int?
        fun nextShort(column: String) = nextObject(column) as? Short?
        fun nextByte(column: String) = nextObject(column) as? Byte?
        fun nextDouble(column: String) = nextObject(column) as? Double?
        fun nextFloat(column: String) = nextObject(column) as? Float?
        fun nextLong(column: String) = nextObject(column) as? Long?
        fun nextJson(column: String) = nextString(column)?.toJson()
        fun nextJsonOrEmpty(column: String) = nextString(column)?.toJsonEmptyIfError() ?: Json()

        fun nextStringOrElse(column: String, or: String) = nextObject(column) ?: or
        fun nextIntOrElse(column: String, or: Int) = nextInt(column) ?: or
        fun nextShortOrElse(column: String, or: Short) = nextShort(column) ?: or
        fun nextByteOrElse(column: String, or: Byte) = nextByte(column) ?: or
        fun nextDoubleOrElse(column: String, or: Double) = nextDouble(column) ?: or
        fun nextFloatOrElse(column: String, or: Float) = nextFloat(column) ?: or
        fun nextLongOrElse(column: String, or: Long) = nextLong(column) ?: or
        fun nextJsonOrElse(column: String, or: Json) = try { nextJson(column) ?: or } catch (_: Exception) { or }
        
    }

}