package net.andorya.llomya.redis;

public record RedisCredentials(String hostname, String password, int port) {
    public static class Builder {
        private String hostname, password;
        private int port;

        public static Builder create() {
            return new Builder();
        }

        public Builder withHostname(String hostname) {
            this.hostname = hostname;
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

        public RedisCredentials build() {
            return new RedisCredentials(hostname, password, port);
        }
    }
}
