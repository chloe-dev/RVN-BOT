package bio.chloe.caches.objects;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GuildMessage {
    private final long GUILD_ID;
    private final long CHANNEL_ID;

    private final long MESSAGE_ID;
    private final String MESSAGE_CONTENT;
    private final List<Message.Attachment> MESSAGE_ATTACHMENT_LIST;

    private final long AUTHOR_ID;
    private final Member AUTHOR;

    private final AtomicLong lastAccessed = new AtomicLong(System.currentTimeMillis());

    private GuildMessage(Builder guildMessageBuilder) {
        this.GUILD_ID = guildMessageBuilder.GUILD_ID;
        this.CHANNEL_ID = guildMessageBuilder.CHANNEL_ID;

        this.MESSAGE_ID = guildMessageBuilder.MESSAGE_ID;
        this.MESSAGE_CONTENT = guildMessageBuilder.messageContent;
        this.MESSAGE_ATTACHMENT_LIST = guildMessageBuilder.messageAttachmentList;

        this.AUTHOR_ID = guildMessageBuilder.AUTHOR_ID;
        this.AUTHOR = guildMessageBuilder.AUTHOR;

        // TODO: may be required for proper caching behavior with regard to message edits. - updateLastAccessed();
    }

    public long getGuildId() { updateLastAccessed(); return this.GUILD_ID; }
    public long getChannelId() { updateLastAccessed(); return this.CHANNEL_ID; }

    public long getMessageId() { updateLastAccessed(); return this.MESSAGE_ID; }
    public String getMessageContent() { updateLastAccessed(); return this.MESSAGE_CONTENT; }
    public List<Message.Attachment> getMessageAttachmentList() { updateLastAccessed(); return this.MESSAGE_ATTACHMENT_LIST; }

    public long getAuthorId() { updateLastAccessed(); return this.AUTHOR_ID; }
    public Member getAuthor() { updateLastAccessed(); return this.AUTHOR; }

    public long getLastAccessed() {
        return lastAccessed.get();
    }

    private void updateLastAccessed() {
        lastAccessed.set(System.currentTimeMillis());
    }

    public static class Builder {
        private final long GUILD_ID;
        private final long CHANNEL_ID;

        private final long MESSAGE_ID;
        private String messageContent;
        private List<Message.Attachment> messageAttachmentList;

        private final long AUTHOR_ID;
        private final Member AUTHOR;


        public Builder(long guildId, long channelId, long messageId, long authorId, Member author) {
            this.GUILD_ID = guildId;
            this.CHANNEL_ID = channelId;

            this.MESSAGE_ID = messageId;
            this.AUTHOR_ID = authorId;

            this.AUTHOR = author;
        }

        public static Builder from(GuildMessage originalGuildMessage) {
            return new Builder(
                    originalGuildMessage.GUILD_ID,
                    originalGuildMessage.CHANNEL_ID,
                    originalGuildMessage.MESSAGE_ID,
                    originalGuildMessage.AUTHOR_ID,
                    originalGuildMessage.AUTHOR
            )
                    .setMessageContent(originalGuildMessage.MESSAGE_CONTENT)
                    .setMessageAttachmentList(originalGuildMessage.MESSAGE_ATTACHMENT_LIST);
        }

        public Builder setMessageContent(String messageContent) {
            this.messageContent = messageContent; return this;
        }

        public Builder setMessageAttachmentList(List<Message.Attachment> messageAttachmentList) {
            this.messageAttachmentList = messageAttachmentList; return this;
        }

        public GuildMessage build() {
            return new GuildMessage(this);
        }
    }
}
