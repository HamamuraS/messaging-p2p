
const dev = {
    API_URL: 'http://192.168.1.59:8080',
  };
  
  const prod = {
    API_URL: 'https://api.tudominio.com',
  };
  
  const staging = {
    API_URL: 'https://staging-api.tudominio.com',
  };
  
  const config = process.env.REACT_APP_STAGE === 'production'
    ? prod
    : process.env.REACT_APP_STAGE === 'staging'
      ? staging
      : dev;
  
  export default {
    ...config,
  };
  
  export const API_URL = config.API_URL;