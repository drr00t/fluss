#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# --------------
# Stream edition
# --------------
FROM eclipse-temurin:17-jre-jammy AS builder

# Install dependencies
RUN set -ex; \
  apt-get update; \
  apt-get -y install gpg libsnappy1v5 gettext-base libjemalloc-dev; \
  rm -rf /var/lib/apt/lists/*

# Please copy build-target to the docker dictory first, then copy to the image.
COPY --chown=fluss:fluss build-target/ /opt/fluss/

 # Prepare environment
ENV FLUSS_HOME=/opt/fluss
ENV PATH=$FLUSS_HOME/bin:$PATH
RUN groupadd --system --gid=9999 fluss && \
     useradd --system --home-dir $FLUSS_HOME --uid=9999 --gid=fluss fluss
WORKDIR $FLUSS_HOME

RUN ["chown", "-R", "fluss:fluss", "."]
COPY docker-entrypoint.sh /
RUN ["chmod", "+x", "/docker-entrypoint.sh"]
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["help"]
