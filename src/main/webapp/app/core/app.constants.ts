export class AppConstants {
  static API_BASE_URL = 'http://localhost:8080/';
  static OAUTH2_URL = `${AppConstants.API_BASE_URL}oauth2/authorization/`;
  static REDIRECT_URL = '?redirect_uri=http://localhost:8081/login';
  static API_URL = `${AppConstants.API_BASE_URL}api/`;
  static AUTH_API = `${AppConstants.API_URL}auth/`;
  static GOOGLE_AUTH_URL = `${AppConstants.OAUTH2_URL}google${AppConstants.REDIRECT_URL}`;
  static FACEBOOK_AUTH_URL = `${AppConstants.OAUTH2_URL}facebook${AppConstants.REDIRECT_URL}`;
  static GITHUB_AUTH_URL = `${AppConstants.OAUTH2_URL}github${AppConstants.REDIRECT_URL}`;
  static LINKEDIN_AUTH_URL = `${AppConstants.OAUTH2_URL}linkedin${AppConstants.REDIRECT_URL}`;
}
