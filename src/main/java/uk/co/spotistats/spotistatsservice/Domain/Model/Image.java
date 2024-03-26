package uk.co.spotistats.spotistatsservice.Domain.Model;

public record Image(String uri, Integer height, Integer width) {


    public static final class Builder {
        private String uri;
        private Integer height;
        private Integer width;

        private Builder() {
        }

        public static Builder anImage() {
            return new Builder();
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withHeight(Integer height) {
            this.height = height;
            return this;
        }

        public Builder withWidth(Integer width) {
            this.width = width;
            return this;
        }

        public Image build() {
            return new Image(uri, height, width);
        }
    }
}
