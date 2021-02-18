FROM adoptopenjdk/openjdk11:alpine-slim

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

RUN apk --no-cache upgrade && apk --no-cache add ca-certificates

# Add apiservver user and group
RUN addgroup apiserver && adduser -D -G apiserver apiserver
# Create apiserver directory to run application
RUN mkdir -p /home/apiserver/app && chown -R apiserver:apiserver /home/apiserver
# Set user to apiserver:apiserver
USER apiserver:apiserver

ENV PORT 8080
EXPOSE 8080

ADD --chown=apiserver:apiserver ./application/app/build/distributions/app-1.0.tar /home/apiserver/app
WORKDIR /home/apiserver/app
CMD ["/home/apiserver/app/app-1.0/bin/app"]
