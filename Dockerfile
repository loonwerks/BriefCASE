ARG BASE_IMG=maven:3.8.3-eclipse-temurin-17
FROM $BASE_IMG

ARG UID
ARG UNAME
ARG GID
ARG GROUP

RUN groupadd -fg "${GID}" "${GROUP}" \
  && groupmod -g "${GID}" "${GROUP}" \
  && useradd -u "${UID}" -g "${GID}" "${UNAME}" \
  && passwd -d "${UNAME}" \
  && mkdir "/home/${UNAME}" \
  && chown -R "${UNAME}":"${GROUP}" "/home/${UNAME}" \
  && chmod -R ug+rw "/home/${UNAME}"

VOLUME /home/${UNAME}

RUN apt-get -q update \
  && apt-get -y -q install curl pandoc sudo vim wget \
  && apt-get install -y --no-install-recommends xvfb openjfx \
  && apt-get clean autoclean \
  && apt-get autoremove --purge --yes \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /host

