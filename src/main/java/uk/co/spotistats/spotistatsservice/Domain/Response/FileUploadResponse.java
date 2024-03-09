package uk.co.spotistats.spotistatsservice.Domain.Response;

public record FileUploadResponse(String fileName, String contentType, long fileSize) {

    public static final class Builder {
        private String fileName;
        private String contentType;
        private long fileSize;

        private Builder() {
        }

        public static Builder aFileUploadResponse() {
            return new Builder();
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder withFileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public FileUploadResponse build() {
            return new FileUploadResponse(fileName, contentType, fileSize);
        }
    }
}
