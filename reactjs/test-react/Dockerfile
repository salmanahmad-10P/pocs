# buildah bud -f Dockerfile -t test-react:0.0.2 .
# podman run -it --rm -d --publish 3000:3000 --name test-react localhost/test-react:0.0.2

FROM registry.redhat.io/ubi8/nodejs-12:1-45

WORKDIR /usr/src/test-react

COPY . ./

RUN npm install
RUN npm run-script build

EXPOSE 8080

ENTRYPOINT [ "npm", "run", "start" ]

