package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
class Helpers {
    private Helpers() {}

    public static class MessageHandler {
        private final BufferedReader reader;
        private final BufferedWriter writer;

        private MessageHandler() {
            this.reader = null;
            this.writer = null;
        }

        protected MessageHandler(BufferedReader reader, BufferedWriter writer) {
            this.reader = Objects.requireNonNull(reader);
            this.writer = Objects.requireNonNull(writer);
        }

        public void dispose() throws IOException {
            Preconditions.checkArgument(this.reader != null);
            Preconditions.checkArgument(this.writer != null);

            this.reader.close();
            this.writer.close();
        }

        public boolean ready() {
            if (this.reader == null) return false;
            try {
                return this.reader.ready();
            } catch (IOException ignored) {
                return false;
            }
        }

        private static String formatMessage(MessageId messageId, String message) {
            return String.format("%s %s\n", messageId.name(), message);
        }

        public void post(MessageId messageId, String message) {
            Preconditions.checkArgument(this.writer != null);

            try {
                this.writer.write(formatMessage(messageId, message));
                this.writer.flush();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        public Payload get() {
            Preconditions.checkArgument(this.reader != null);

            try {
                return new Payload(this.reader.readLine());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    public static class Payload {
        private final MessageId id;
        private final List<String> content;

        public Payload(String message) {
            String[] messageParts = message.split(Pattern.quote(" "), -1);

            Preconditions.checkArgument(messageParts.length >= 2);

            this.id = MessageId.valueOf(messageParts[0]);
            this.content = List.of(messageParts).subList(1, messageParts.length);
        }

        public MessageId id() {
            return this.id;
        }

        public List<String> content() {
            return this.content;
        }
    }
}
