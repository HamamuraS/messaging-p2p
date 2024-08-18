
const server_ip = '192.168.1.102'

const dev = {
  API_URL: 'http://'+server_ip+':8080',
  WS_URL: 'ws://'+server_ip+':8080'
};
  
const prod = {
  API_URL: 'https://api.midominio.com',
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