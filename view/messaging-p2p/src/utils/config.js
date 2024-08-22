//const server_ip = '192.168.1.102'; // Puedes eliminar esto si ya no lo necesitas
const server_ip = 'messaging-p2p-production.up.railway.app';
const dev = {
  API_URL: `http://${server_ip}:8080`,
  WS_URL: `ws://${server_ip}:8080`,
};

const prod = {
  API_URL: `http://${process.env.REACT_APP_API_URL}` || 'http://localhost:8080'
};

const staging = {
  API_URL: 'https://staging-api.midominio.com',
};

const config = process.env.REACT_APP_STAGE === 'production'
  ? prod
  : process.env.REACT_APP_STAGE === 'staging'
    ? staging
    : dev;

const exportedConfig = {
  ...config,
};

export default exportedConfig;

export const API_URL = config.API_URL;
export const WS_URL = config.WS_URL;
