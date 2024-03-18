package uk.co.spotistats.spotistatsservice.Domain.Model;

public record User(String id, Image image, String url) {

    public static final class Builder {
        private String id;
        private Image image;
        private String url;

        private Builder() {
        }

        public static Builder anUser() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withImage(Image image) {
            this.image = image;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public User build() {
            return new User(id, image, url);
        }
    }
}
