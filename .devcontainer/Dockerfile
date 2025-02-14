# Use the official Clojure image with tools.deps, Leiningen, and/or Boot
FROM ghcr.io/zcaudate-xyz/infra-foundation-clean:ci

# [Optional] Install additional packages.
# You can run things like:
# RUN apk update && apk add --no-cache <package-name>

# Create a directory for your application (if you want to keep consistent with common patterns).
WORKDIR /workspace

# Copy the current repo into the container (useful if you want to build inside the container).
# You may prefer using "postCreateCommand" in devcontainer.json for performance reasons.
# COPY . /workspace

# Expose any ports needed for live-reload or webserver (if you want).
EXPOSE 8080 10234
