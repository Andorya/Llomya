package net.andorya.llomya.rabbitmq;

public record RabbitMQCredentials(String hostname, String username, String password, int port) {
    public static class Builder {
        private String hostname, username, password;
        private int port;

        public static Builder create() {
            return new Builder();
        }

        public Builder withHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public RabbitMQCredentials build() {
            return new RabbitMQCredentials(hostname, username, password, port);
        }
    }
}
