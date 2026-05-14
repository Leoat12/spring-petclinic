ARG VARIANT=21-bookworm
FROM mcr.microsoft.com/vscode/devcontainers/java:0-${VARIANT}
ARG NODE_VERSION="none"
RUN if [ "${NODE_VERSION}" != "none" ]; then su vscode -c "umask 0002 && . /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi
ARG USER=vscode
VOLUME /home/$USER/.gradle
ARG JAVA_VERSION=21.0.7-ms
RUN bash -lc '. /usr/local/sdkman/bin/sdkman-init.sh && sdk install java $JAVA_VERSION && sdk use java $JAVA_VERSION'