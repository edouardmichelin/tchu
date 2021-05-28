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
 * Classe d'outils pour le package <i>ch.epfl.tchu.net</i>
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class Helpers {
    private Helpers() {}

    /**
     * Repprésente un gestionnaire de messages envoyés sur le réseau
     */
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

        /**
         * Libère l'espace pris par le lecteur et l'écrivain
         * @throws IOException
         */
        public void dispose() throws IOException {
            Preconditions.checkArgument(this.reader != null);
            Preconditions.checkArgument(this.writer != null);

            this.reader.close();
            this.writer.close();
        }

        /**
         * Retourne <i>true</i> ssi le lecteur est prêt à lire un message sur le réseau
         * @return
         */
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

        /**
         * Envois un message sur le réseau
         * @param messageId id du message
         * @param message contenu seérialisé du message
         */
        public void post(MessageId messageId, String message) {
            Preconditions.checkArgument(this.writer != null);

            try {
                this.writer.write(formatMessage(messageId, message));
                this.writer.flush();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        /**
         * Cherche un message sur le réseau et le récupère
         * @return un <code>Payload</code> qui contient le message
         */
        public Payload get() {
            Preconditions.checkArgument(this.reader != null);

            try {
                return new Payload(this.reader.readLine());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    /**
     * Représente le contenu d'un message qui transit sur le réseau
     */
    public static class Payload {
        private final MessageId id;
        private final List<String> content;

        /**
         * Construit un message destiné à être envoyé sur le réseau
         * @param message
         */
        public Payload(String message) {
            String[] messageParts = message.split(Pattern.quote(" "), -1);

            Preconditions.checkArgument(messageParts.length >= 1);

            this.id = MessageId.valueOf(messageParts[0]);
            this.content = List.of(messageParts).subList(1, messageParts.length);
        }

        /**
         * Retourne l'id du message
         * @return l'id du message
         */
        public MessageId id() {
            return this.id;
        }

        /**
         * Retourne le contenu sérialisé du message
         * @return le contenu sérialisé du message
         */
        public List<String> content() {
            return this.content;
        }
    }
}
