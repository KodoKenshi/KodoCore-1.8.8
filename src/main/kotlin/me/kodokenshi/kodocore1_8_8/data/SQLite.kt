package me.kodokenshi.kodocore1_8_8.data

import com.github.kittinunf.fuel.httpDownload
import me.kodokenshi.kodocore1_8_8.extras.javaPlugin
import me.kodokenshi.kodocore1_8_8.extras.log
import me.kodokenshi.kodocore1_8_8.oop.SQL
import me.kodokenshi.kodocore1_8_8.plugin.KPlugin
import java.io.File
import java.net.URLClassLoader
import java.sql.DriverManager

inline fun sqlite(fileName: String, block: SQLite.() -> Unit) = SQLite(fileName).apply(block).closeConnection()
inline fun sqlite(filePath: String, fileName: String, block: SQLite.() -> Unit) = SQLite(filePath, fileName).apply(block).closeConnection()

class SQLite: SQL {

    val filePath: String
    val name: String

    constructor(filePath: String, fileName: String) {

        this.filePath = filePath
        name = "$fileName.db"

        try {

            if (!loadSQLite()) {
                "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't connect to SQLite for file \"$name\" in folder \"${filePath}\".".log()
                return
            }

            File(filePath).apply { if (!exists()) mkdirs() }

            connection = DriverManager.getConnection("jdbc:sqlite:${filePath}/$name")

            isConnected = true

        } catch (e: Exception) {

            "&9${javaPlugin<KPlugin>().name} - SQL> &7Couldn't connect to SQLite for file \"$name\" in folder \"${filePath}\".".log()
            e.printStackTrace()

        }

    }
    constructor(fileName: String): this("plugins/${javaPlugin<KPlugin>().name}/SQLite", fileName)

    companion object { private var sqliteLoaded = false }

    private fun loadSQLite(): Boolean {

        if (sqliteLoaded) return true

        return try {

            var canReturn = true
            var loaded = true

            try { Class.forName("org.sqlite.JDBC"); } catch (_: Exception) {

                val sqlite = File("plugins/KodoCore/SQLite-3.43.0.0.jar")

                if (!sqlite.exists()) {
                    
                    canReturn = false
                    "&9${javaPlugin<KPlugin>().name} - SQLite> &7Downloading SQLite...".log()
                    "https://github.com/xerial/sqlite-jdbc/releases/download/3.43.0.0/sqlite-jdbc-3.43.0.0.jar"
                        .httpDownload()
                        .fileDestination { _, _ ->
                            File("plugins/KodoCore").mkdirs()
                            sqlite
                        }.response { _, _, result ->

                            result.fold(
                                success = {

                                    "&9${javaPlugin<KPlugin>().name} - SQL> &aSQLite downloaded.".log()
                                    loaded = true
                                    canReturn = true

                                },
                                failure = {

                                    "&9${javaPlugin<KPlugin>().name} - SQL> &cCouldn't download SQLite. No data will be saved.".log()
                                    it.exception.printStackTrace()
                                    loaded = false
                                    canReturn = true

                                }
                            )

                        }

                }

                while (!canReturn) { Thread.sleep(100) }

                URLClassLoader(arrayOf(sqlite.toURI().toURL())).loadClass("org.sqlite.JDBC")

            }

            sqliteLoaded = loaded
            loaded

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

}