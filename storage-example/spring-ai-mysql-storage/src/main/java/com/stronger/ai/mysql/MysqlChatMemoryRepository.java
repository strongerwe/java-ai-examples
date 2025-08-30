package com.stronger.ai.mysql;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class MysqlChatMemoryRepository.class
 * @department Platform R&D
 * @date 2025/7/25
 * @desc MySQLChatMemoryRepository
 * MySQL数据库存储用户上下文
 * 建表：
 * CREATE TABLE `ai_chat_memory` (
 *   `id` bigint NOT NULL AUTO_INCREMENT,
 *   `conversation_id` varchar(256) COLLATE utf8mb4_general_ci NOT NULL,
 *   `content` longtext COLLATE utf8mb4_general_ci NOT NULL,
 *   `type` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
 *   `timestamp` timestamp NOT NULL,
 *   PRIMARY KEY (`id`),
 *   CONSTRAINT `chk_message_type` CHECK ((`type` in (_utf8mb4'USER',_utf8mb4'ASSISTANT',_utf8mb4'SYSTEM',_utf8mb4'TOOL')))
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
public class MysqlChatMemoryRepository extends JdbcChatMemoryRepository {

    private static final String MYSQL_QUERY_ADD = "INSERT INTO ai_chat_memory (conversation_id, content, type, timestamp) VALUES (?, ?, ?, ?)";
    private static final String MYSQL_QUERY_GET = "SELECT content, type FROM ai_chat_memory WHERE conversation_id = ? ORDER BY timestamp";
    public static final String MYSQL_TABLE_NAME = "ai_chat_memory";
    private static final String MYSQL_QUERY_GET_IDS = "SELECT DISTINCT conversation_id FROM ai_chat_memory\n";
    private static final String MYSQL_QUERY_CLEAR = "DELETE FROM ai_chat_memory WHERE conversation_id = ?";

    private MysqlChatMemoryRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String hasTableSql(String tableName) {
        return String.format("SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = '%s'", tableName);
    }

    @Override
    protected String createTableSql(String tableName) {
        return String.format("CREATE TABLE %s (id BIGINT AUTO_INCREMENT PRIMARY KEY, conversation_id VARCHAR(256) NOT NULL, content LONGTEXT NOT NULL, type VARCHAR(100) NOT NULL, timestamp TIMESTAMP NOT NULL, CONSTRAINT chk_message_type CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')))", tableName);
    }

    @Override
    protected String getTableName() {
        return MYSQL_TABLE_NAME;
    }

    @Override
    protected String getClearSql() {
        return MYSQL_QUERY_CLEAR;
    }

    @Override
    protected String getAddSql() {
        return MYSQL_QUERY_ADD;
    }

    @Override
    protected String getGetSql() {
        return MYSQL_QUERY_GET;
    }

    @Override
    protected String getQueryGetIdsSql() {
        return MYSQL_QUERY_GET_IDS;
    }

    public static MysqlBuilder mysqlBuilder() {
        return new MysqlBuilder();
    }

    public static class MysqlBuilder {
        private JdbcTemplate jdbcTemplate;

        public MysqlBuilder jdbcTemplate(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
            return this;
        }

        public MysqlChatMemoryRepository build() {
            return new MysqlChatMemoryRepository(this.jdbcTemplate);
        }
    }
}
