package com.swordfish.lemuroid.lib.library.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import com.swordfish.lemuroid.lib.library.db.entity.Game

class GameSearchDao(private val internalDao: Internal) {
    object CALLBACK : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            MIGRATION.migrate(db)
            createFtsUpdateTriggers(db)
        }
    }

    object MIGRATION : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE VIRTUAL TABLE fts_games USING FTS4(
                  tokenize=unicode61 "remove_diacritics=1",
                  content="games",
                  title);
                """,
            )
            database.execSQL(
                """
                CREATE TRIGGER games_bu BEFORE UPDATE ON games BEGIN
                  DELETE FROM fts_games WHERE docid=old.id;
                END;
                """,
            )
            database.execSQL(
                """
                CREATE TRIGGER games_bd BEFORE DELETE ON games BEGIN
                  DELETE FROM fts_games WHERE docid=old.id;
                END;
                """,
            )
            database.execSQL(
                """
                CREATE TRIGGER games_au AFTER UPDATE ON games BEGIN
                  INSERT INTO fts_games(docid, title) VALUES(new.id, COALESCE(new.customName, new.title));
                END;
                """,
            )
            database.execSQL(
                """
                CREATE TRIGGER games_ai AFTER INSERT ON games BEGIN
                  INSERT INTO fts_games(docid, title) VALUES(new.id, COALESCE(new.customName, new.title));
                END;
                """,
            )
            database.execSQL(
                """
                INSERT INTO fts_games(docid, title) SELECT id, COALESCE(customName, title) FROM games;
                """,
            )
        }
    }

    companion object {
        /**
         * The search index only depends on the displayed name, so it's only refreshed when it changes. Updating it on
         * any change (favorites, last played, every game on every library scan) was a significant cost.
         */
        internal fun createFtsUpdateTriggers(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TRIGGER IF EXISTS games_bu")
            database.execSQL("DROP TRIGGER IF EXISTS games_au")
            database.execSQL(
                """
                CREATE TRIGGER games_bu BEFORE UPDATE OF title, customName ON games BEGIN
                  DELETE FROM fts_games WHERE docid = old.id;
                END;
                """.trimIndent(),
            )
            database.execSQL(
                """
                CREATE TRIGGER games_au AFTER UPDATE OF title, customName ON games BEGIN
                  INSERT INTO fts_games(docid, title)
                  VALUES(new.id, COALESCE(new.customName, new.title));
                END;
                """.trimIndent(),
            )
        }

        internal fun migrateFtsForVersion10(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TRIGGER IF EXISTS games_bu")
            database.execSQL("DROP TRIGGER IF EXISTS games_bd")
            database.execSQL("DROP TRIGGER IF EXISTS games_au")
            database.execSQL("DROP TRIGGER IF EXISTS games_ai")

            database.execSQL(
                """
                CREATE TRIGGER games_bu BEFORE UPDATE ON games BEGIN
                  DELETE FROM fts_games WHERE docid = old.id;
                END;
                """.trimIndent(),
            )
            database.execSQL(
                """
                CREATE TRIGGER games_bd BEFORE DELETE ON games BEGIN
                  DELETE FROM fts_games WHERE docid = old.id;
                END;
                """.trimIndent(),
            )
            database.execSQL(
                """
                CREATE TRIGGER games_au AFTER UPDATE ON games BEGIN
                  INSERT INTO fts_games(docid, title)
                  VALUES(new.id, COALESCE(new.customName, new.title));
                END;
                """.trimIndent(),
            )
            database.execSQL(
                """
                CREATE TRIGGER games_ai AFTER INSERT ON games BEGIN
                  INSERT INTO fts_games(docid, title)
                  VALUES(new.id, COALESCE(new.customName, new.title));
                END;
                """.trimIndent(),
            )

            database.execSQL("DELETE FROM fts_games")
            database.execSQL(
                """
                INSERT INTO fts_games(docid, title)
                SELECT id, COALESCE(customName, title) FROM games
                """.trimIndent(),
            )
        }
    }

    fun search(query: String): PagingSource<Int, Game> =
        internalDao.rawSearch(
            SimpleSQLiteQuery(
                """
                SELECT games.*
                    FROM fts_games
                    JOIN games ON games.id = fts_games.docid
                    WHERE fts_games MATCH ?
                """,
                arrayOf(query),
            ),
        )

    @Dao
    interface Internal {
        @RawQuery(observedEntities = [(Game::class)])
        fun rawSearch(query: SupportSQLiteQuery): PagingSource<Int, Game>
    }
}
