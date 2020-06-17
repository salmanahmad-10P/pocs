# buildah bud -f Dockerfile -t test-react:0.0.3 .
# podman run -it --rm -d --publish 3000:3000 --name test-react localhost/test-react:0.0.3

# ENV VARIABLES:
#  https://catalog.redhat.com/software/containers/ubi8/nodejs-12/5d3fff015a13461f5fb8635a?container-tabs=overview&gti-tabs=registry-tokens

FROM registry.redhat.io/ubi8/nodejs-12:1-45

WORKDIR /usr/src/test-react

COPY . ./

RUN npm install -g yarn
RUN npm install
RUN yarn build

EXPOSE 3000

ENTRYPOINT [ "yarn", "start" ]

