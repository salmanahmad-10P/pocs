# buildah bud -f Dockerfile -t test-react:0.0.4 .
# dev mode:
#    podman run -it --rm -d --publish 8080:8080 --name test-react localhost/test-react:0.0.4 yarn start

# prod mod:
#    podman run -it --rm -d --publish 8080:8080 --name test-react localhost/test-react:0.0.4

# ENV VARIABLES:
#  https://catalog.redhat.com/software/containers/ubi8/nodejs-12/5d3fff015a13461f5fb8635a?container-tabs=overview&gti-tabs=registry-tokens

FROM registry.redhat.io/ubi8/nodejs-12:1-45

WORKDIR /usr/src/test-react

COPY . ./

RUN npm install -g yarn
RUN npm install -g serve
RUN npm install
RUN yarn build

EXPOSE 8080

CMD [ "serve", "-s", "build", "-l", "8080" ]

