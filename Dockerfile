FROM node:24-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.29-alpine
COPY --from=build /app/dist/bomefit/browser /usr/share/nginx/html
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf
COPY deploy/config-api.sh /docker-entrypoint.d/40-config-api.sh
RUN chmod +x /docker-entrypoint.d/40-config-api.sh
EXPOSE 80
