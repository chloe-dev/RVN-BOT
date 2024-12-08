package bio.chloe.caches.objects;

import net.dv8tion.jda.api.entities.Member;

public class GuildMessage {
    private final long guildId;
    private final long messageId;
    private final Member messageAuthor;
    private String messageContent;

    private final long creationTime;

    public GuildMessage(long guildId, long messageId, Member messageAuthor, String messageContent) {
        this.guildId = guildId;
        this.messageId = messageId;
        this.messageAuthor = messageAuthor;
        this.messageContent = messageContent;

        this.creationTime = System.currentTimeMillis();
    }

    public long getGuildId() {
        return guildId;
    }

    public long getMessageId() {
        return messageId;
    }

    public Member getMessageAuthor() {
        return messageAuthor;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String newContent) {
        messageContent = newContent;
    }

    public long getCreationTime() {
        return creationTime;
    }
}
