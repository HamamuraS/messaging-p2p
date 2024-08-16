
const dev = {
    API_URL: 'http://192.168.1.102:8080',
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