package com.stronger.ai.mysql;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class JdbcChatMemoryRepository.class
 * @department Platform R&D
 * @date 2025/7/26
 * @desc JDBC对话存储实现
 */
public abstract class JdbcChatMemoryRepository implements ChatMemoryRepository {

    public static final String TABLE_NAME = "ai_chat_memory";
    private static final String QUERY_GET_IDS = "SELECT DISTINCT conversation_id FROM ai_chat_memory\n";
    private static final String QUERY_ADD = "INSERT INTO ai_chat_memory (conversation_id, content, type, \"timestamp\") VALUES (?, ?, ?, ?)\n";
    private static final String QUERY_GET = "SELECT content, type FROM ai_chat_memory WHERE conversation_id = ? ORDER BY \"timestamp\"\n";
    private static final String QUERY_CLEAR = "DELETE FROM ai_chat_memory WHERE conversation_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public JdbcChatMemoryRepository(JdbcTemplate jdbcTemplate) {
        Assert.notNull(jdbcTemplate, "jdbcTemplate cannot be null");
        this.jdbcTemplate = jdbcTemplate;
        this.checkAndCreateTable();
    }

    private void checkAndCreateTable() {
        if (Boolean.FALSE.equals(this.jdbcTemplate
                .query(this.hasTableSql(getTableName()),
                        ResultSet::next))) {
            this.jdbcTemplate.execute(
                    this.createTableSql(getTableName()));
        }
    }

    protected abstract String hasTableSql(String tableName);

    protected abstract String createTableSql(String tableName);

    protected String getTableName() {
        return TABLE_NAME;
    }

    protected String getAddSql() {
        return QUERY_ADD;
    }

    protected String getGetSql() {
        return QUERY_GET;
    }

    protected String getClearSql() {
        return QUERY_CLEAR;
    }

    protected String getQueryGetIdsSql() {
        return QUERY_GET_IDS;
    }

    @Override
    public List<String> findConversationIds() {
        List<String> conversationIds =
                this.jdbcTemplate.query(this.getQueryGetIdsSql(), (rs) -> {
                    ArrayList<String> ids = new ArrayList<>();
                    while (rs.next()) {
                        ids.add(rs.getString(1));
                    }
                    return ids;
                });
        return conversationIds != null ? conversationIds : List.of();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        Assert.hasText(conversationId, "会话ID不能为空！");
        return this.jdbcTemplate.query(this.getGetSql(), new MessageRowMapper(), new Object[]{conversationId});
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        Assert.notNull(messages, "messages cannot be null");
        Assert.noNullElements(messages, "messages cannot contain null elements");
        this.deleteByConversationId(conversationId);
        this.jdbcTemplate.batchUpdate(this.getAddSql(), new AddBatchPreparedStatement(conversationId, messages));
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        this.jdbcTemplate.update(this.getClearSql(), new Object[]{conversationId});
    }

    private static record AddBatchPreparedStatement(String conversationId, List<Message> messages,
                                                    AtomicLong instantSeq) implements BatchPreparedStatementSetter {

        private AddBatchPreparedStatement(String conversationId, List<Message> messages) {
            this(conversationId, messages, new AtomicLong(Instant.now().toEpochMilli()));
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Message message = (Message) this.messages.get(i);
            ps.setString(1, this.conversationId);
            ps.setString(2, message.getText());
            ps.setString(3, message.getMessageType().name());
            ps.setTimestamp(4, new Timestamp(this.instantSeq.getAndIncrement()));
        }

        @Override
        public int getBatchSize() {
            return this.messages.size();
        }
    }

    private static class MessageRowMapper implements RowMapper<Message> {
        @Nullable
        @Override
        public Message mapRow(ResultSet rs, int i) throws SQLException {
            String content = rs.getString(1);
            MessageType type = MessageType.valueOf(rs.getString(2));
            Message message;
            switch (type) {
                case USER -> message = new UserMessage(content);
                case ASSISTANT -> message = new AssistantMessage(content);
                case SYSTEM -> message = new SystemMessage(content);
                case TOOL -> message = new ToolResponseMessage(List.of());
                default -> throw new IncompatibleClassChangeError();
            }
            return message;
        }
    }
}
